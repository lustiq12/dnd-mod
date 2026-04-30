package net.luderspieler.dnd.classes;

import net.luderspieler.dnd.network.DndModVariables;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
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

            // ── 1. Combine proficiencies from race + subrace + class ──
            // Use a LinkedHashSet to deduplicate while preserving order
            LinkedHashSet<String> profSet = new LinkedHashSet<>();
            addProfs(profSet, race.getProficiencies());
            if (subrace != null) addProfs(profSet, subrace.getProficiencies());
            addProfs(profSet, cls.getProficiencies());
            String combinedProfs = String.join(",", profSet);

            // ── 2. Combine attribute modifiers: race + class ──
            Map<String, Double> combined = new LinkedHashMap<>();
            for (Map.Entry<String,Double> e : race.getAttributeModifiers().entrySet())
                combined.merge(e.getKey(), e.getValue(), Double::sum);
            for (Map.Entry<String,Double> e : cls.getAttributeModifiers().entrySet())
                combined.merge(e.getKey(), e.getValue(), Double::sum);

            // ── 3. Set player variables ──
            DndModVariables.PlayerVariables vars = player.getData(DndModVariables.PLAYER_VARIABLES);
            vars.PlayerRace                = pkt.raceId();
            vars.PlayerSubrace             = pkt.subraceId();
            vars.PlayerClass               = pkt.classId();
            vars.PlayerName                = pkt.name();
            vars.PlayerStory               = pkt.story();
            vars.PlayerPersonality         = pkt.personality();
            vars.PlayerLevel               = 1;
            vars.Spellslots                = "000000000";
            vars.Proficiencys              = combinedProfs;
            vars.FinishedCharacterCreation = true;
            vars.markSyncDirty();

            // ── 4. Apply attribute modifiers ──
            applyAttrs(player, combined);

            // ── 5. Give starter items — CLASS ONLY (race items removed) ──
            for (ItemStack stack : cls.getStarterItems())
                player.addItem(stack.copy());

            // ── 6. Heal to full ──
            player.setHealth(player.getMaxHealth());
        });
    }

    /** Split comma-separated proficiency string and add non-empty entries to set */
    private static void addProfs(LinkedHashSet<String> set, String profs) {
        if (profs == null || profs.isBlank()) return;
        for (String p : profs.split(",")) {
            String trimmed = p.trim();
            if (!trimmed.isEmpty()) set.add(trimmed);
        }
    }

    // ── Attribute IDs ──
    private static final ResourceLocation ID_HP    = ResourceLocation.parse("dnd:class_max_health");
    private static final ResourceLocation ID_DMG   = ResourceLocation.parse("dnd:class_attack_damage");
    private static final ResourceLocation ID_ARMOR = ResourceLocation.parse("dnd:class_armor");
    private static final ResourceLocation ID_SPEED = ResourceLocation.parse("dnd:class_movement_speed");
    private static final ResourceLocation ID_ASPD  = ResourceLocation.parse("dnd:class_attack_speed");
    private static final ResourceLocation ID_LUCK  = ResourceLocation.parse("dnd:class_luck");

    private static void applyAttrs(ServerPlayer player, Map<String, Double> attrs) {
        removeIfPresent(player, Attributes.MAX_HEALTH,     ID_HP);
        removeIfPresent(player, Attributes.ATTACK_DAMAGE,  ID_DMG);
        removeIfPresent(player, Attributes.ARMOR,          ID_ARMOR);
        removeIfPresent(player, Attributes.MOVEMENT_SPEED, ID_SPEED);
        removeIfPresent(player, Attributes.ATTACK_SPEED,   ID_ASPD);
        removeIfPresent(player, Attributes.LUCK,           ID_LUCK);

        for (Map.Entry<String, Double> e : attrs.entrySet()) {
            if (e.getValue() == 0) continue;
            switch (e.getKey()) {
                case "Max Health"     -> addMod(player, Attributes.MAX_HEALTH,     ID_HP,    e.getValue());
                case "Attack Damage"  -> addMod(player, Attributes.ATTACK_DAMAGE,  ID_DMG,   e.getValue());
                case "Armor"          -> addMod(player, Attributes.ARMOR,          ID_ARMOR, e.getValue());
                case "Movement Speed" -> addMod(player, Attributes.MOVEMENT_SPEED, ID_SPEED, e.getValue());
                case "Attack Speed"   -> addMod(player, Attributes.ATTACK_SPEED,   ID_ASPD,  e.getValue());
                case "Luck"           -> addMod(player, Attributes.LUCK,           ID_LUCK,  e.getValue());
            }
        }
    }

    private static void addMod(ServerPlayer player,
                               net.minecraft.core.Holder<net.minecraft.world.entity.ai.attributes.Attribute> attr,
                               ResourceLocation id, double value) {
        var inst = player.getAttribute(attr);
        if (inst != null) inst.addPermanentModifier(new AttributeModifier(id, value, AttributeModifier.Operation.ADD_VALUE));
    }

    private static void removeIfPresent(ServerPlayer player,
                                        net.minecraft.core.Holder<net.minecraft.world.entity.ai.attributes.Attribute> attr,
                                        ResourceLocation id) {
        var inst = player.getAttribute(attr);
        if (inst != null) inst.removeModifier(id);
    }
}