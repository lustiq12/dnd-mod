package net.luderspieler.dnd.gameplay;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

import static net.luderspieler.dnd.character.ProficiencyCheckProcedure.isProficient;

public class ToolTipModifier {

    @SubscribeEvent
    public void onTooltipDisplay(ItemTooltipEvent event) {
        Player player = event.getEntity();
        if (player == null) return;
        ItemStack item = event.getItemStack();

        if (!isProficient(player, item)) {
            event.getToolTip().add(
                    Component.literal("You are not proficient with this item.")
                            .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC)
            );
        }
    }
}