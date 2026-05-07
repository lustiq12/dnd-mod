package net.luderspieler.dnd.rests;

import net.luderspieler.dnd.classes.ClassDefinition;
import net.luderspieler.dnd.classes.ClassRegistry;
import net.luderspieler.dnd.network.DndModVariables;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerWakeUpEvent;

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
                    serverPlayer.displayClientMessage(Component.literal("Deine lange Rast war erfolgreich!"), false);
                } else {
                    // ── RAST UNTERBROCHEN ──
                    serverPlayer.displayClientMessage(Component.literal("Die Rast wurde unterbrochen. Deine Kräfte sind noch nicht regeneriert."), false);
                }
            }
        }
    }

    private void applyLongRestBenefits(ServerPlayer player) {
        // HP heilen
        DndModVariables.PlayerVariables vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        
        int level = (int)vars.PlayerLevel;

        ClassDefinition cls = ClassRegistry.getClass(vars.PlayerClass);
        vars.Spellslots = formatSpellSlots(cls, level);
        player.setHealth(player.getMaxHealth());
        ClientAccess.openLongRestScreen();

        vars.markSyncDirty();
    }

    public static String formatSpellSlots(ClassDefinition cls, int level) {
        // Falls die Klasse keine Magie hat (NONE_S), wird ein leerer Slot-String geliefert
        int[][] slotTable = cls.getSpellSlots(); // Holt z.B. WIZ_S

        if (slotTable == null || level < 0 || level >= slotTable.length) {
            return "000000000";
        }

        int[] slotsAtLevel = slotTable[level];
        StringBuilder sb = new StringBuilder();

        // D&D Spell-Levels 1 bis 9 (Index 1-9 im Array)
        for (int i = 1; i <= 9; i++) {
            sb.append(slotsAtLevel[i]);
        }

        return sb.toString();
    }

    private static class ClientAccess {
        public static void openLongRestScreen() {
            var mc = net.minecraft.client.Minecraft.getInstance();
            // Hier deinen neuen Screen öffnen
            mc.setScreen(new LongRestScreen());
        }
    }
}