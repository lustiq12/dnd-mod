package net.luderspieler.dnd.rests;

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
        player.setHealth(player.getMaxHealth());
        ClientAccess.openLongRestScreen();
    }

    private static class ClientAccess {
        public static void openLongRestScreen() {
            var mc = net.minecraft.client.Minecraft.getInstance();
            // Hier deinen neuen Screen öffnen
            mc.setScreen(new LongRestScreen());
        }
    }
}