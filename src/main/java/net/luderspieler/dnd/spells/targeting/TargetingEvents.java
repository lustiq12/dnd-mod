package net.luderspieler.dnd.spells.targeting;

import net.luderspieler.dnd.network.DndModVariables;
import net.luderspieler.dnd.spells.SpellCasters;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber
public class TargetingEvents {

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            var vars = player.getData(net.luderspieler.dnd.network.DndModVariables.PLAYER_VARIABLES);
            if (vars.TargetingMode) {
                RaycastHelper.renderPreview(player, vars.TargetingRange);
            }
        }
    }

    // Feuert, wenn man in die Luft klickt (Client-to-Server Fix notwendig)
    @SubscribeEvent
    public static void onRightClickEmpty(PlayerInteractEvent.RightClickEmpty event) {
        // Hinweis: Dieses Event feuert standardmäßig NUR auf dem Client.
        // Für eine leere Hand in der Luft muss ggf. ein Packet gesendet werden.
        handleCasting(event.getEntity(), event.getHand());
    }

    // Feuert, wenn man auf einen Block klickt
    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        handleCasting(event.getEntity(), event.getHand());
    }

    // Feuert, wenn man direkt auf ein Entity klickt[cite: 3]
    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        handleCasting(event.getEntity(), event.getHand());
    }

    private static void handleCasting(net.minecraft.world.entity.player.Player p, InteractionHand hand) {
        if (p instanceof ServerPlayer player && hand == InteractionHand.MAIN_HAND && player.getMainHandItem().isEmpty()) {
            var vars = player.getData(net.luderspieler.dnd.network.DndModVariables.PLAYER_VARIABLES);
            if (vars.TargetingMode) {
                RaycastHelper.tryPickTarget(player, vars.TargetingRange, (int) vars.TargetingAmount, (targets) -> {
                    for (LivingEntity target : targets) {
                        switch (vars.TargetingSpell) {
                            case "CURE_WOUNDS" -> SpellCasters.castCureWounds(player, target);
                            case "HEALING_WORD" -> SpellCasters.castHealingWord(player, target);
                            case "RESTORATION" -> SpellCasters.castRestoration(player, target);
                            case "AID" -> SpellCasters.castAid(player, target);
                            case "INFLICT_WOUNDS" -> SpellCasters.castInflictWounds(player, target);
                            case "BLIGHT" -> SpellCasters.castBlight(player, target);
                            case "HOLD_PERSON" -> SpellCasters.castHoldPerson(player, target);
                            case "BESTOW_CURSE" -> SpellCasters.castBestowCurse(player, target);
                        }
                    }
                    vars.TargetingMode = false; // Modus beenden
                    vars.markSyncDirty(); // Synchronisieren
                });
            }
        }
    }
}