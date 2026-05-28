package net.luderspieler.dnd;

import net.luderspieler.dnd.character.AbilitysAndFeats.ActivateAbilityPacket;
import net.luderspieler.dnd.character.network.CharacterCreationPacket;
import net.luderspieler.dnd.character.network.KeepCharacterPacket;
import net.luderspieler.dnd.character.network.OpenCreationGuiPacket;
import net.luderspieler.dnd.character.choices.ExecuteChoicePacket;
import net.luderspieler.dnd.network.AirClickPacket;
import net.luderspieler.dnd.spells.CastSpellPacket;
import net.luderspieler.dnd.spells.PrepareSpellsPacket;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

/**
 * Call DndModNetworkRegistry.register(modEventBus) from your main mod constructor.
 *
 * ProficiencyCheckProcedure is annotated with @EventBusSubscriber so it
 * registers itself automatically — do NOT add it here.
 */
public class DndModNetworkRegistry {

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(DndModNetworkRegistry::onRegisterPayloads);
    }

    private static void onRegisterPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar reg = event.registrar("1");

        // ── Client → Server ──────────────────────────────────────
        reg.playToServer(
                CharacterCreationPacket.TYPE,
                CharacterCreationPacket.CODEC,
                CharacterCreationPacket::handle
        );

        reg.playToServer(
                KeepCharacterPacket.TYPE,
                KeepCharacterPacket.CODEC,
                KeepCharacterPacket::handle
        );

        reg.playToServer(
                CastSpellPacket.TYPE,
                CastSpellPacket.CODEC,
                CastSpellPacket::handle
        );

        reg.playToServer(
                PrepareSpellsPacket.TYPE,
                PrepareSpellsPacket.CODEC,
                PrepareSpellsPacket::handle
        );

        reg.playToServer(
                AirClickPacket.TYPE,
                AirClickPacket.CODEC,
                AirClickPacket::handle
        );

        reg.playToServer(
                ExecuteChoicePacket.TYPE,
                ExecuteChoicePacket.CODEC,
                ExecuteChoicePacket::handle
        );

        reg.playToServer(
                ActivateAbilityPacket.TYPE,
                ActivateAbilityPacket.CODEC,
                ActivateAbilityPacket::handle
        );

        // ── Server → Client ──────────────────────────────────────
        reg.playToClient(
                OpenCreationGuiPacket.TYPE,
                OpenCreationGuiPacket.CODEC,
                OpenCreationGuiPacket::handle
        );
    }
}