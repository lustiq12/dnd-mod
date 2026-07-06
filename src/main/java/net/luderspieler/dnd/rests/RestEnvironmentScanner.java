package net.luderspieler.dnd.rests;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Set;

/**
 * Scans the blocks around a bed position and returns a ScanResult.
 * Works on both client and server side — call from LongRestScreen (client)
 * for display, and from SleepingIntereferer.applyRadiusBonuses() (server)
 * for actual effect application.
 */
public class RestEnvironmentScanner {

    public static final int RADIUS = 5;

    // Raw (uncooked) meat items that count toward the campfire food bonus.
    private static final Set<Item> MEAT_ITEMS = Set.of(
            Items.BEEF, Items.PORKCHOP, Items.CHICKEN, Items.RABBIT,
            Items.MUTTON, Items.COD, Items.SALMON
    );

    // ─────────────────────────────────────────────────────────────────────────

    public record ScanResult(
            boolean isSafe,          // no dark monster-spawn spots in radius
            boolean isWilderness,    // sky light reaches the bed
            boolean hasCampfire,     // at least one lit campfire in radius
            int     meatCount,       // raw meat items on campfires (0-4+)
            boolean hasAnvil,
            boolean hasFurnace,
            boolean hasEnchantingTable,
            boolean hasBrewingStand
    ) {
        /** True when the conditions for a long rest are met. */
        public boolean canRest() {
            if (!isSafe) return false;
            if (isWilderness && !hasCampfire) return false;
            return true;
        }

        /**
         * Food bonus tier based on campfire meat count.
         *   0 → no campfire / no meat
         *   1 → hunger 100 %
         *   2 → hunger + saturation 100 %
         *   3 → tier 2 + 8 min Saturation effect
         *   4 → tier 2 + 16 min Saturation effect
         */
        public int campfireTier() {
            if (!hasCampfire) return 0;
            return Math.min(4, meatCount);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────

    public static ScanResult scan(Level level, BlockPos bedPos) {
        boolean hasCampfire        = false;
        int     meatCount          = 0;
        boolean hasAnvil           = false;
        boolean hasFurnace         = false;
        boolean hasEnchantingTable = false;
        boolean hasBrewingStand    = false;
        boolean hasDarkSpot        = false;

        // Flat scan ±RADIUS in X/Z, ±2 in Y (beds don't usually have ceiling height issues).
        for (BlockPos pos : BlockPos.betweenClosed(
                bedPos.offset(-RADIUS, -2, -RADIUS),
                bedPos.offset( RADIUS,  2,  RADIUS))) {

            BlockState state = level.getBlockState(pos);
            Block      block = state.getBlock();

            // ── Campfire ──────────────────────────────────────────────────
            if (block instanceof CampfireBlock
                    && state.getValue(CampfireBlock.LIT)) {
                hasCampfire = true;
                // CampfireBlockEntity is synced to client, so this works both sides.
                if (level.getBlockEntity(pos) instanceof CampfireBlockEntity cbe) {
                    for (var item : cbe.getItems()) {
                        if (!item.isEmpty() && MEAT_ITEMS.contains(item.getItem())) {
                            meatCount++;
                        }
                    }
                }
            }

            // ── Bonus blocks ─────────────────────────────────────────────
            if (block instanceof AnvilBlock)            hasAnvil           = true;
            if (block instanceof AbstractFurnaceBlock)  hasFurnace         = true;
            if (block instanceof EnchantingTableBlock)  hasEnchantingTable = true;
            if (block instanceof BrewingStandBlock)     hasBrewingStand    = true;

            // ── Dark-spot check (solid floor, air above, block-light = 0) ─
            if (!hasDarkSpot && state.isSolidRender()) {
                BlockPos above = pos.above();
                if (level.getBlockState(above).isAir()
                        && level.getBrightness(LightLayer.BLOCK, above) == 0) {
                    hasDarkSpot = true;
                }
            }
        }

        // Wilderness = sky light can reach directly above the bed.
        boolean isWilderness = level.canSeeSky(bedPos.above());

        return new ScanResult(
                !hasDarkSpot, isWilderness,
                hasCampfire, meatCount,
                hasAnvil, hasFurnace, hasEnchantingTable, hasBrewingStand
        );
    }
}