package net.luderspieler.dnd.classes;

import net.luderspieler.dnd.network.DndModVariables;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Every second, checks if the player is wearing armor or holding a weapon
 * they are not proficient with. If so, applies Slowness I (amplifier 0).
 *
 * Proficiency tags are stored in the PlayerVariables.Proficiencys string,
 * comma-separated, e.g. "light_armor,medium_armor,simple_weapons".
 *
 * Item tags checked:
 *   Armor slots  → dnd:armor/light_armor, dnd:armor/medium_armor, dnd:armor/heavy_armor, dnd:armor/shields
 *   Main hand    → dnd:weapons/simple_weapons, dnd:weapons/war_weapons
 *
 * If a slot has an item in ANY of those tags AND the player does NOT have
 * that tag in their Proficiencys, Slowness is applied.
 * If all equipped items are either untagged (non-armor/weapon) or proficient,
 * the slowness is removed.
 */
public class ProficiencyCheckProcedure {

    // Check every 20 ticks (1 second) to avoid per-tick overhead
    private static final int CHECK_INTERVAL = 10;

    // Armor tags — items in these tags are considered "armor requiring proficiency"
    private static final TagKey<Item> TAG_LIGHT  = TagKey.create(Registries.ITEM, ResourceLocation.parse("dnd:light_armor"));
    private static final TagKey<Item> TAG_MEDIUM = TagKey.create(Registries.ITEM, ResourceLocation.parse("dnd:medium_armor"));
    private static final TagKey<Item> TAG_HEAVY  = TagKey.create(Registries.ITEM, ResourceLocation.parse("dnd:heavy_armor"));
    private static final TagKey<Item> TAG_SHIELD = TagKey.create(Registries.ITEM, ResourceLocation.parse("dnd:shields"));

    // Weapon tags
    private static final TagKey<Item> TAG_SIMPLE = TagKey.create(Registries.ITEM, ResourceLocation.parse("dnd:simple_weapons"));
    private static final TagKey<Item> TAG_WAR    = TagKey.create(Registries.ITEM, ResourceLocation.parse("dnd:war_weapons"));

    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        // Only run on server, once per second
        if (player.level().isClientSide()) return;
        if (player.tickCount % CHECK_INTERVAL != 0) return;

        // Read proficiency set
        String profStr = player.getData(DndModVariables.PLAYER_VARIABLES).Proficiencys;
        Set<String> proficiencies = parseProficiencies(profStr);

        boolean lacksProf = false;

        // ── CHECK ARMOR SLOTS ──
        for (int i = 36; i <= 39; i++) {
            ItemStack armor = player.getInventory().getItem(i);
            if (armor.isEmpty()) continue;
            if (isTagged(armor, TAG_LIGHT)  && !proficiencies.contains("light_armor"))  { lacksProf = true; break; }
            if (isTagged(armor, TAG_MEDIUM) && !proficiencies.contains("medium_armor")) { lacksProf = true; break; }
            if (isTagged(armor, TAG_HEAVY)  && !proficiencies.contains("heavy_armor"))  { lacksProf = true; break; }
            if (isTagged(armor, TAG_SHIELD) && !proficiencies.contains("shields"))      { lacksProf = true; break; }
        }

        // ── CHECK OFFHAND FOR SHIELD ──
        if (!lacksProf) {
            ItemStack offhand = player.getOffhandItem();
            if (!offhand.isEmpty() && isTagged(offhand, TAG_SHIELD) && !proficiencies.contains("shields")) {
                lacksProf = true;
            }
        }

        // ── CHECK MAIN HAND FOR WEAPON ──
        if (!lacksProf) {
            ItemStack mainhand = player.getMainHandItem();
            if (!mainhand.isEmpty()) {
                if (isTagged(mainhand, TAG_WAR)    && !proficiencies.contains("war_weapons"))    lacksProf = true;
                if (isTagged(mainhand, TAG_SIMPLE) && !proficiencies.contains("simple_weapons")) lacksProf = true;
            }
        }

        if (lacksProf) {
            // Apply Slowness I (amplifier 0 = Slowness I in-game), 30 ticks = 1.5s
            // Re-applied every second so it persists as long as the item is worn
            player.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 25, 2, false, false, false));
        } else {
            // Remove the effect if they fixed their equipment
            // Only remove if the cause was us (amplifier 0, non-ambient)
            MobEffectInstance existing = player.getEffect(MobEffects.SLOWNESS);
            if (existing != null && existing.getAmplifier() == 0 && !existing.isAmbient()) {
                player.removeEffect(MobEffects.SLOWNESS);
            }
        }
    }

    private static boolean isTagged(ItemStack stack, TagKey<Item> tag) {
        return stack.is(tag);
    }

    private static Set<String> parseProficiencies(String profStr) {
        Set<String> set = new HashSet<>();
        if (profStr == null || profStr.isBlank()) return set;
        for (String p : profStr.split(",")) {
            String t = p.trim();
            if (!t.isEmpty()) set.add(t);
        }
        return set;
    }
}