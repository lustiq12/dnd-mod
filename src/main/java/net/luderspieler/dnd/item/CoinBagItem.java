package net.luderspieler.dnd.item;

import net.luderspieler.dnd.network.CoinBagScrollPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
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
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@EventBusSubscriber(modid = "dnd", value = Dist.CLIENT)
public class CoinBagItem extends Item {

    public static final List<String> COIN_TYPES = List.of(
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
        return holderOpt.map(Holder.Reference::value).orElse(null);
    }

    public static int getSelectedIndex(ItemStack bag) {
        CustomData customData = bag.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CompoundTag tag = customData.copyTag();
        return tag.getInt("selected_index").orElse(0);
    }

    public static void setSelectedIndex(ItemStack bag, int index) {
        CustomData.update(DataComponents.CUSTOM_DATA, bag, tag -> {
            tag.putInt("selected_index", index);
        });
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

        int selectedIndex = getSelectedIndex(bag);
        if (selectedIndex < 0 || selectedIndex >= COIN_TYPES.size()) {
            selectedIndex = 0;
        }

        String selectedCoinKey = COIN_TYPES.get(selectedIndex);
        int amount = tag.getInt(selectedCoinKey).orElse(0);

        if (amount > 0) {
            Item item = getItem(selectedCoinKey);
            if (item != null) {
                int toExtract = Math.min(amount, Math.min(maxAmount, item.getDefaultInstance().getMaxStackSize()));
                int remaining = amount - toExtract;

                CustomData.update(DataComponents.CUSTOM_DATA, bag, updatedTag -> {
                    if (remaining > 0) {
                        updatedTag.putInt(selectedCoinKey, remaining);
                    } else {
                        updatedTag.remove(selectedCoinKey);
                    }
                });

                return new ItemStack(item, toExtract);
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipComponents, TooltipFlag flag) {
        super.appendHoverText(stack, context, display, tooltipComponents, flag);

        CustomData customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CompoundTag tag = customData.copyTag();

        int selectedIndex = getSelectedIndex(stack);

        // Prüfen, ob mindestens eine Münze im Beutel ist
        boolean hasAnyCoins = false;
        for (String coinKey : COIN_TYPES) {
            if (tag.getInt(coinKey).orElse(0) > 0) {
                hasAnyCoins = true;
                break;
            }
        }

        if (hasAnyCoins) {
            // Alle Münztypen anzeigen (auch mit x0)
            for (int i = 0; i < COIN_TYPES.size(); i++) {
                String coinKey = COIN_TYPES.get(i);
                int amount = tag.getInt(coinKey).orElse(0);
                boolean isSelected = (i == selectedIndex);

                String prefix = isSelected ? "> " : "  ";
                String displayName = formatCoinName(coinKey);
                ChatFormatting color = isSelected ? ChatFormatting.YELLOW : ChatFormatting.GOLD;

                tooltipComponents.accept(Component.literal(prefix + "• ")
                        .append(Component.literal(displayName))
                        .append(Component.literal(" x" + amount))
                        .withStyle(color));
            }
        } else {
            tooltipComponents.accept(Component.literal("Empty").withStyle(ChatFormatting.GRAY));
        }

        if (Screen.hasShiftDown()) {
            tooltipComponents.accept(Component.literal(""));
            tooltipComponents.accept(Component.literal("Info:").withStyle(ChatFormatting.GRAY, ChatFormatting.UNDERLINE));
            tooltipComponents.accept(Component.literal("• Scroll to select coin type").withStyle(ChatFormatting.GRAY));
            tooltipComponents.accept(Component.literal("• Deposit: Right-click bag on coins or coins on bag").withStyle(ChatFormatting.GRAY));
            tooltipComponents.accept(Component.literal("• Withdraw: Right-click bag on an empty slot").withStyle(ChatFormatting.GRAY));
        } else {
            tooltipComponents.accept(Component.literal("[Shift] for more info").withStyle(ChatFormatting.DARK_GRAY));
        }
    }

    // ── SCROLL EVENTS ─────────────────────────────────────────────────────────

    @SubscribeEvent
    public static void onScreenMouseScroll(ScreenEvent.MouseScrolled.Pre event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.screen instanceof AbstractContainerScreen<?> containerScreen) {
            Slot slot = containerScreen.getSlotUnderMouse();
            if (slot != null && slot.hasItem() && slot.getItem().getItem() instanceof CoinBagItem) {
                double delta = event.getScrollDeltaY();
                if (delta != 0) {
                    handleScroll(slot.getItem(), slot.index, delta > 0);
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onInGameMouseScroll(InputEvent.MouseScrollingEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.screen == null && mc.player != null && Screen.hasShiftDown()) {
            ItemStack mainHand = mc.player.getMainHandItem();
            if (mainHand.getItem() instanceof CoinBagItem) {
                double delta = event.getScrollDeltaY();
                if (delta != 0) {
                    int slotId = 36 + mc.player.getInventory().getSelectedSlot();
                    handleScroll(mainHand, slotId, delta > 0);
                    event.setCanceled(true);
                }
            }
        }
    }

    private static void handleScroll(ItemStack bag, int slotId, boolean scrollUp) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        CustomData customData = bag.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CompoundTag tag = customData.copyTag();

        // Erst scrollen erlauben, wenn mindestens eine Münze im Beutel ist
        boolean hasAnyCoins = false;
        for (String coinKey : COIN_TYPES) {
            if (tag.getInt(coinKey).orElse(0) > 0) {
                hasAnyCoins = true;
                break;
            }
        }

        if (!hasAnyCoins) return;

        int currentIndex = getSelectedIndex(bag);
        int totalTypes = COIN_TYPES.size();
        int newIndex = scrollUp ? (currentIndex - 1 + totalTypes) % totalTypes : (currentIndex + 1) % totalTypes;

        if (newIndex != currentIndex) {
            setSelectedIndex(bag, newIndex);
            CoinBagScrollPacket.send(slotId, newIndex);
            mc.player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 0.2F, 1.5F);
        }
    }
}