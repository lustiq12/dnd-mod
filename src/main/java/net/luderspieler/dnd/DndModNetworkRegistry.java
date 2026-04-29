package net.luderspieler.dnd;

import net.luderspieler.dnd.classes.CharacterCreationEventHandler;
import net.luderspieler.dnd.classes.CharacterCreationPacket;
import net.luderspieler.dnd.classes.KeepCharacterPacket;
import net.luderspieler.dnd.classes.OpenCreationGuiPacket;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

/**
 * Call DndModNetworkRegistry.register(modEventBus) from your main mod constructor.
 *
 * This registers:
 *  - All custom network packets
 *  - The CharacterCreationEventHandler on the NeoForge event bus
 */
public class DndModNetworkRegistry {

    public static void register(IEventBus modEventBus) {
        // Packets
        modEventBus.addListener(DndModNetworkRegistry::onRegisterPayloads);

        // Server-side game events (join, respawn)
        NeoForge.EVENT_BUS.register(new CharacterCreationEventHandler());
    }

    private static void onRegisterPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar reg = event.registrar("1");

        // Client → Server
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

        // Server → Client
        reg.playToClient(
                OpenCreationGuiPacket.TYPE,
                OpenCreationGuiPacket.CODEC,
                OpenCreationGuiPacket::handle
        );
    }
}