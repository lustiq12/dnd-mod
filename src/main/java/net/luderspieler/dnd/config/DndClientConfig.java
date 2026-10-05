package net.luderspieler.dnd.config;

import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * Client-side config. Read directly at render time; ModConfigSpec values are cached
 * internally so calling .get() every frame is the normal usage pattern.
 */
public class DndClientConfig {

    /** Screen corner/edge the overlay is positioned relative to. */
    public enum Anchor {
        TOP_LEFT, TOP_CENTER, TOP_RIGHT,
        BOTTOM_LEFT, BOTTOM_CENTER, BOTTOM_RIGHT
    }

    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.BooleanValue RESOURCE_OVERLAY_ENABLED;
    public static final ModConfigSpec.EnumValue<Anchor> RESOURCE_OVERLAY_ANCHOR;
    public static final ModConfigSpec.IntValue RESOURCE_OVERLAY_OFFSET_X;
    public static final ModConfigSpec.IntValue RESOURCE_OVERLAY_OFFSET_Y;
    public static final ModConfigSpec.DoubleValue RESOURCE_OVERLAY_SCALE;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        // TODO: add lang file entries for config.dnd.* keys once localization is set up
        builder.comment("Resource pool HUD overlay (Rage, Focus Points, spell slot charges, etc.)")
                .push("resource_overlay");

        RESOURCE_OVERLAY_ENABLED = builder
                .comment("Whether the resource overlay is shown at all.")
                .translation("config.dnd.resource_overlay.enabled")
                .define("enabled", true);

        RESOURCE_OVERLAY_ANCHOR = builder
                .comment("Which screen corner/edge the overlay is positioned relative to.")
                .translation("config.dnd.resource_overlay.anchor")
                .defineEnum("anchor", Anchor.TOP_LEFT);

        RESOURCE_OVERLAY_OFFSET_X = builder
                .comment("Horizontal offset from the chosen anchor, in pixels.")
                .translation("config.dnd.resource_overlay.offset_x")
                .defineInRange("offset_x", 5, 0, 4096);

        RESOURCE_OVERLAY_OFFSET_Y = builder
                .comment("Vertical offset from the chosen anchor, in pixels.")
                .translation("config.dnd.resource_overlay.offset_y")
                .defineInRange("offset_y", 5, 0, 4096);

        RESOURCE_OVERLAY_SCALE = builder
                .comment("Overall scale multiplier for the overlay.")
                .translation("config.dnd.resource_overlay.scale")
                .defineInRange("scale", 1.0, 0.5, 3.0);

        builder.pop();

        SPEC = builder.build();
    }

    private DndClientConfig() {
    }
}