package net.luderspieler.dnd.character.network;

import net.luderspieler.dnd.character.AbilitysAndFeats.management.AbilityUtils;
import net.luderspieler.dnd.character.choices.ChoiceUpdateSystem;
import net.luderspieler.dnd.character.definition.RaceDefinition;
import net.luderspieler.dnd.character.definition.SubraceDefinition;
import net.luderspieler.dnd.character.registrys.ClassRegistry;
import net.luderspieler.dnd.character.registrys.RaceRegistry;
import net.luderspieler.dnd.character.definition.ClassDefinition;
import net.luderspieler.dnd.network.DndModVariables;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
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

            // 1. Stammdaten
            vars.PlayerRace = pkt.raceId();
            vars.PlayerSubrace = pkt.subraceId();
            vars.PlayerClass = pkt.classId();
            vars.PlayerSubclass = "";
            vars.PlayerName = pkt.name();
            vars.PlayerStory = pkt.story();
            vars.PlayerPersonality = pkt.personality();
            vars.PlayerLevel = 1;
            vars.FinishedCharacterCreation = true;
            vars.CanUseMagic = cls.canUseMagic();
            vars.ChoicesNeeded = "";
            vars.ChoicesMade = "";
            vars.Abilities = "";

            clearAllSpellLists(vars);
            resetSpellSlots(cls, (int)vars.PlayerLevel);


            // 2. Proficiencies
            LinkedHashSet<String> profSet = new LinkedHashSet<>();
            addProfs(profSet, race.getProficiencies());
            if (subrace != null) addProfs(profSet, subrace.getProficiencies());
            addProfs(profSet, cls.getProficiencies());
            vars.Proficiencys = String.join(",", profSet);

            // 3. Stats
            resetStats(vars);
            applyDndStats(vars, race.getAbilityScoreIncrements());
            if (subrace != null) applyDndStats(vars, subrace.getAbilityScoreIncrements());
            applyDndStats(vars, cls.getAbilityScoreIncrements());

            // 3.5 Abilities
            AbilityUtils.addRaceAbilities(player);
            AbilityUtils.updateClassAbilities(player);
            ChoiceUpdateSystem.updateChoices(player);

            // 4. Items & Spells
            for (ItemStack stack : cls.getStarterItems()) {
                player.addItem(stack.copy());
            }

            vars.markSyncDirty();
            applyAttrs(player);
        });
    }

    private static void resetStats(DndModVariables.PlayerVariables vars) {
        vars.Strength = 10;
        vars.Dexterity = 10;
        vars.Constitution = 10;
        vars.Intelligence = 10;
        vars.Wisdom = 10;
        vars.Charisma = 10;
    }

    private static void applyDndStats(DndModVariables.PlayerVariables vars, Map<String, Integer> increments) {
        if (increments == null) return;

        for (Map.Entry<String, Integer> e : increments.entrySet()) {
            switch (e.getKey().toLowerCase()) {
                case "strength"     -> vars.Strength += e.getValue();
                case "dexterity"    -> vars.Dexterity += e.getValue();
                case "constitution" -> vars.Constitution += e.getValue();
                case "intelligence" -> vars.Intelligence += e.getValue();
                case "wisdom"       -> vars.Wisdom += e.getValue();
                case "charisma"     -> vars.Charisma += e.getValue();
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

    public static String resetSpellSlots(ClassDefinition cls, int level) {
        int[][] slotTable = cls.getSpellSlots();

        // Sicherheitsscheck: Falls keine Tabelle da ist oder das Level ungültig ist
        if (slotTable == null || level < 0 || level >= slotTable.length) {
            return "000000000";
        }

        int[] slotsAtLevel = slotTable[level]; // Das Array hat die Länge 9 (Indizes 0-8)
        StringBuilder sb = new StringBuilder();

        // Wir laufen von 0 bis 8 (entspricht Grade 1 bis 9)
        for (int i = 0; i < 9; i++) {
            // slotsAtLevel[0] ist Grade 1, slotsAtLevel[1] ist Grade 2, etc.
            sb.append(slotsAtLevel[i]);
        }

        return sb.toString();
    }

    public static void applyAttrs(ServerPlayer player) {

        DndModVariables.PlayerVariables vars = player.getData(DndModVariables.PLAYER_VARIABLES);

        int level = (int) vars.PlayerLevel;

        int strM = (int) Math.floor((vars.Strength - 10) / 2.0);
        int dexM = (int) Math.floor((vars.Dexterity - 10) / 2.0);
        int conM = (int) Math.floor((vars.Constitution - 10) / 2.0);
        int intM = (int) Math.floor((vars.Intelligence - 10) / 2.0);
        int wisM = (int) Math.floor((vars.Wisdom - 10) / 2.0);
        int chaM = (int) Math.floor((vars.Charisma - 10) / 2.0);

        ClassDefinition cls = ClassRegistry.getClass(vars.PlayerClass);

        if (cls == null) {
            throw new IllegalStateException("Fehler: Spieler " + player.getName().getString() + " hat keine gültige Dnd-Klasse definiert! (Klassen-ID: " + vars.PlayerClass + ")");
        }
        // No x2 here as you stated the values are already doubled
        int hpPerLvl = cls.getClassHealth();

        // --- STRENGTH ---
        updateMod(player, Attributes.ATTACK_DAMAGE, "dnd:str_dmg", strM * 1.5);
        updateMod(player, Attributes.BLOCK_BREAK_SPEED, "dnd:str_mining", Math.max(-0.5, strM * 0.15));
        updateMod(player, Attributes.ATTACK_KNOCKBACK, "dnd:str_kb", Math.max(0, strM * 0.3));
        updateMod(player, Attributes.KNOCKBACK_RESISTANCE, "dnd:str_kb_res", Math.max(0, strM * 0.1));

        // --- DEXTERITY ---
        updateMod(player, Attributes.ATTACK_SPEED, "dnd:dex_ats", dexM * 0.15);
        updateMod(player, Attributes.SNEAKING_SPEED, "dnd:dex_sneak", dexM * 0.05);
        updateMod(player, Attributes.JUMP_STRENGTH, "dnd:dex_jump", dexM * 0.03);
        updateMod(player, net.neoforged.neoforge.common.NeoForgeMod.SWIM_SPEED, "dnd:dex_swim", dexM * 0.1);

        // --- CONSTITUTION ---
        // Bonus HP from Con (x2 for Hearts) + Class HP for levels above 1
        double totalTargetHP = (hpPerLvl + (conM * 2.0)) * level;
        double bonusHP = totalTargetHP - 20.0;
        if (bonusHP <= -20.0) {
            bonusHP = -18.0;
        }
        updateMod(player, Attributes.MAX_HEALTH, "dnd:con_hp", bonusHP);
        updateMod(player, Attributes.OXYGEN_BONUS, "dnd:con_oxy", conM * 20.0);
        updateMod(player, Attributes.SAFE_FALL_DISTANCE, "dnd:con_fall_dist", conM * 1.5);
        updateMod(player, Attributes.BURNING_TIME, "dnd:con_burn", conM * -0.1);

        // --- INTELLIGENCE ---
        updateMod(player, Attributes.MINING_EFFICIENCY, "dnd:int_eff", intM * 2.0);
        updateMod(player, Attributes.BLOCK_INTERACTION_RANGE, "dnd:int_reach", intM * 0.2);
        updateMod(player, Attributes.SUBMERGED_MINING_SPEED, "dnd:int_sub_mining", intM * 0.2);

        // --- WISDOM ---
        updateMod(player, Attributes.ENTITY_INTERACTION_RANGE, "dnd:wis_ent_reach", wisM * 0.3);
        updateMod(player, Attributes.STEP_HEIGHT, "dnd:wis_step", (wisM >= 2) ? 0.5 : 0.0);
        updateMod(player, Attributes.FALL_DAMAGE_MULTIPLIER, "dnd:wis_fall_dmg", wisM * -0.06);

        // --- CHARISMA ---
        updateMod(player, Attributes.LUCK, "dnd:cha_luck", (double) chaM);
        updateMod(player, Attributes.TEMPT_RANGE, "dnd:cha_tempt", chaM * 3.0);
        updateMod(player, net.neoforged.neoforge.common.NeoForgeMod.NAMETAG_DISTANCE, "dnd:cha_name", chaM * 2.0);

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