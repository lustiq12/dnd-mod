package net.luderspieler.dnd.classes;

import net.luderspieler.dnd.network.DndModVariables;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.LinkedHashSet;
import java.util.Map;

public record CharacterCreationPacket(
        String raceId, String subraceId, String classId,
        String name, String story, String personality
) implements CustomPacketPayload {

    public static final Type<CharacterCreationPacket> TYPE =
            new Type<>(ResourceLocation.parse("dnd:character_creation"));

    public static final StreamCodec<FriendlyByteBuf, CharacterCreationPacket> CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.STRING_UTF8, CharacterCreationPacket::raceId,
                    ByteBufCodecs.STRING_UTF8, CharacterCreationPacket::subraceId,
                    ByteBufCodecs.STRING_UTF8, CharacterCreationPacket::classId,
                    ByteBufCodecs.STRING_UTF8, CharacterCreationPacket::name,
                    ByteBufCodecs.STRING_UTF8, CharacterCreationPacket::story,
                    ByteBufCodecs.STRING_UTF8, CharacterCreationPacket::personality,
                    CharacterCreationPacket::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void send(String raceId, String subraceId, String classId,
                            String name, String story, String personality) {
        ClientPacketDistributor.sendToServer(new CharacterCreationPacket(raceId, subraceId, classId, name, story, personality));
    }

    public static void handle(CharacterCreationPacket pkt, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (!(ctx.player() instanceof ServerPlayer player)) return;

            RaceDefinition race = RaceRegistry.getRace(pkt.raceId());
            SubraceDefinition subrace = RaceRegistry.getSubrace(pkt.subraceId());
            ClassDefinition cls = ClassRegistry.getClass(pkt.classId());

            if (race == null || cls == null) return;

            DndModVariables.PlayerVariables vars = player.getData(DndModVariables.PLAYER_VARIABLES);

            // 1. Stammdaten (Namen an deine PlayerVariables angepasst)
            vars.PlayerRace = pkt.raceId();
            vars.PlayerSubrace = pkt.subraceId();
            vars.PlayerClass = pkt.classId();
            vars.PlayerName = pkt.name();      // FIX: hieß vorher CharacterName
            vars.PlayerStory = pkt.story();    // FIX: hieß vorher Backstory
            vars.PlayerPersonality = pkt.personality(); // FIX: hieß vorher Personality
            vars.PlayerLevel = 1;
            vars.FinishedCharacterCreation = true;
            vars.CanUseMagic = cls.canUseMagic();

            // 2. Proficiencies
            LinkedHashSet<String> profSet = new LinkedHashSet<>();
            addProfs(profSet, race.getProficiencies());
            if (subrace != null) addProfs(profSet, subrace.getProficiencies());
            addProfs(profSet, cls.getProficiencies());
            vars.Proficiencys = String.join(",", profSet);

            // 3. Stats
            resetStats(vars);
            applyDndStats(vars, race.getAbilityScoreIncrements());
            // FIX: Da SubraceDefinition evtl. die Methode nicht hat, nutzen wir getAttributeModifiers
            // oder stellen sicher, dass sie existiert. Falls Subrace nur Double nutzt: (int) (double)
            if (subrace != null) applyDndStats(vars, subrace.getAbilityScoreIncrements());
            applyDndStats(vars, cls.getAbilityScoreIncrements());

            // 4. Items & Spells
            for (ItemStack stack : cls.getStarterItems()) {
                player.addItem(stack.copy());
            }
            clearAllSpellLists(vars);

            vars.FinishedCharacterCreation = true;
            vars.markSyncDirty();
            applyAttrs(player, null, false);
        });
    }

    private static void resetStats(DndModVariables.PlayerVariables vars) {
        vars.Strength = 0; vars.Dexterity = 0; vars.Constitution = 0;
        vars.Intelligence = 0; vars.Wisdom = 0; vars.Charisma = 0;
    }

    private static void applyDndStats(DndModVariables.PlayerVariables vars, Map<String, Integer> increments) {
        if (increments == null) return;
        for (Map.Entry<String, Integer> e : increments.entrySet()) {
            switch (e.getKey()) {
                case "Strength"     -> vars.Strength += e.getValue();
                case "Dexterity"    -> vars.Dexterity += e.getValue();
                case "Constitution" -> vars.Constitution += e.getValue();
                case "Intelligence" -> vars.Intelligence += e.getValue();
                case "Wisdom"       -> vars.Wisdom += e.getValue();
                case "Charisma"     -> vars.Charisma += e.getValue();
            }
        }
    }

    private static void addProfs(LinkedHashSet<String> set, String profs) {
        if (profs == null || profs.isBlank()) return;
        for (String p : profs.split(",")) {
            String trimmed = p.trim();
            if (!trimmed.isEmpty()) set.add(trimmed);
        }
    }

    private static void clearAllSpellLists(DndModVariables.PlayerVariables v) {
        v.PreparedCantrips = ""; v.PreparedSpellsLVL1 = ""; v.PreparedSpellsLVL2 = "";
        v.PreparedSpellsLVL3 = ""; v.PreparedSpellsLVL4 = ""; v.PreparedSpellsLVL5 = "";
        v.PreparedSpellsLVL6 = ""; v.PreparedSpellsLVL7 = ""; v.PreparedSpellsLVL8 = "";
        v.PreparedSpellsLVL9 = "";
    }

    public static void applyAttrs(ServerPlayer player, Map<String, Double> attrs, boolean isRace) {
        player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§aDEBUG: Wende Attribute an..."));

        DndModVariables.PlayerVariables vars = player.getData(DndModVariables.PLAYER_VARIABLES);

        int level = (int) vars.PlayerLevel;

        // Hilfsfunktion für Modifier: (Wert - 10) / 2
        int strM = (int) Math.floor((vars.Strength - 10) / 2.0);
        int dexM = (int) Math.floor((vars.Dexterity - 10) / 2.0);
        int conM = (int) Math.floor((vars.Constitution - 10) / 2.0);
        int intM = (int) Math.floor((vars.Intelligence - 10) / 2.0);
        int wisM = (int) Math.floor((vars.Wisdom - 10) / 2.0);
        int chaM = (int) Math.floor((vars.Charisma - 10) / 2.0);

        ClassDefinition cls = ClassRegistry.getClass(vars.PlayerClass);
        int hpPerLvl = (cls != null) ? cls.getClassHealth() : 0;

        // --- STRENGTH ---
        updateMod(player, Attributes.ATTACK_DAMAGE, "dnd:str_dmg", strM);
        updateMod(player, Attributes.BLOCK_BREAK_SPEED, "dnd:str_mining", Math.max(-0.5, strM * 0.1));
        updateMod(player, Attributes.ATTACK_KNOCKBACK, "dnd:str_kb", Math.max(0, strM * 0.5));
        updateMod(player, Attributes.KNOCKBACK_RESISTANCE, "dnd:str_kb_res", Math.max(0, strM * 0.1));

        // --- DEXTERITY ---
        updateMod(player, Attributes.MOVEMENT_SPEED, "dnd:dex_speed", dexM * 0.005);
        updateMod(player, Attributes.ATTACK_SPEED, "dnd:dex_ats", dexM * 0.2);
        updateMod(player, Attributes.SNEAKING_SPEED, "dnd:dex_sneak", dexM * 0.05);
        updateMod(player, Attributes.JUMP_STRENGTH, "dnd:dex_jump", dexM * 0.02);
        updateMod(player, net.neoforged.neoforge.common.NeoForgeMod.SWIM_SPEED, "dnd:dex_swim", dexM * 0.1);

        // --- CONSTITUTION ---
        double maxHP = (conM * level) + (hpPerLvl * (level - 1));
        updateMod(player, Attributes.MAX_HEALTH, "dnd:con_hp", maxHP);
        updateMod(player, Attributes.OXYGEN_BONUS, "dnd:con_oxy", conM * 10.0);
        updateMod(player, Attributes.SAFE_FALL_DISTANCE, "dnd:con_fall_dist", conM * 0.5);
        updateMod(player, Attributes.BURNING_TIME, "dnd:con_burn", Math.max(-0.8, conM * -0.05));

        // --- INTELLIGENCE ---
        updateMod(player, Attributes.MINING_EFFICIENCY, "dnd:int_eff", intM * 1.0);
        updateMod(player, Attributes.BLOCK_INTERACTION_RANGE, "dnd:int_reach", intM * 0.2);
        updateMod(player, Attributes.SUBMERGED_MINING_SPEED, "dnd:int_sub_mining", intM * 0.1);

        // --- WISDOM ---
        updateMod(player, Attributes.ENTITY_INTERACTION_RANGE, "dnd:wis_ent_reach", wisM * 0.2);
        updateMod(player, Attributes.STEP_HEIGHT, "dnd:wis_step", Math.max(0, wisM * 0.1));
        updateMod(player, Attributes.FALL_DAMAGE_MULTIPLIER, "dnd:wis_fall_dmg", wisM * -0.05);

        // --- CHARISMA ---
        updateMod(player, Attributes.LUCK, "dnd:cha_luck", (double) chaM);
        updateMod(player, Attributes.TEMPT_RANGE, "dnd:cha_tempt", chaM * 2.0);
        updateMod(player, net.neoforged.neoforge.common.NeoForgeMod.NAMETAG_DISTANCE, "dnd:cha_name", chaM * 2.0);

        // Sync & Heal
        player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§aDEBUG: Max HP Bonus: " + maxHP));
        player.setHealth(player.getMaxHealth());
    }

    private static void updateMod(ServerPlayer player, net.minecraft.core.Holder<net.minecraft.world.entity.ai.attributes.Attribute> attr, String idStr, double val) {
        var inst = player.getAttribute(attr);
        if (inst != null) {
            ResourceLocation loc = ResourceLocation.fromNamespaceAndPath("dnd", idStr.replace("dnd:", ""));

            inst.removeModifier(loc);
            if (val != 0) {
                inst.addPermanentModifier(new AttributeModifier(loc, val, AttributeModifier.Operation.ADD_VALUE));
            }
        }
    }
}