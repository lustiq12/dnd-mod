package net.luderspieler.dnd.rests;

import net.luderspieler.dnd.character.AbilitysAndFeats.management.Ability;
import net.luderspieler.dnd.aUtils.AbilityDataUtils;
import net.luderspieler.dnd.aUtils.AbilityUtils;
import net.luderspieler.dnd.character.definition.ClassDefinition;
import net.luderspieler.dnd.character.registrys.ClassRegistry;
import net.luderspieler.dnd.network.DndModVariables;
import net.luderspieler.dnd.resources.ResourceManager;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.entity.player.PlayerWakeUpEvent;

import java.util.Map;

import static net.luderspieler.dnd.character.network.CharacterCreationPacket.resetSpellSlots;

public class SleepingIntereferer {

    // ── BED RIGHT-CLICK INTERCEPT (client-side) ───────────────────────────────
    // PlayerBedEnterEvent no longer exists in NeoForge 1.21.x.
    // We intercept the right-click on the client, cancel it and open the
    // preview screen directly. BeginRestPacket re-validates conditions server-side.

    @SubscribeEvent
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        BlockPos pos = event.getPos();
        if (!(event.getLevel().getBlockState(pos).getBlock() instanceof BedBlock)) return;

        // Cancel on BOTH sides:
        //   Client → prevents local bed-GUI handling
        //   Server → prevents vanilla bed.use() so player doesn't sleep immediately
        // The actual sleep only happens when BeginRestPacket calls startSleepInBed().
        event.setCanceled(true);

        if (event.getLevel().isClientSide()) {
            Minecraft.getInstance().execute(() ->
                    Minecraft.getInstance().setScreen(new LongRestPreviewScreen(pos))
            );
        }
        // Server side: just cancel — no further action needed here.
    }

    // ── WAKE UP (server-side) ─────────────────────────────────────────────────

    @SubscribeEvent
    public void onWakeUp(PlayerWakeUpEvent event) {
        Player player = event.getEntity();
        Level  level  = player.level();

        if (level.isClientSide()) return;
        if (!(player instanceof ServerPlayer sp)) return;

        long    timeOfDay      = level.getDayTime() % 24000;
        boolean successfulRest = timeOfDay < 12000;

        if (!successfulRest) {
            sp.displayClientMessage(Component.literal(
                    "§cYour rest was interrupted — no benefits gained."), false);
        }

        // Retrieve the bed position stored by BeginRestPacket.
        var vars = sp.getData(DndModVariables.PLAYER_VARIABLES);
        String bedStr = AbilityDataUtils.get(vars, "LONG_REST_BED", "");
        BlockPos bedPos = parseBedPos(bedStr);
        AbilityDataUtils.set(vars, "LONG_REST_BED", ""); // clear after reading
        vars.markSyncDirty();

        if (bedPos == null) bedPos = sp.blockPosition(); // fallback

        // Open Screen 2 (spell prep + finish) — bonuses NOT applied yet.
        OpenLongRestManagementPacket.send(sp, bedPos, successfulRest);
    }

    // ── LONG REST BENEFITS — called via ApplyLongRestPacket ──────────────────

    public static void applyLongRestBenefits(ServerPlayer player, BlockPos bedPos) {
        DndModVariables.PlayerVariables vars =
                player.getData(DndModVariables.PLAYER_VARIABLES);

        // 1. Spell slots.
        ClassDefinition cls = ClassRegistry.getClass(vars.PlayerClass);
        vars.Spellslots = resetSpellSlots(cls, (int) vars.PlayerLevel);

        // 2. Full HP.
        player.setHealth(player.getMaxHealth());

        // 3. All ability charges + flag cleanup.
        ResourceManager.resetForLongRest(player);

        // 4. Resourceful (Human 2024) — Heroic Inspiration.
        if (AbilityUtils.hasAbility(player, Ability.RESOURCEFUL)) {
            AbilityDataUtils.set(vars, "HEROIC_INSPIRATION", 1);
            player.displayClientMessage(
                    Component.literal("§6[Resourceful] §eYou feel inspired!"), false);
        }

        // 5. Radius bonuses.
        applyRadiusBonuses(player, RestEnvironmentScanner.scan(player.level(), bedPos));

        vars.markSyncDirty();
        player.displayClientMessage(
                Component.literal("§aYour long rest is complete!"), false);
    }

    // ── RADIUS BONUSES ────────────────────────────────────────────────────────

    private static void applyRadiusBonuses(ServerPlayer player,
                                           RestEnvironmentScanner.ScanResult scan) {

        // Campfire — food / saturation.
        if (scan.hasCampfire()) {
            int tier = scan.campfireTier();
            if (tier >= 1) player.getFoodData().setFoodLevel(20);
            if (tier >= 2) player.getFoodData().setSaturation(
                    player.getFoodData().getFoodLevel() * 2.0f);
            if (tier >= 3) {
                int min = tier >= 4 ? 16 : 8;
                player.addEffect(new MobEffectInstance(
                        MobEffects.SATURATION, 20 * 60 * min, 0, false, true, true));
                player.displayClientMessage(Component.literal(
                        "§6[Campfire] §eWell Fed for " + min + " min!"), false);
            } else if (tier >= 1) {
                player.displayClientMessage(Component.literal(
                        "§6[Campfire] §eHunger" + (tier >= 2 ? " + saturation" : "")
                                + " restored!"), false);
            }
        }

        // Anvil — repair worn armor and hotbar weapons by 50 durability.
        if (scan.hasAnvil()) {
            final int REPAIR = 50;
            for (EquipmentSlot slot : new EquipmentSlot[]{
                    EquipmentSlot.HEAD, EquipmentSlot.CHEST,
                    EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
                ItemStack armor = player.getItemBySlot(slot);
                if (!armor.isEmpty() && armor.isDamaged())
                    armor.setDamageValue(Math.max(0, armor.getDamageValue() - REPAIR));
            }
            for (int slot = 0; slot < 9; slot++) {
                ItemStack item = player.getInventory().getItem(slot);
                if (!item.isEmpty() && item.isDamaged())
                    item.setDamageValue(Math.max(0, item.getDamageValue() - REPAIR));
            }
            player.displayClientMessage(Component.literal(
                    "§6[Anvil] §eEquipment tended during the night."), false);
        }

        // Furnace — auto-smelt 50 % of raw ores (max 32).
        if (scan.hasFurnace()) {
            int smelted = applyAutoSmelt(player);
            player.displayClientMessage(Component.literal(
                    smelted > 0
                            ? "§6[Furnace] §e" + smelted + " ore(s) smelted."
                            : "§6[Furnace] §7No raw ores found."), false);
        }

        // Enchanting Table — +2 XP levels.
        if (scan.hasEnchantingTable()) {
            player.giveExperienceLevels(2);
            player.displayClientMessage(Component.literal(
                    "§6[Enchanting Table] §e+2 XP levels."), false);
        }

        // Brewing Stand — remove debuffs, apply morning buffs.
        if (scan.hasBrewingStand()) {
            player.getActiveEffects().stream()
                    .filter(e -> !e.getEffect().value().isBeneficial())
                    .map(MobEffectInstance::getEffect)
                    .toList()
                    .forEach(player::removeEffect);

            player.addEffect(new MobEffectInstance(
                    MobEffects.REGENERATION, 20 * 60 * 2, 0, false, true, true));
            player.addEffect(new MobEffectInstance(
                    MobEffects.RESISTANCE, 20 * 60 * 2, 0, false, true, true));

            player.displayClientMessage(Component.literal(
                    "§6[Brewing Stand] §eAilments cured. Morning tonics absorbed."), false);
        }
    }

    // ── AUTO-SMELT ────────────────────────────────────────────────────────────

    private static final Map<Item, Item> SMELT_MAP = Map.of(
            Items.RAW_IRON,   Items.IRON_INGOT,
            Items.RAW_GOLD,   Items.GOLD_INGOT,
            Items.RAW_COPPER, Items.COPPER_INGOT
    );

    private static int applyAutoSmelt(ServerPlayer player) {
        int converted = 0;
        for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
            if (converted >= 32) break;
            ItemStack stack = player.getInventory().getItem(slot);
            if (stack.isEmpty()) continue;
            Item result = SMELT_MAP.get(stack.getItem());
            if (result == null) continue;
            int toSmelt = Math.min(stack.getCount() / 2, 32 - converted);
            if (toSmelt < 1) continue;
            stack.shrink(toSmelt);
            converted += toSmelt;
            player.addItem(new ItemStack(result, toSmelt));
        }
        return converted;
    }

    // ── HELPERS ───────────────────────────────────────────────────────────────

    /** Parses "x;y;z" stored by BeginRestPacket. Returns null on parse failure. */
    private static BlockPos parseBedPos(String s) {
        if (s == null || s.isBlank()) return null;
        try {
            String[] p = s.split(";");
            return new BlockPos(Integer.parseInt(p[0]),
                    Integer.parseInt(p[1]),
                    Integer.parseInt(p[2]));
        } catch (Exception e) { return null; }
    }
}