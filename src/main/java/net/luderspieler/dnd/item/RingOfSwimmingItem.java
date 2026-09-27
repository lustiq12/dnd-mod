package net.luderspieler.dnd.item;

import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.neoforged.neoforge.common.NeoForgeMod;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.function.Consumer;

public class RingOfSwimmingItem extends Item implements ICurioItem {
    public RingOfSwimmingItem(Item.Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public void appendHoverText(ItemStack itemstack, Item.TooltipContext context, TooltipDisplay tooltipDisplay, Consumer componentConsumer, TooltipFlag flag) {
        super.appendHoverText(itemstack, context, tooltipDisplay, componentConsumer, flag);
        componentConsumer.accept(Component.translatable("item.dnd.ring_of_swimming.description_0"));
    }

    private static final ResourceLocation SWIM_MODIFIER_ID =
            ResourceLocation.fromNamespaceAndPath("dnd", "ring_of_swimming_speed");

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        AttributeInstance instance = slotContext.entity().getAttribute(NeoForgeMod.SWIM_SPEED);
        if (instance != null && instance.getModifier(SWIM_MODIFIER_ID) == null) {
            instance.addTransientModifier(new AttributeModifier(SWIM_MODIFIER_ID, 1.67, AttributeModifier.Operation.ADD_VALUE));
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        AttributeInstance instance = slotContext.entity().getAttribute(NeoForgeMod.SWIM_SPEED);
        if (instance != null) {
            instance.removeModifier(SWIM_MODIFIER_ID);
        }
    }
}