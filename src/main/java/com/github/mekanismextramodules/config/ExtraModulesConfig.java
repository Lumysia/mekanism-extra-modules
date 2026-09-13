package com.github.mekanismextramodules.config;

import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

public final class ExtraModulesConfig {
    private static final String TRANSLATION_PREFIX = "mekanism_extra_modules.configuration.";
    public static final ModConfigSpec SERVER_SPEC;

    public static final ModConfigSpec.BooleanValue DEBUG;
    public static final ModConfigSpec.BooleanValue LOG_PROTECTION_EVENTS;
    public static final ModConfigSpec.BooleanValue REQUIRE_FULL_MEKASUIT;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> ALLOWED_DIMENSIONS;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> BLOCKED_DIMENSIONS;

    public static final ModConfigSpec.BooleanValue PHASE_ENABLED;
    public static final ModConfigSpec.LongValue PHASE_ENERGY_COST_PER_TICK;
    public static final ModConfigSpec.BooleanValue PHASE_SET_INVULNERABLE_TIME;
    public static final ModConfigSpec.IntValue PHASE_INVULNERABILITY_TICKS;
    public static final ModConfigSpec.DoubleValue PHASE_MIN_HEALTH;
    public static final ModConfigSpec.BooleanValue PHASE_CLEAR_FIRE;
    public static final ModConfigSpec.BooleanValue PHASE_RESET_FALL_DISTANCE;

    public static final ModConfigSpec.BooleanValue CHAOS_ENABLED;
    public static final ModConfigSpec.BooleanValue CHAOS_ALLOW_MIXIN_PROTECTION;

    public static final ModConfigSpec.BooleanValue REVIVAL_ENABLED;
    public static final ModConfigSpec.LongValue REVIVAL_ENERGY_COST;
    public static final ModConfigSpec.DoubleValue REVIVAL_HEALTH;
    public static final ModConfigSpec.DoubleValue REVIVAL_HEALTH_PERCENTAGE;
    public static final ModConfigSpec.IntValue REVIVAL_INVULNERABILITY_TICKS;
    public static final ModConfigSpec.BooleanValue REVIVAL_CLEAR_FIRE;
    public static final ModConfigSpec.BooleanValue REVIVAL_RESET_FALL_DISTANCE;
    public static final ModConfigSpec.BooleanValue REVIVAL_CURE_HARMFUL_EFFECTS;
    public static final ModConfigSpec.BooleanValue REVIVAL_APPLY_TOTEM_EFFECTS;
    public static final ModConfigSpec.IntValue REVIVAL_REGENERATION_DURATION;
    public static final ModConfigSpec.IntValue REVIVAL_REGENERATION_AMPLIFIER;
    public static final ModConfigSpec.IntValue REVIVAL_ABSORPTION_DURATION;
    public static final ModConfigSpec.IntValue REVIVAL_ABSORPTION_AMPLIFIER;
    public static final ModConfigSpec.IntValue REVIVAL_FIRE_RESISTANCE_DURATION;
    public static final ModConfigSpec.IntValue REVIVAL_FIRE_RESISTANCE_AMPLIFIER;
    public static final ModConfigSpec.BooleanValue REVIVAL_PLAY_TOTEM_ANIMATION;
    public static final ModConfigSpec.BooleanValue REVIVAL_SHOW_MESSAGE;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.translation(TRANSLATION_PREFIX + "general").push("general");
        builder.comment("Enables additional diagnostic logging. Leave disabled during normal play.");
        DEBUG = builder.translation(TRANSLATION_PREFIX + "general.debug").define("debug", false);
        builder.comment("Logs every blocked damage or death event to the server log.");
        LOG_PROTECTION_EVENTS = builder.translation(TRANSLATION_PREFIX + "general.log_protection_events").define("logProtectionEvents", false);
        builder.comment("Requires all four MekaSuit armor pieces for every protection module.");
        REQUIRE_FULL_MEKASUIT = builder.translation(TRANSLATION_PREFIX + "general.require_full_mekasuit").define("requireFullMekaSuit", true);
        builder.comment("Dimensions where modules may work. Empty allows every dimension not listed below.");
        ALLOWED_DIMENSIONS = builder.translation(TRANSLATION_PREFIX + "general.allowed_dimensions").defineListAllowEmpty("allowedDimensions", List.of(), ExtraModulesConfig::isString);
        builder.comment("Dimensions where protection modules never work. Entries here override allowedDimensions.");
        BLOCKED_DIMENSIONS = builder.translation(TRANSLATION_PREFIX + "general.blocked_dimensions").defineListAllowEmpty("blockedDimensions", List.of(), ExtraModulesConfig::isString);
        builder.pop();

        builder.translation(TRANSLATION_PREFIX + "phase_guard").push("phaseGuard");
        builder.comment("Enables the Phase Guard Unit.");
        PHASE_ENABLED = builder.translation(TRANSLATION_PREFIX + "phase_guard.enabled").define("enabled", true);
        builder.comment("Forge Energy (FE) consumed each tick while Phase Guard is active. 20 ticks = 1 second.");
        PHASE_ENERGY_COST_PER_TICK = builder.translation(TRANSLATION_PREFIX + "phase_guard.energy_cost_per_tick").defineInRange("energyCostPerTick", 500_000L, 0L, Long.MAX_VALUE);
        builder.comment("Applies vanilla invulnerability time when Phase Guard blocks damage.");
        PHASE_SET_INVULNERABLE_TIME = builder.translation(TRANSLATION_PREFIX + "phase_guard.set_invulnerable_time").define("setInvulnerableTime", true);
        builder.comment("Minimum invulnerability time maintained after protection. Only used when setInvulnerableTime is true.");
        PHASE_INVULNERABILITY_TICKS = builder.translation(TRANSLATION_PREFIX + "phase_guard.invulnerability_ticks").defineInRange("invulnerabilityTicks", 2, 0, 200);
        builder.comment("Minimum health maintained while Phase Guard is active. 2 health = 1 heart.");
        PHASE_MIN_HEALTH = builder.translation(TRANSLATION_PREFIX + "phase_guard.min_health").defineInRange("minHealth", 1.0D, 0.5D, 1024.0D);
        builder.comment("Extinguishes the player while Phase Guard is active.");
        PHASE_CLEAR_FIRE = builder.translation(TRANSLATION_PREFIX + "phase_guard.clear_fire").define("clearFire", true);
        builder.comment("Prevents accumulated fall damage while Phase Guard is active.");
        PHASE_RESET_FALL_DISTANCE = builder.translation(TRANSLATION_PREFIX + "phase_guard.reset_fall_distance").define("resetFallDistance", true);
        builder.pop();

        builder.translation(TRANSLATION_PREFIX + "chaos_anchor").push("chaosAnchor");
        builder.comment("Enables the Chaos Anchor Unit and its optional Draconic Evolution integration.");
        CHAOS_ENABLED = builder.translation(TRANSLATION_PREFIX + "chaos_anchor.enabled").define("enabled", true);
        builder.comment("Allows the optional mixin that intercepts Chaos Guardian direct health writes and forced death.");
        CHAOS_ALLOW_MIXIN_PROTECTION = builder.translation(TRANSLATION_PREFIX + "chaos_anchor.allow_mixin_protection").define("allowMixinProtection", true);
        builder.pop();

        builder.translation(TRANSLATION_PREFIX + "emergency_revival").push("emergencyRevival");
        builder.comment("Enables the Emergency Revival Unit.");
        REVIVAL_ENABLED = builder.translation(TRANSLATION_PREFIX + "emergency_revival.enabled").define("enabled", true);
        builder.comment("Forge Energy (FE) consumed each time Emergency Revival prevents death.");
        REVIVAL_ENERGY_COST = builder.translation(TRANSLATION_PREFIX + "emergency_revival.energy_cost").defineInRange("energyCost", 10_000_000L, 0L, Long.MAX_VALUE);
        builder.comment("Flat health restored after revival. 2 health = 1 heart.");
        REVIVAL_HEALTH = builder.translation(TRANSLATION_PREFIX + "emergency_revival.revive_health").defineInRange("reviveHealth", 4.0D, 1.0D, 1024.0D);
        builder.comment("Fraction of maximum health restored after revival. The greater of this value and reviveHealth is used. Set to 0 to disable.");
        REVIVAL_HEALTH_PERCENTAGE = builder.translation(TRANSLATION_PREFIX + "emergency_revival.revive_health_percentage").defineInRange("reviveHealthPercentage", 0.0D, 0.0D, 1.0D);
        builder.comment("Invulnerability time granted after revival.");
        REVIVAL_INVULNERABILITY_TICKS = builder.translation(TRANSLATION_PREFIX + "emergency_revival.invulnerability_ticks").defineInRange("invulnerabilityTicks", 20, 0, 1200);
        builder.comment("Extinguishes the player after revival.");
        REVIVAL_CLEAR_FIRE = builder.translation(TRANSLATION_PREFIX + "emergency_revival.clear_fire").define("clearFire", true);
        builder.comment("Clears accumulated fall distance after revival.");
        REVIVAL_RESET_FALL_DISTANCE = builder.translation(TRANSLATION_PREFIX + "emergency_revival.reset_fall_distance").define("resetFallDistance", true);
        builder.comment("Removes all active harmful effects after revival, including effects added by other mods.");
        REVIVAL_CURE_HARMFUL_EFFECTS = builder.translation(TRANSLATION_PREFIX + "emergency_revival.cure_harmful_effects").define("cureHarmfulEffects", true);
        builder.comment("Applies the configurable regeneration, absorption, and fire resistance effects below.");
        REVIVAL_APPLY_TOTEM_EFFECTS = builder.translation(TRANSLATION_PREFIX + "emergency_revival.apply_totem_effects").define("applyTotemEffects", true);
        builder.comment("Regeneration duration in ticks. Set to 0 to disable this effect.");
        REVIVAL_REGENERATION_DURATION = builder.translation(TRANSLATION_PREFIX + "emergency_revival.regeneration_duration").defineInRange("regenerationDuration", 160, 0, 72_000);
        builder.comment("Regeneration amplifier, where 0 is level I and 1 is level II.");
        REVIVAL_REGENERATION_AMPLIFIER = builder.translation(TRANSLATION_PREFIX + "emergency_revival.regeneration_amplifier").defineInRange("regenerationAmplifier", 1, 0, 255);
        builder.comment("Absorption duration in ticks. Set to 0 to disable this effect.");
        REVIVAL_ABSORPTION_DURATION = builder.translation(TRANSLATION_PREFIX + "emergency_revival.absorption_duration").defineInRange("absorptionDuration", 100, 0, 72_000);
        builder.comment("Absorption amplifier, where 0 is level I and 1 is level II.");
        REVIVAL_ABSORPTION_AMPLIFIER = builder.translation(TRANSLATION_PREFIX + "emergency_revival.absorption_amplifier").defineInRange("absorptionAmplifier", 1, 0, 255);
        builder.comment("Fire resistance duration in ticks. Set to 0 to disable this effect.");
        REVIVAL_FIRE_RESISTANCE_DURATION = builder.translation(TRANSLATION_PREFIX + "emergency_revival.fire_resistance_duration").defineInRange("fireResistanceDuration", 800, 0, 72_000);
        builder.comment("Fire resistance amplifier, where 0 is level I.");
        REVIVAL_FIRE_RESISTANCE_AMPLIFIER = builder.translation(TRANSLATION_PREFIX + "emergency_revival.fire_resistance_amplifier").defineInRange("fireResistanceAmplifier", 0, 0, 255);
        builder.comment("Plays the vanilla Totem of Undying animation and sound after revival.");
        REVIVAL_PLAY_TOTEM_ANIMATION = builder.translation(TRANSLATION_PREFIX + "emergency_revival.play_totem_animation").define("playTotemAnimation", true);
        builder.comment("Shows the Emergency Revival activation message above the hotbar.");
        REVIVAL_SHOW_MESSAGE = builder.translation(TRANSLATION_PREFIX + "emergency_revival.show_activation_message").define("showActivationMessage", true);
        builder.pop();

        SERVER_SPEC = builder.build();
    }

    private static boolean isString(Object value) {
        return value instanceof String;
    }

    private ExtraModulesConfig() {
    }
}
