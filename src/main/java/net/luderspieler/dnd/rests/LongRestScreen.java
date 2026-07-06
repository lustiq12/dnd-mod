package net.luderspieler.dnd.rests;

import net.luderspieler.dnd.generalConfigs;
import net.luderspieler.dnd.network.DndModVariables;
import net.luderspieler.dnd.spells.SpellPrepScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;

/**
 * Screen 2 of the Long Rest flow — shown AFTER the player wakes up.
 *
 * If wasSuccessful=true:
 *   - Shows spell-prep button (spellcasters only)
 *   - "Finish Long Rest" sends ApplyLongRestPacket → server applies all benefits
 *
 * If wasSuccessful=false (rest was interrupted):
 *   - Shows a rest-failed notice
 *   - "Close" dismisses without applying anything
 */
public class LongRestScreen extends Screen {

    private final BlockPos bedPos;
    private final boolean  wasSuccessful;

    public LongRestScreen(BlockPos bedPos, boolean wasSuccessful) {
        super(Component.literal("Long Rest"));
        this.bedPos        = bedPos;
        this.wasSuccessful = wasSuccessful;
    }

    @Override
    protected void init() {
        super.init();

        int cx     = this.width  / 2;
        int cy     = this.height / 2;
        int btnTop = cy + 20;

        if (wasSuccessful) {
            // "Change Spells" — spellcasters only.
            if (this.minecraft.player != null) {
                var vars = this.minecraft.player
                        .getData(DndModVariables.PLAYER_VARIABLES);
                if (vars.CanUseMagic) {
                    this.addRenderableWidget(Button.builder(
                            Component.literal("Change Spells"),
                            btn -> this.minecraft.setScreen(new SpellPrepScreen(this))
                    ).bounds(cx - 75, btnTop, 150, 20).build());
                    btnTop += 24;
                }
            }

            // "Finish Long Rest" — applies all benefits server-side.
            this.addRenderableWidget(Button.builder(
                    Component.literal("Finish Long Rest"),
                    btn -> {
                        ApplyLongRestPacket.send(bedPos);
                        this.onClose();
                    }
            ).bounds(cx - 75, btnTop, 150, 20).build());

        } else {
            // Rest failed — just a dismiss button.
            this.addRenderableWidget(Button.builder(
                    Component.literal("Close"),
                    btn -> this.onClose()
            ).bounds(cx - 75, btnTop, 150, 20).build());
        }
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partial) {
        g.fillGradient(0, 0, this.width, this.height,
                generalConfigs.COLOR_DEATH_OVERLAY_TOP,
                generalConfigs.COLOR_DEATH_OVERLAY_BOTTOM);

        super.render(g, mouseX, mouseY, partial);

        int cx = this.width  / 2;
        int cy = this.height / 2;

        // Title.
        g.drawCenteredString(font, "Long Rest",
                cx, cy - 60, generalConfigs.COLOR_ACCENT_GOLD);

        if (wasSuccessful) {
            // Subtitle.
            g.drawCenteredString(font,
                    "You wake up refreshed. Prepare for the day ahead.",
                    cx, cy - 46, generalConfigs.TEXT_GRAY);

            // Divider.
            g.fill(cx - 90, cy - 37, cx + 90, cy - 36, 0x55FFFFFF);

            // Hint: bonuses will be applied on finish.
            g.drawCenteredString(font,
                    "Bonuses will be applied when you finish.",
                    cx, cy - 28, generalConfigs.TEXT_GRAY);
            g.drawCenteredString(font,
                    "Prepare your spells for the day first.",
                    cx, cy - 16, generalConfigs.TEXT_GRAY);

        } else {
            // Rest-failed state.
            g.drawCenteredString(font,
                    "§cYour rest was interrupted.",
                    cx, cy - 46, 0xFFFF5555);
            g.drawCenteredString(font,
                    "No long rest benefits will be applied.",
                    cx, cy - 32, generalConfigs.TEXT_GRAY);
        }
    }

    @Override
    public boolean shouldCloseOnEsc() { return false; }
}