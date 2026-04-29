package net.luderspieler.dnd.classes;

import net.luderspieler.dnd.classes.RaceDefinition;
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
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Map;

public record CharacterCreationPacket(
        String raceId,
        String subraceId,
        String classId,
        String name,
        String story,
        String personality
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

    // ── CLIENT-SIDE SEND ──
    public static void send(String raceId, String subraceId, String classId,
                            String name, String story, String personality) {
        ClientPacketDistributor.sendToServer(
                new CharacterCreationPacket(raceId, subraceId, classId, name, story, personality)
        );
    }

    // ── SERVER-SIDE HANDLE ──
    public static void handle(CharacterCreationPacket pkt, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (!(ctx.player() instanceof ServerPlayer player)) return;

            RaceDefinition race    = RaceRegistry.getRace(pkt.raceId());
            ClassDefinition cls     = ClassRegistry.getClass(pkt.classId());

            if (race == null || cls == null) return;

            // ── 1. Set player variables ──
            DndModVariables.PlayerVariables vars = player.getData(DndModVariables.PLAYER_VARIABLES);
            vars.PlayerRace        = pkt.raceId();
            vars.PlayerSubrace     = pkt.subraceId();
            vars.PlayerClass       = pkt.classId();
            vars.PlayerName        = pkt.name();
            vars.PlayerStory       = pkt.story();
            vars.PlayerPersonality = pkt.personality();
            vars.PlayerLevel       = 1;
            vars.Spellslots        = "000000000"; // 9 slot levels, all 0
            vars.FinishedCharacterCreation = true;
            vars.markSyncDirty();

            // ── 2. Combine attrs: race + class ──
            Map<String, Double> combined = new java.util.LinkedHashMap<>();
            for (Map.Entry<String,Double> e : race.getAttributeModifiers().entrySet())
                combined.merge(e.getKey(), e.getValue(), Double::sum);
            for (Map.Entry<String,Double> e : cls.getAttributeModifiers().entrySet())
                combined.merge(e.getKey(), e.getValue(), Double::sum);

            // ── 3. Apply attribute modifiers ──
            applyAttrs(player, combined);

            // ── 4. Give starter items (class items + race items) ──
            for (ItemStack stack : cls.getStarterItems())
                player.addItem(stack.copy());
            for (ItemStack stack : race.getStarterItems())
                player.addItem(stack.copy());

            // ── 5. Heal to full after applying new max health ──
            player.setHealth(player.getMaxHealth());
        });
    }

    // ─────────────────────────────────────────────────────────────
    //  Attribute application
    //  We use ADD_VALUE operation and a fixed UUID per stat so we
    //  can remove/re-apply cleanly if the player recreates.
    // ─────────────────────────────────────────────────────────────

    private static final java.util.UUID UUID_HP    = java.util.UUID.fromString("a1b2c3d4-0001-0000-0000-000000000001");
    private static final java.util.UUID UUID_DMG   = java.util.UUID.fromString("a1b2c3d4-0002-0000-0000-000000000002");
    private static final java.util.UUID UUID_ARMOR = java.util.UUID.fromString("a1b2c3d4-0003-0000-0000-000000000003");
    private static final java.util.UUID UUID_SPEED = java.util.UUID.fromString("a1b2c3d4-0004-0000-0000-000000000004");
    private static final java.util.UUID UUID_ASPD  = java.util.UUID.fromString("a1b2c3d4-0005-0000-0000-000000000005");
    private static final java.util.UUID UUID_LUCK  = java.util.UUID.fromString("a1b2c3d4-0006-0000-0000-000000000006");

    private static final ResourceLocation ID_HP    = ResourceLocation.parse("dnd:class_max_health");
    private static final ResourceLocation ID_DMG   = ResourceLocation.parse("dnd:class_attack_damage");
    private static final ResourceLocation ID_ARMOR = ResourceLocation.parse("dnd:class_armor");
    private static final ResourceLocation ID_SPEED = ResourceLocation.parse("dnd:class_movement_speed");
    private static final ResourceLocation ID_ASPD  = ResourceLocation.parse("dnd:class_attack_speed");
    private static final ResourceLocation ID_LUCK  = ResourceLocation.parse("dnd:class_luck");

    private static void applyAttrs(ServerPlayer player, Map<String, Double> attrs) {
        // Remove old modifiers first (in case of recreation)
        removeIfPresent(player, Attributes.MAX_HEALTH,     ID_HP);
        removeIfPresent(player, Attributes.ATTACK_DAMAGE,  ID_DMG);
        removeIfPresent(player, Attributes.ARMOR,          ID_ARMOR);
        removeIfPresent(player, Attributes.MOVEMENT_SPEED, ID_SPEED);
        removeIfPresent(player, Attributes.ATTACK_SPEED,   ID_ASPD);
        removeIfPresent(player, Attributes.LUCK,           ID_LUCK);

        for (Map.Entry<String, Double> e : attrs.entrySet()) {
            if (e.getValue() == 0) continue;
            switch (e.getKey()) {
                case "Max Health"     -> addMod(player, Attributes.MAX_HEALTH,     ID_HP,    e.getValue(), AttributeModifier.Operation.ADD_VALUE);
                case "Attack Damage"  -> addMod(player, Attributes.ATTACK_DAMAGE,  ID_DMG,   e.getValue(), AttributeModifier.Operation.ADD_VALUE);
                case "Armor"          -> addMod(player, Attributes.ARMOR,          ID_ARMOR, e.getValue(), AttributeModifier.Operation.ADD_VALUE);
                case "Movement Speed" -> addMod(player, Attributes.MOVEMENT_SPEED, ID_SPEED, e.getValue(), AttributeModifier.Operation.ADD_VALUE);
                case "Attack Speed"   -> addMod(player, Attributes.ATTACK_SPEED,   ID_ASPD,  e.getValue(), AttributeModifier.Operation.ADD_VALUE);
                case "Luck"           -> addMod(player, Attributes.LUCK,           ID_LUCK,  e.getValue(), AttributeModifier.Operation.ADD_VALUE);
            }
        }
    }

    private static void addMod(ServerPlayer player,
                               net.minecraft.core.Holder<net.minecraft.world.entity.ai.attributes.Attribute> attr,
                               ResourceLocation id, double value,
                               AttributeModifier.Operation op) {
        var instance = player.getAttribute(attr);
        if (instance != null)
            instance.addPermanentModifier(new AttributeModifier(id, value, op));
    }

    private static void removeIfPresent(ServerPlayer player,
                                        net.minecraft.core.Holder<net.minecraft.world.entity.ai.attributes.Attribute> attr,
                                        ResourceLocation id) {
        var instance = player.getAttribute(attr);
        if (instance != null) instance.removeModifier(id);
    }
}