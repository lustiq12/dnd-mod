package net.luderspieler.dnd.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class CoinBagItem extends Item {

    private static final List<String> COIN_TYPES = List.of(
        "copper_coin",
        "silver_coin",
        "electrum_coin",
        "gold_coin",
        "platinum_coin"
    );

    public CoinBagItem(Item.Properties properties) {
        super(properties.stacksTo(1));
    }

    private static String getCoinKey(ItemStack stack) {
        if (stack.isEmpty()) return null;
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        if ("dnd".equals(id.getNamespace()) && COIN_TYPES.contains(id.getPath())) {
            return id.getPath();
        }
        return null;
    }

    // Wandelt NBT-Keys wie "silver_coin" direkt in sauberen Anzeigetext um
    private static String formatCoinName(String coinKey) {
        switch (coinKey) {
            case "copper_coin": return "Copper Coin";
            case "silver_coin": return "Silver Coin";
            case "electrum_coin": return "Electrum Coin";
            case "gold_coin": return "Gold Coin";
            case "platinum_coin": return "Platinum Coin";
            default: return coinKey;
        }
    }

    private static Item getItem(String coinKey) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath("dnd", coinKey);
        Optional<Holder.Reference<Item>> holderOpt = BuiltInRegistries.ITEM.get(id);
        
        if (holderOpt.isPresent()) {
            return holderOpt.get().value();
        }
        return null;
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack bag, ItemStack carried, Slot slot, ClickAction action, Player player, SlotAccess access) {
        if (action == ClickAction.SECONDARY && !carried.isEmpty()) {
            String coinKey = getCoinKey(carried);
            if (coinKey != null) {
                int count = carried.getCount();
                addCoins(bag, coinKey, count);
                carried.shrink(count);
                
                player.playSound(SoundEvents.BUNDLE_INSERT, 0.8F, 0.8F + player.level().getRandom().nextFloat() * 0.4F);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean overrideStackedOnOther(ItemStack bag, Slot slot, ClickAction action, Player player) {
        if (action == ClickAction.SECONDARY && slot.allowModification(player)) {
            ItemStack slotStack = slot.getItem();
            if (!slotStack.isEmpty()) {
                String coinKey = getCoinKey(slotStack);
                if (coinKey != null) {
                    int count = slotStack.getCount();
                    addCoins(bag, coinKey, count);
                    slotStack.shrink(count);

                    player.playSound(SoundEvents.BUNDLE_INSERT, 0.8F, 0.8F + player.level().getRandom().nextFloat() * 0.4F);
                    return true;
                }
            } else {
                ItemStack extracted = extractCoins(bag, 64);
                if (!extracted.isEmpty()) {
                    slot.set(extracted);
                    player.playSound(SoundEvents.BUNDLE_REMOVE_ONE, 0.8F, 0.8F + player.level().getRandom().nextFloat() * 0.4F);
                    return true;
                }
            }
        }
        return false;
    }

    private static void addCoins(ItemStack bag, String coinKey, int amount) {
        CustomData.update(DataComponents.CUSTOM_DATA, bag, tag -> {
            int currentAmount = tag.getInt(coinKey).orElse(0);
            tag.putInt(coinKey, currentAmount + amount);
        });
    }

    private static ItemStack extractCoins(ItemStack bag, int maxAmount) {
        CustomData customData = bag.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CompoundTag tag = customData.copyTag();

        for (String coinKey : COIN_TYPES) {
            int amount = tag.getInt(coinKey).orElse(0);
            if (amount > 0) {
                Item item = getItem(coinKey);
                if (item != null) {
                    int toExtract = Math.min(amount, Math.min(maxAmount, item.getDefaultInstance().getMaxStackSize()));
                    int remaining = amount - toExtract;

                    CustomData.update(DataComponents.CUSTOM_DATA, bag, updatedTag -> {
                        if (remaining > 0) {
                            updatedTag.putInt(coinKey, remaining);
                        } else {
                            updatedTag.remove(coinKey);
                        }
                    });

                    return new ItemStack(item, toExtract);
                }
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipComponents, TooltipFlag flag) {
        super.appendHoverText(stack, context, display, tooltipComponents, flag);

        CustomData customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CompoundTag tag = customData.copyTag();

        boolean hasCoins = false;

        for (String coinKey : COIN_TYPES) {
            int amount = tag.getInt(coinKey).orElse(0);

            if (amount > 0) {
                hasCoins = true;
                String displayName = formatCoinName(coinKey);
                tooltipComponents.accept(Component.literal("• ")
                        .append(Component.literal(displayName))
                        .append(Component.literal(" x" + amount))
                        .withStyle(ChatFormatting.GOLD));
            }
        }

        if (!hasCoins) {
            tooltipComponents.accept(Component.literal("Empty").withStyle(ChatFormatting.GRAY));
        }
    }
}