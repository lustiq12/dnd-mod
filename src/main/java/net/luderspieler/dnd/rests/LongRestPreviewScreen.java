package net.luderspieler.dnd.rests;

import net.luderspieler.dnd.generalConfigs;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Screen 1 of the Long Rest flow — shown BEFORE the player sleeps.
 * Displays the bonus preview and any blocking conditions.
 * "Begin Rest" sends BeginRestPacket to the server, which rolls for
 * a wilderness encounter and then actually puts the player to sleep.
 */
public class LongRestPreviewScreen extends Screen {

    private final BlockPos bedPos;
    private RestEnvironmentScanner.ScanResult scan;

    private record Row(String text, int color) {}
    private final List<Row> rows = new ArrayList<>();

    public LongRestPreviewScreen(BlockPos bedPos) {
        super(Component.literal("Long Rest"));
        this.bedPos = bedPos;
    }

    @Override
    protected void init() {
        super.init();

        if (this.minecraft.level != null)
            scan = RestEnvironmentScanner.scan(this.minecraft.level, bedPos);

        buildRows();

        int cx     = this.width / 2;
        int rowsH  = rows.size() * 12;
        int btnTop = this.height / 2 + rowsH / 2 + 18;

        boolean canRest = scan == null || scan.canRest();

        if (canRest) {
            this.addRenderableWidget(Button.builder(
                    Component.literal("Begin Rest"),
                    btn -> {
                        BeginRestPacket.send(bedPos);
                        this.onClose();
                    }
            ).bounds(cx - 75, btnTop, 150, 20).build());
        } else {
            // Rest is blocked — show a clear "Close" button so the player
            // knows how to exit (ESC is disabled).
            this.addRenderableWidget(Button.builder(
                    Component.literal("Close"),
                    btn -> this.onClose()
            ).bounds(cx - 75, btnTop, 150, 20).build());
        }
    }

    private void buildRows() {
        rows.clear();
        if (scan == null) return;

        int white  = generalConfigs.TEXT_WHITE;
        int grey   = generalConfigs.TEXT_GRAY;
        int red    = 0xFFFF5555;
        int yellow = 0xFFFFAA00;

        // ── Blocking conditions ───────────────────────────────────────────────
        if (!scan.isSafe()) {
            rows.add(new Row("⚠  Dark spots nearby — monsters may attack!", red));
            rows.add(new Row("   Light up the area before resting.", grey));
            rows.add(new Row("", grey)); // spacer
        }
        if (scan.isWilderness() && !scan.hasCampfire()) {
            rows.add(new Row("⚠  Wilderness rest requires a lit campfire.", red));
            rows.add(new Row("", grey));
        }

        if (!scan.canRest()) return;

        // ── Location ─────────────────────────────────────────────────────────
        if (scan.isWilderness()) {
            rows.add(new Row("✦  Wilderness camp", yellow));
            rows.add(new Row("   Encounter possible — stay alert!", grey));
        } else {
            rows.add(new Row("✦  Sheltered rest", white));
        }
        rows.add(new Row("", grey)); // spacer

        // ── Campfire ─────────────────────────────────────────────────────────
        if (scan.hasCampfire()) {
            String tier = switch (scan.campfireTier()) {
                case 0 -> "Campfire  — no meat (no food bonus)";
                case 1 -> "Campfire  — hunger fully restored";
                case 2 -> "Campfire  — hunger + saturation restored";
                case 3 -> "Campfire  — well fed  (8 min Saturation)";
                default -> "Campfire  — well fed  (16 min Saturation)";
            };
            rows.add(new Row(tier, white));
        } else {
            rows.add(new Row("Campfire  — none in range", grey));
        }

        rows.add(new Row("Anvil           — equipment repair",          scan.hasAnvil()           ? white : grey));
        rows.add(new Row("Furnace         — 50 % of raw ores smelted",  scan.hasFurnace()         ? white : grey));
        rows.add(new Row("Enchanting Table — +2 XP levels",             scan.hasEnchantingTable() ? white : grey));
        rows.add(new Row("Brewing Stand   — ailments cured + buffs",    scan.hasBrewingStand()    ? white : grey));
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partial) {
        g.fillGradient(0, 0, this.width, this.height,
                generalConfigs.COLOR_DEATH_OVERLAY_TOP,
                generalConfigs.COLOR_DEATH_OVERLAY_BOTTOM);

        super.render(g, mouseX, mouseY, partial);

        int cx = this.width / 2;
        int cy = this.height / 2;

        g.drawCenteredString(font, "Long Rest", cx, cy - 70, generalConfigs.COLOR_ACCENT_GOLD);
        g.drawCenteredString(font, "Camp bonuses available tonight",
                cx, cy - 57, generalConfigs.TEXT_GRAY);
        g.fill(cx - 100, cy - 48, cx + 100, cy - 47, 0x55FFFFFF);

        int rowsH = rows.size() * 12;
        int rowY  = cy - 42;
        for (Row row : rows) {
            if (!row.text().isEmpty())
                g.drawCenteredString(font, row.text(), cx, rowY, row.color());
            rowY += 12;
        }
    }

    @Override public boolean shouldCloseOnEsc() { return true; }
}