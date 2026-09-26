package net.luderspieler.dnd.character.network;

import net.luderspieler.dnd.character.AbilitysAndFeats.management.Ability;
import net.luderspieler.dnd.Utils.AbilityDataUtils;
import net.luderspieler.dnd.Utils.AbilityUtils;
import net.luderspieler.dnd.character.AttributeHandler;
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

import java.util.Map;

import static net.luderspieler.dnd.Utils.ProficiencyUtils.addProficiency;
import static net.luderspieler.dnd.character.AttributeHandler.applyAttrs;

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
            vars.PlayerXP = 0;
            vars.FinishedCharacterCreation = true;
            vars.CanUseMagic = cls.canUseMagic();
            vars.ChoicesNeeded = "";
            vars.ChoicesMade = "";
            vars.Abilities = "";
            vars.Feats = "";
            vars.AbilityData = "";
            vars.GeneralData = "";
            vars.Proficiencys = "";

            clearAllSpellLists(vars);
            resetSpellSlots(cls, (int)vars.PlayerLevel);


            // 2. Proficiencies
            if (race.getProficiencies() != null && !race.getProficiencies().isBlank()) {
                for (String prof : race.getProficiencies().split(",")) {
                    addProficiency(vars, prof.trim());
                }
            }

            if (subrace != null && subrace.getProficiencies() != null && !subrace.getProficiencies().isBlank()) {
                for (String prof : subrace.getProficiencies().split(",")) {
                    addProficiency(vars, prof.trim());
                }
            }

            if (cls.getProficiencies() != null && !cls.getProficiencies().isBlank()) {
                for (String prof : cls.getProficiencies().split(",")) {
                    addProficiency(vars, prof.trim());
                }
            }

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
}