package net.luderspieler.dnd.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;

public class DndModDamageTypes {
    public static final ResourceKey<DamageType> FIRE = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.parse("dnd:fire"));
    public static final ResourceKey<DamageType> COLD = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.parse("dnd:cold"));
    public static final ResourceKey<DamageType> LIGHTNING = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.parse("dnd:lightning"));
    public static final ResourceKey<DamageType> THUNDER = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.parse("dnd:thunder"));
    public static final ResourceKey<DamageType> FORCE = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.parse("dnd:force"));
    public static final ResourceKey<DamageType> NECROTIC = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.parse("dnd:necrotic"));
    public static final ResourceKey<DamageType> POISON = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.parse("dnd:poison"));
    public static final ResourceKey<DamageType> ACID = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.parse("dnd:acid"));
    public static final ResourceKey<DamageType> PSYCHIC = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.parse("dnd:psychic"));
    public static final ResourceKey<DamageType> RADIANT = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.parse("dnd:radiant"));
    public static final ResourceKey<DamageType> BLUDGEONING = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.parse("dnd:bludgeoning"));
    public static final ResourceKey<DamageType> PIERCING = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.parse("dnd:piercing"));
    public static final ResourceKey<DamageType> SLASHING = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.parse("dnd:slashing"));
}