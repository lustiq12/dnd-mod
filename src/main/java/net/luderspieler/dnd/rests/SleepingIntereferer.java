package net.luderspieler.dnd.rests;

import net.luderspieler.dnd.character.AbilitysAndFeats.management.Ability;
import net.luderspieler.dnd.character.AbilitysAndFeats.management.AbilityDataUtils;
import net.luderspieler.dnd.character.AbilitysAndFeats.management.AbilityResetRegistry;
import net.luderspieler.dnd.character.AbilitysAndFeats.management.AbilityUtils;
import net.luderspieler.dnd.character.definition.ClassDefinition;
import net.luderspieler.dnd.character.registrys.ClassRegistry;
import net.luderspieler.dnd.network.DndModVariables;
import net.luderspieler.dnd.resources.ResourceManager;
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

        if (level.isClientSide()) return;
        if (!(player instanceof ServerPlayer serverPlayer)) return;

        long timeOfDay = level.getDayTime() % 24000;
        boolean successfulRest = timeOfDay < 12000;

        if (successfulRest) {
            applyLongRestBenefits(serverPlayer);
            serverPlayer.displayClientMessage(
                    Component.literal("§aYour long rest was successful!"), false);
        } else {
            serverPlayer.displayClientMessage(
                    Component.literal("§cYour rest was interrupted — no benefits gained."), false);
        }
    }

    private void applyLongRestBenefits(ServerPlayer player) {
        DndModVariables.PlayerVariables vars = player.getData(DndModVariables.PLAYER_VARIABLES);

        // 1. Spell Slots zurücksetzen
        ClassDefinition cls = ClassRegistry.getClass(vars.PlayerClass);
        vars.Spellslots = resetSpellSlots(cls, (int) vars.PlayerLevel);

        // 2. HP voll
        player.setHealth(player.getMaxHealth());

        // 3. Alle Ability-Ladungen (Long + Short Rest) auffüllen + Flags bereinigen
        ResourceManager.resetForLongRest(player);

        // 4. Spezies-spezifische Long-Rest-Boni ──────────────────────────────

        // RESOURCEFUL (Human 2024) — Heroic Inspiration nach Long Rest
        // Heroic Inspiration erlaubt dem Spieler Advantage auf einen beliebigen d20-Test.
        // Gespeichert als "HEROIC_INSPIRATION" in AbilityData (1 = verfügbar, 0 = verbraucht).
        if (AbilityUtils.hasAbility(player, Ability.RESOURCEFUL)) {
            AbilityDataUtils.set(vars, "HEROIC_INSPIRATION", 1);
            vars.markSyncDirty();
            player.displayClientMessage(
                    Component.literal("§6[Resourceful] §eYou feel inspired!"), false);
        }
        vars.markSyncDirty();

        // 5. Long Rest Screen öffnen (Zauber vorbereiten etc.)
        ClientAccess.openLongRestScreen();
    }

    private static class ClientAccess {
        public static void openLongRestScreen() {
            net.minecraft.client.Minecraft.getInstance().setScreen(new LongRestScreen());
        }
    }
}