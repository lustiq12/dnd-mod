package net.luderspieler.dnd.classes;

import net.luderspieler.dnd.network.DndModVariables;
import net.minecraft.core.Holder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.*;

public record CharacterCreationPacket(
        String raceId, String subraceId, String classId,
        String name, String story, String personality
) implements CustomPacketPayload {

    public static final Type<CharacterCreationPacket> TYPE =
            new Type<>(ResourceLocation.parse("dnd:character_creation"));

    public static final StreamCodec<FriendlyByteBuf, CharacterCreationPacket> CODEC =
            StreamCodec.composite(
                    net.minecraft.network.codec.ByteBufCodecs.STRING_UTF8, CharacterCreationPacket::raceId,
                    net.minecraft.network.codec.ByteBufCodecs.STRING_UTF8, CharacterCreationPacket::subraceId,
                    net.minecraft.network.codec.ByteBufCodecs.STRING_UTF8, CharacterCreationPacket::classId,
                    net.minecraft.network.codec.ByteBufCodecs.STRING_UTF8, CharacterCreationPacket::name,
                    net.minecraft.network.codec.ByteBufCodecs.STRING_UTF8, CharacterCreationPacket::story,
                    net.minecraft.network.codec.ByteBufCodecs.STRING_UTF8, CharacterCreationPacket::personality,
                    CharacterCreationPacket::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void send(String raceId, String subraceId, String classId,
                            String name, String story, String personality) {
        ClientPacketDistributor.sendToServer(
                new CharacterCreationPacket(raceId, subraceId, classId, name, story, personality));
    }

    public static void handle(CharacterCreationPacket pkt, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (!(ctx.player() instanceof ServerPlayer player)) return;

            RaceDefinition    race    = RaceRegistry.getRace(pkt.raceId());
            SubraceDefinition subrace = RaceRegistry.getSubrace(pkt.subraceId());
            ClassDefinition   cls     = ClassRegistry.getClass(pkt.classId());

            if (race == null || cls == null) return;

            // 1. Combine proficiencies
            LinkedHashSet<String> profSet = new LinkedHashSet<>();
            addProfs(profSet, race.getProficiencies());
            if (subrace != null) addProfs(profSet, subrace.getProficiencies());
            addProfs(profSet, cls.getProficiencies());
            String combinedProfs = String.join(",", profSet);

            // 2. Set player variables
            DndModVariables.PlayerVariables vars = player.getData(DndModVariables.PLAYER_VARIABLES);
            vars.PlayerRace                = pkt.raceId();
            vars.PlayerSubrace             = pkt.subraceId();
            vars.PlayerClass               = pkt.classId();
            vars.PlayerName                = pkt.name();
            vars.PlayerStory               = pkt.story();
            vars.PlayerPersonality         = pkt.personality();
            vars.PlayerLevel               = 1;
            vars.PlayerXP                  = 0; // hatte gefehlt
            vars.Spellslots                = "000000000";
            vars.Proficiencys              = combinedProfs;
            vars.FinishedCharacterCreation = true;
            vars.markSyncDirty();

            // 3. Apply attribute modifiers (Separated Species and Class)
            applyAttrs(player, race.getAttributeModifiers(), true);
            applyAttrs(player, cls.getAttributeModifiers(), false);

            // 4. Give starter items
            for (ItemStack stack : cls.getStarterItems())
                player.addItem(stack.copy());

            // 5. Heal to full
            player.setHealth(player.getMaxHealth());
        });
    }

    private static void addProfs(LinkedHashSet<String> set, String profs) {
        if (profs == null || profs.isBlank()) return;
        for (String p : profs.split(",")) {
            String trimmed = p.trim();
            if (!trimmed.isEmpty()) set.add(trimmed);
        }
    }

    // ── Attribute IDs Species ──
    private static final ResourceLocation ID_RACE_HP    = ResourceLocation.parse("dnd:species_max_health");
    private static final ResourceLocation ID_RACE_DMG   = ResourceLocation.parse("dnd:species_attack_damage");
    private static final ResourceLocation ID_RACE_ARMOR = ResourceLocation.parse("dnd:species_armor");
    private static final ResourceLocation ID_RACE_SPEED = ResourceLocation.parse("dnd:species_movement_speed");
    private static final ResourceLocation ID_RACE_ASPD  = ResourceLocation.parse("dnd:species_attack_speed");
    private static final ResourceLocation ID_RACE_LUCK  = ResourceLocation.parse("dnd:species_luck");

    // ── Attribute IDs Class ──
    private static final ResourceLocation ID_CLASS_HP    = ResourceLocation.parse("dnd:class_max_health");
    private static final ResourceLocation ID_CLASS_DMG   = ResourceLocation.parse("dnd:class_attack_damage");
    private static final ResourceLocation ID_CLASS_ARMOR = ResourceLocation.parse("dnd:class_armor");
    private static final ResourceLocation ID_CLASS_SPEED = ResourceLocation.parse("dnd:class_movement_speed");
    private static final ResourceLocation ID_CLASS_ASPD  = ResourceLocation.parse("dnd:class_attack_speed");
    private static final ResourceLocation ID_CLASS_LUCK  = ResourceLocation.parse("dnd:class_luck");

    public static void applyAttrs(ServerPlayer player, Map<String, Double> attrs, boolean isRace) {
        DndModVariables.PlayerVariables vars = player.getData(DndModVariables.PLAYER_VARIABLES);
        int PlayerLevel = (int)vars.PlayerLevel;
        ClassDefinition cls = ClassRegistry.getClass(vars.PlayerClass);
        int HealthPerLevel = (cls != null) ? cls.getClassHealth() : 0;

        for (Map.Entry<String, Double> e : attrs.entrySet()) {
            double value = e.getValue();
            Holder<Attribute> attr;
            ResourceLocation modifierId;
            double finalValue = value;

            switch (e.getKey()) {
                case "Max Health" -> {
                    attr = Attributes.MAX_HEALTH;
                    modifierId = isRace ? ID_RACE_HP : ID_CLASS_HP;
                    // Die HP-Skalierung findet jetzt hier statt, wenn es sich um die Klasse handelt
                    if (!isRace) {
                        finalValue = value + (HealthPerLevel * PlayerLevel);
                    }
                }
                case "Attack Damage" -> {
                    attr = Attributes.ATTACK_DAMAGE;
                    modifierId = isRace ? ID_RACE_DMG : ID_CLASS_DMG;
                }
                case "Armor" -> {
                    attr = Attributes.ARMOR;
                    modifierId = isRace ? ID_RACE_ARMOR : ID_CLASS_ARMOR;
                }
                case "Movement Speed" -> {
                    attr = Attributes.MOVEMENT_SPEED;
                    modifierId = isRace ? ID_RACE_SPEED : ID_CLASS_SPEED;
                }
                case "Attack Speed" -> {
                    attr = Attributes.ATTACK_SPEED;
                    modifierId = isRace ? ID_RACE_ASPD : ID_CLASS_ASPD;
                }
                case "Luck" -> {
                    attr = Attributes.LUCK;
                    modifierId = isRace ? ID_RACE_LUCK : ID_CLASS_LUCK;
                    // Luck nimmt jetzt nur noch den Basis-Wert 'value'
                }
                default -> { continue; }
            }

            removeIfPresent(player, attr, modifierId);

            // Wir fügen den Modifier hinzu, wenn der berechnete finalValue nicht 0 ist
            if (finalValue != 0) {
                var instance = player.getAttribute(attr);
                if (instance != null) {
                    instance.addPermanentModifier(new AttributeModifier(modifierId, finalValue, AttributeModifier.Operation.ADD_VALUE));

                    // Kleiner Fix: Wenn Max Health erhöht wird, sollte die aktuelle HP des Spielers
                    // synchronisiert werden, damit die neuen Herzen nicht leer erscheinen.
                    if (attr == Attributes.MAX_HEALTH) {
                        removeIfPresent(player, attr, modifierId);
                        // ... modifier hinzufügen ...
                        player.setHealth(player.getMaxHealth()); // Macht die Leiste einfach voll
                    }
                }
            }
        }
    }

    private static void removeIfPresent(ServerPlayer player, Holder<Attribute> attr, ResourceLocation id) {
        var inst = player.getAttribute(attr);
        if (inst != null) inst.removeModifier(id);
    }
}