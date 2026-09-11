package net.luderspieler.dnd.init;

import net.luderspieler.dnd.DndMod;
import net.luderspieler.dnd.npc.NpcTradeMenu;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;

public class DndModMenus {
    public static final DeferredRegister<MenuType<?>> REGISTRY =
            DeferredRegister.create(Registries.MENU, DndMod.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<NpcTradeMenu>> NPC_TRADE_MENU =
            REGISTRY.register("npc_trade_menu", () -> IMenuTypeExtension.create(NpcTradeMenu::new));
}