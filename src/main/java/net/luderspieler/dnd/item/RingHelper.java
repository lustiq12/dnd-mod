package net.luderspieler.dnd.item;

import net.luderspieler.dnd.init.DndModItems;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.function.Predicate;

public class RingHelper {

    public static boolean isEquipped(Player player, String slotIdentifier, Predicate<ItemStack> filter) {
        if (player == null) return false;
        try {
            return CuriosApi.getCuriosInventory(player)
                    .map(h -> slotIdentifier == null || slotIdentifier.isEmpty()
                            ? h.findFirstCurio(filter).isPresent()
                            : h.findCurios(slotIdentifier).stream().anyMatch(r -> filter.test(r.stack())))
                    .orElse(false);
        } catch (Throwable ignored) {
            return false; // Fallback if Curios is not loaded
        }
    }

    @SubscribeEvent
    public void onDamageTaken(LivingIncomingDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        // Apply 10% damage reduction if Ring of Protection is equipped
        if (isEquipped(player, "ring", stack -> stack.is(DndModItems.RING_OF_PROTECTION.get()))) {
            event.setAmount(event.getAmount() * 0.9f);
        }
    }
}
