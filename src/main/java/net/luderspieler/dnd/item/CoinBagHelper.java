package net.luderspieler.dnd.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.TriState;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.ArrayList;
import java.util.List;

public class CoinBagHelper {

    public enum CoinType {
        COPPER("copper_coin", 1),
        SILVER("silver_coin", 10),
        ELECTRUM("electrum_coin", 50),
        GOLD("gold_coin", 100),
        PLATINUM("platinum_coin", 1000);

        private final String tagKey;
        private final int copperValue;

        CoinType(String tagKey, int copperValue) {
            this.tagKey = tagKey;
            this.copperValue = copperValue;
        }

        public String getTagKey() {
            return tagKey;
        }

        public int getCopperValue() {
            return copperValue;
        }
    }

    // ── Data Component Utilities ─────────────────────────────────────────────

    public static int getCoins(ItemStack bagStack, CoinType type) {
        if (bagStack == null || bagStack.isEmpty()) return 0;
        CustomData customData = bagStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CompoundTag tag = customData.copyTag();
        return tag.getInt(type.getTagKey()).orElse(0);
    }

    public static void addCoins(ItemStack bagStack, CoinType type, int amount) {
        if (bagStack == null || bagStack.isEmpty() || amount <= 0) return;
        CustomData.update(DataComponents.CUSTOM_DATA, bagStack, tag -> {
            int current = tag.getInt(type.getTagKey()).orElse(0);
            tag.putInt(type.getTagKey(), current + amount);
        });
    }

    public static boolean removeCoinsFromBag(ItemStack bagStack, CoinType type, int amount) {
        if (bagStack == null || bagStack.isEmpty() || amount <= 0) return false;
        int current = getCoins(bagStack, type);
        if (current < amount) return false;

        CustomData.update(DataComponents.CUSTOM_DATA, bagStack, tag -> {
            tag.putInt(type.getTagKey(), current - amount);
        });
        return true;
    }

    // ── Bag Lookup (Curios Belt Slot) ────────────────────────────────────────

    public static List<ItemStack> findCoinBags(Player player) {
        List<ItemStack> bags = new ArrayList<>();

        try {
            CuriosApi.getCuriosInventory(player).ifPresent(handler -> {
                handler.findCurios("belt").forEach(slotResult -> {
                    ItemStack stack = slotResult.stack();
                    if (isCoinBag(stack)) {
                        bags.add(stack);
                    }
                });
            });
        } catch (Throwable ignored) {
            // Fallback if Curios is missing
        }

        return bags;
    }

    private static boolean isCoinBag(ItemStack stack) {
        return stack != null && !stack.isEmpty() && stack.getItem() instanceof CoinBagItem;
    }

    // ── Payment Processing ───────────────────────────────────────────────────

    public static boolean payCoins(Player player, CoinType type, int amount) {
        if (amount <= 0) return true;

        List<ItemStack> bags = findCoinBags(player);
        if (bags.isEmpty()) return false;

        // 1. Exact payment
        int remainingToPay = amount;
        for (ItemStack bag : bags) {
            int available = getCoins(bag, type);
            if (available > 0) {
                int toDeduct = Math.min(available, remainingToPay);
                removeCoinsFromBag(bag, type, toDeduct);
                remainingToPay -= toDeduct;
                if (remainingToPay == 0) return true;
            }
        }

        // 2. Value-based payment with change
        int requiredCopperValue = remainingToPay * type.getCopperValue();
        int totalCopperAvailable = getTotalCopperValue(bags);

        if (totalCopperAvailable < requiredCopperValue) {
            return false;
        }

        clearAllCoins(bags);
        int changeInCopper = totalCopperAvailable - requiredCopperValue;
        depositCopperValue(bags.get(0), changeInCopper);

        return true;
    }

    // ── Currency Conversion Helpers ──────────────────────────────────────────

    public static int getTotalCopperValue(List<ItemStack> bags) {
        int total = 0;
        for (ItemStack bag : bags) {
            for (CoinType type : CoinType.values()) {
                total += getCoins(bag, type) * type.getCopperValue();
            }
        }
        return total;
    }

    private static void clearAllCoins(List<ItemStack> bags) {
        for (ItemStack bag : bags) {
            CustomData.update(DataComponents.CUSTOM_DATA, bag, tag -> {
                for (CoinType type : CoinType.values()) {
                    tag.putInt(type.getTagKey(), 0);
                }
            });
        }
    }

    private static void depositCopperValue(ItemStack bag, int totalCopper) {
        int platinum = totalCopper / CoinType.PLATINUM.getCopperValue();
        totalCopper %= CoinType.PLATINUM.getCopperValue();

        int gold = totalCopper / CoinType.GOLD.getCopperValue();
        totalCopper %= CoinType.GOLD.getCopperValue();

        int electrum = totalCopper / CoinType.ELECTRUM.getCopperValue();
        totalCopper %= CoinType.ELECTRUM.getCopperValue();

        int silver = totalCopper / CoinType.SILVER.getCopperValue();
        int copper = totalCopper % CoinType.SILVER.getCopperValue();

        if (platinum > 0) addCoins(bag, CoinType.PLATINUM, platinum);
        if (gold > 0) addCoins(bag, CoinType.GOLD, gold);
        if (electrum > 0) addCoins(bag, CoinType.ELECTRUM, electrum);
        if (silver > 0) addCoins(bag, CoinType.SILVER, silver);
        if (copper > 0) addCoins(bag, CoinType.COPPER, copper);
    }

    // ── Event Listener: Auto Item Pickup ─────────────────────────────────────

    @SubscribeEvent
    public void onItemPickup(ItemEntityPickupEvent.Pre event) {
        ItemEntity itemEntity = event.getItemEntity();
        if (itemEntity == null || !itemEntity.isAlive()) return;

        if (itemEntity.hasPickUpDelay()) return;

        ItemStack stack = itemEntity.getItem();
        if (stack.isEmpty()) return;

        CoinType coinType = getCoinTypeFromItem(stack);
        if (coinType == null) return;

        Player player = event.getPlayer();
        if (player == null) return;

        List<ItemStack> bags = findCoinBags(player);
        if (bags.isEmpty()) return;

        // Prevent vanilla item pickup behavior
        event.setCanPickup(TriState.FALSE);

        if (!player.level().isClientSide()) {
            CuriosApi.getCuriosInventory(player).ifPresent(handler -> {
                handler.findCurios("belt").stream().findFirst().ifPresent(slotResult -> {
                    ItemStack bagStack = slotResult.stack();
                    if (isCoinBag(bagStack)) {
                        int count = stack.getCount();

                        // Add coins cleanly to NBT
                        addCoins(bagStack, coinType, count);

                        // Sync Curios slot stack
                        handler.setEquippedCurio(
                                slotResult.slotContext().identifier(),
                                slotResult.slotContext().index(),
                                bagStack
                        );

                        player.containerMenu.broadcastChanges();

                        player.level().playSound(
                                null, player.getX(), player.getY(), player.getZ(),
                                SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS,
                                0.2F, (player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.2F + 1.4F
                        );

                        itemEntity.discard();
                    }
                });
            });
        }
    }

    private static CoinType getCoinTypeFromItem(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return null;

        String path = BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath().toLowerCase();

        if (path.contains("copper")) return CoinType.COPPER;
        if (path.contains("silver")) return CoinType.SILVER;
        if (path.contains("electrum")) return CoinType.ELECTRUM;
        if (path.contains("gold")) return CoinType.GOLD;
        if (path.contains("platinum")) return CoinType.PLATINUM;

        return null;
    }
}