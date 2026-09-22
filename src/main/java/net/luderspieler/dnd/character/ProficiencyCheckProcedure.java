package net.luderspieler.dnd.character;

import net.luderspieler.dnd.network.DndModVariables;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.HashSet;
import java.util.Set;

/**
 * Every second, checks if the player is wearing armor or holding a weapon
 * they are not proficient with. If so, applies Slowness I (amplifier 0).
 */
public class ProficiencyCheckProcedure {

    private static final int CHECK_INTERVAL = 10;
    private static final int SLOWNESS_AMPLIFIER = 0; // Slowness I

    // Armor tags
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

        if (player.level().isClientSide()) return;
        if (player.tickCount % CHECK_INTERVAL != 0) return;

        Set<String> proficiencies = getProficiencies(player);
        boolean lacksProf = false;

        // ── CHECK ARMOR SLOTS ──
        for (int i = 36; i <= 39; i++) {
            ItemStack armor = player.getInventory().getItem(i);
            if (!isProficient(armor, proficiencies)) {
                lacksProf = true;
                break;
            }
        }

        // ── CHECK OFFHAND FOR SHIELD ──
        if (!lacksProf) {
            ItemStack offhand = player.getOffhandItem();
            if (!isProficient(offhand, proficiencies)) {
                lacksProf = true;
            }
        }

        // ── CHECK MAIN HAND FOR WEAPON ──
        if (!lacksProf) {
            ItemStack mainhand = player.getMainHandItem();
            if (!isProficient(mainhand, proficiencies)) {
                lacksProf = true;
            }
        }

        if (lacksProf) {
            player.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 25, SLOWNESS_AMPLIFIER, false, false, false));
        } else {
            MobEffectInstance existing = player.getEffect(MobEffects.SLOWNESS);
            if (existing != null && existing.getAmplifier() == SLOWNESS_AMPLIFIER && !existing.isAmbient()) {
                player.removeEffect(MobEffects.SLOWNESS);
            }
        }
    }

    /**
     * Checks if a player is proficient with a given ItemStack.
     */
    public static boolean isProficient(Player player, ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return true;
        }
        return isProficient(stack, getProficiencies(player));
    }

    /**
     * Checks proficiency against an already parsed set of proficiencies.
     */
    public static boolean isProficient(ItemStack stack, Set<String> proficiencies) {
        if (stack == null || stack.isEmpty()) {
            return true;
        }

        if (isTagged(stack, TAG_LIGHT)  && !proficiencies.contains("light_armor"))   return false;
        if (isTagged(stack, TAG_MEDIUM) && !proficiencies.contains("medium_armor"))  return false;
        if (isTagged(stack, TAG_HEAVY)  && !proficiencies.contains("heavy_armor"))   return false;
        if (isTagged(stack, TAG_SHIELD) && !proficiencies.contains("shields"))       return false;
        if (isTagged(stack, TAG_SIMPLE) && !proficiencies.contains("simple_weapons")) return false;
        if (isTagged(stack, TAG_WAR)    && !proficiencies.contains("war_weapons"))    return false;

        return true;
    }

    public static Set<String> getProficiencies(Player player) {
        String profStr = player.getData(DndModVariables.PLAYER_VARIABLES).Proficiencys;
        return parseProficiencies(profStr);
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