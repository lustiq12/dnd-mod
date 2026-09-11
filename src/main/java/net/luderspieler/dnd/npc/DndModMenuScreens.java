package net.luderspieler.dnd.npc;

import net.luderspieler.dnd.init.DndModMenus;
import net.luderspieler.dnd.npc.screens.NpcTradeScreen;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

public class DndModMenuScreens {

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(DndModMenus.NPC_TRADE_MENU.get(), NpcTradeScreen::new);
    }
}