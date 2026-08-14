package net.luderspieler.dnd.debug;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.neoforge.common.NeoForgeMod;

import java.util.ArrayList;
import java.util.List;

/** Reads the real AttributeInstance modifiers this mod assigns, for the debug attribute breakdown. */
public class DebugAttributeInspector {

    // Every attribute this mod actually places modifiers on, taken from
    // CharacterCreationPacket.applyAttrs and the mod's MobEffect classes.
    private static final List<Holder<Attribute>> TRACKED = List.of(
            Attributes.MAX_HEALTH,
            Attributes.ARMOR,
            Attributes.ATTACK_DAMAGE,
            Attributes.ATTACK_SPEED,
            Attributes.ATTACK_KNOCKBACK,
            Attributes.KNOCKBACK_RESISTANCE,
            Attributes.MOVEMENT_SPEED,
            Attributes.SNEAKING_SPEED,
            Attributes.JUMP_STRENGTH,
            Attributes.BLOCK_BREAK_SPEED,
            Attributes.MINING_EFFICIENCY,
            Attributes.BLOCK_INTERACTION_RANGE,
            Attributes.ENTITY_INTERACTION_RANGE,
            Attributes.SUBMERGED_MINING_SPEED,
            Attributes.STEP_HEIGHT,
            Attributes.SAFE_FALL_DISTANCE,
            Attributes.FALL_DAMAGE_MULTIPLIER,
            Attributes.OXYGEN_BONUS,
            Attributes.BURNING_TIME,
            Attributes.LUCK,
            Attributes.TEMPT_RANGE,
            Attributes.SCALE,
            Attributes.GRAVITY,
            NeoForgeMod.SWIM_SPEED,
            NeoForgeMod.NAMETAG_DISTANCE
    );

    public static DebugAttributesResponsePacket buildResponse(ServerPlayer target) {
        List<DebugAttributesClientState.AttributeEntry> entries = new ArrayList<>();

        for (Holder<Attribute> holder : TRACKED) {
            AttributeInstance instance = target.getAttribute(holder);
            if (instance == null) continue;

            ResourceLocation attrId = instance.getAttribute().unwrapKey()
                    .map(ResourceKey::location).orElse(null);
            if (attrId == null) continue;

            List<DebugAttributesClientState.ModifierEntry> modifiers = new ArrayList<>();
            for (AttributeModifier modifier : instance.getModifiers()) {
                modifiers.add(new DebugAttributesClientState.ModifierEntry(
                        modifier.id().toString(), modifier.amount(), modifier.operation().name()));
            }

            entries.add(new DebugAttributesClientState.AttributeEntry(
                    attrId.toString(), instance.getBaseValue(), instance.getValue(), modifiers));
        }

        return new DebugAttributesResponsePacket(target.getStringUUID(), entries);
    }
}