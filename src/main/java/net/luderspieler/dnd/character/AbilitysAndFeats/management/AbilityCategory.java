package net.luderspieler.dnd.character.AbilitysAndFeats.management;

/**
 * Categories that determine when and how an ability triggers.
 *
 * ONE_TIME_TRIGGER     — Executed once the moment it is added (stat bonuses, permanent unlocks).
 * PLAYER_TRIGGERED     — The player activates it manually via the ability wheel.
 * SELF_TRIGGERED       — Fires automatically on a game event (damage taken, death, hit).
 * ALWAYS_ACTIVE        — Checked and applied every tick interval (Unarmored Defense, Darkvision).
 * SHORT_REST_TRIGGER   — Applied/recharged when the player takes a short rest.
 * LONG_REST_TRIGGER    — Applied/recharged when the player takes a long rest.
 * PASSIVE_TRACKED      — No Minecraft effect; just stored so other systems can check for it.
 */
public enum AbilityCategory {
    ONE_TIME_TRIGGER,
    PLAYER_TRIGGERED,
    SELF_TRIGGERED,
    ALWAYS_ACTIVE,
    SHORT_REST_TRIGGER,
    LONG_REST_TRIGGER,
    PASSIVE_TRACKED
}
