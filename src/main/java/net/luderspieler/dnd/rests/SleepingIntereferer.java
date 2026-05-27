package net.luderspieler.dnd.rests;

import net.luderspieler.dnd.character.AbilitysAndFeats.management.AbilityDataUtils;
import net.luderspieler.dnd.character.AbilitysAndFeats.management.AbilityResetRegistry;
import net.luderspieler.dnd.character.definition.ClassDefinition;
import net.luderspieler.dnd.character.registrys.ClassRegistry;
import net.luderspieler.dnd.network.DndModVariables;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerWakeUpEvent;

import static net.luderspieler.dnd.character.network.CharacterCreationPacket.resetSpellSlots;

public class SleepingIntereferer {

    @SubscribeEvent
    public void onWakeUp(PlayerWakeUpEvent event) {
        Player player = event.getEntity();
        Level level = player.level();

        if (!level.isClientSide()) {
            if (player instanceof ServerPlayer serverPlayer) {

                // Wir prüfen die Tageszeit direkt (0 - 24000 Ticks)
                // 0 bis ca. 12000 ist Tag in Minecraft.
                long timeOfDay = level.getDayTime() % 24000;
                boolean isActuallyDay = timeOfDay < 12000;

                if (isActuallyDay) {
                    // ── LANGE RAST ERFOLGREICH ──
                    this.applyLongRestBenefits(serverPlayer);
                    serverPlayer.displayClientMessage(Component.literal("Your long rest was successful!"), false);
                } else {
                    // ── RAST UNTERBROCHEN ──
                    serverPlayer.displayClientMessage(Component.literal("Your Rest was interrupted, you didnt gain the benefits."), false);
                }
            }
        }
    }

    private void applyLongRestBenefits(ServerPlayer player) {
        DndModVariables.PlayerVariables vars = player.getData(DndModVariables.PLAYER_VARIABLES);

        int level = (int) vars.PlayerLevel;
        ClassDefinition cls = ClassRegistry.getClass(vars.PlayerClass);
        vars.Spellslots = resetSpellSlots(cls, level);
        player.setHealth(player.getMaxHealth());

        // ── NEU: Registry aufrufen statt manuell zu löschen ──
        // Das löscht alte Flags UND berechnet alle max_uses für Long & Short Rest neu!
        AbilityResetRegistry.resetOnLongRest(player);

        ClientAccess.openLongRestScreen();
        vars.markSyncDirty();
    }

    private static class ClientAccess {
        public static void openLongRestScreen() {
            var mc = net.minecraft.client.Minecraft.getInstance();
            // Hier deinen neuen Screen öffnen
            mc.setScreen(new LongRestScreen());
        }
    }
}