package net.luderspieler.dnd.classes;

import net.luderspieler.dnd.network.DndModVariables;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerXpEvent;

public class LevelEvents {

    @SubscribeEvent
    public void onXpChange(PlayerXpEvent.XpChange event) {
        Player player = event.getEntity();
        if (!(player instanceof ServerPlayer serverPlayer)) return;

        // Wir holen die Menge, die Minecraft hinzufügen will
        int amount = event.getAmount();

        // WICHTIG: Minecraft löst dieses Event manchmal mit 0 oder negativen Werten aus,
        // wenn die Leiste nur aktualisiert wird. Das ignorieren wir.
        if (amount <= 0) return;

        DndModVariables.PlayerVariables vars = player.getData(DndModVariables.PLAYER_VARIABLES);

        // XP addieren
        vars.PlayerXP += amount;

        // Multi-Level-Up Check
        int currentLevel = (int) vars.PlayerLevel;
        boolean leveledUp = false;

        // Wir prüfen, wie viele Level wir mit der neuen XP-Summe aufsteigen
        while (currentLevel < 20 && vars.PlayerXP >= getRequiredXP(currentLevel + 1)) {
            currentLevel++;
            leveledUp = true;
        }

        if (leveledUp) {
            vars.PlayerLevel = currentLevel;
            player.displayClientMessage(Component.literal("§6§lLEVEL UP! §fDu bist jetzt Level " + currentLevel), false);

            ClassDefinition cls = ClassRegistry.getClass(vars.PlayerClass);
            if (cls != null) {
                CharacterCreationPacket.applyAttrs(serverPlayer, cls.getAttributeModifiers(), false);
            }
        }

        vars.markSyncDirty();
    }

    private long getRequiredXP(int level) {
        // Deine Tabelle ist korrekt (D&D 5e Standard)
        return switch (level) {
            case 1 -> 0L;
            case 2 -> 300L;
            case 3 -> 900L;
            case 4 -> 2700L;
            case 5 -> 6500L;
            case 6 -> 14000L;
            case 7 -> 23000L;
            case 8 -> 34000L;
            case 9 -> 48000L;
            case 10 -> 64000L;
            case 11 -> 85000L;
            case 12 -> 100000L;
            case 13 -> 120000L;
            case 14 -> 140000L;
            case 15 -> 165000L;
            case 16 -> 195000L;
            case 17 -> 225000L;
            case 18 -> 265000L;
            case 19 -> 305000L;
            case 20 -> 355000L;
            default -> level > 20 ? 355000L + (level - 20) * 50000L : 0L;
        };
    }
}