package com.github.mekanismsuitsurvivalunits.config;

import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

public final class MSSUConfig {
    public static final ModConfigSpec SERVER_SPEC;

    public static final ModConfigSpec.BooleanValue DEBUG;
    public static final ModConfigSpec.BooleanValue LOG_PROTECTION_EVENTS;
    public static final ModConfigSpec.BooleanValue REQUIRE_FULL_MEKASUIT;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> ALLOWED_DIMENSIONS;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> BLOCKED_DIMENSIONS;

    public static final ModConfigSpec.BooleanValue PHASE_ENABLED;
    public static final ModConfigSpec.LongValue PHASE_ENERGY_COST_PER_TICK;
    public static final ModConfigSpec.BooleanValue PHASE_SET_INVULNERABLE_TIME;
    public static final ModConfigSpec.DoubleValue PHASE_MIN_HEALTH;

    public static final ModConfigSpec.BooleanValue CHAOS_ENABLED;
    public static final ModConfigSpec.BooleanValue CHAOS_REQUIRE_PHASE_GUARD_ACTIVE;
    public static final ModConfigSpec.BooleanValue CHAOS_ALLOW_MIXIN_PROTECTION;
    public static final ModConfigSpec.LongValue CHAOS_EXTRA_ENERGY_COST_PER_TICK;
    public static final ModConfigSpec.BooleanValue CHAOS_STRICT_DRACONIC_ONLY;

    public static final ModConfigSpec.BooleanValue REVIVAL_ENABLED;
    public static final ModConfigSpec.LongValue REVIVAL_ENERGY_COST;
    public static final ModConfigSpec.DoubleValue REVIVAL_HEALTH;
    public static final ModConfigSpec.BooleanValue REVIVAL_APPLY_TOTEM_EFFECTS;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.push("general");
        DEBUG = builder.define("debug", false);
        LOG_PROTECTION_EVENTS = builder.define("logProtectionEvents", false);
        REQUIRE_FULL_MEKASUIT = builder.define("requireFullMekaSuit", true);
        ALLOWED_DIMENSIONS = builder.defineListAllowEmpty("allowedDimensions", List.of(), MSSUConfig::isString);
        BLOCKED_DIMENSIONS = builder.defineListAllowEmpty("blockedDimensions", List.of(), MSSUConfig::isString);
        builder.pop();

        builder.push("phaseGuard");
        PHASE_ENABLED = builder.define("enabled", true);
        PHASE_ENERGY_COST_PER_TICK = builder.defineInRange("energyCostPerTick", 500_000L, 0L, Long.MAX_VALUE);
        PHASE_SET_INVULNERABLE_TIME = builder.define("setInvulnerableTime", true);
        PHASE_MIN_HEALTH = builder.defineInRange("minHealth", 1.0D, 0.5D, 1024.0D);
        builder.pop();

        builder.push("chaosAnchor");
        CHAOS_ENABLED = builder.define("enabled", true);
        CHAOS_REQUIRE_PHASE_GUARD_ACTIVE = builder.define("requirePhaseGuardActive", true);
        CHAOS_ALLOW_MIXIN_PROTECTION = builder.define("allowMixinProtection", true);
        CHAOS_EXTRA_ENERGY_COST_PER_TICK = builder.defineInRange("extraEnergyCostPerTick", 0L, 0L, Long.MAX_VALUE);
        CHAOS_STRICT_DRACONIC_ONLY = builder.define("strictDraconicOnly", true);
        builder.pop();

        builder.push("emergencyRevival");
        REVIVAL_ENABLED = builder.define("enabled", true);
        REVIVAL_ENERGY_COST = builder.defineInRange("energyCost", 10_000_000L, 0L, Long.MAX_VALUE);
        REVIVAL_HEALTH = builder.defineInRange("reviveHealth", 4.0D, 1.0D, 1024.0D);
        REVIVAL_APPLY_TOTEM_EFFECTS = builder.define("applyTotemEffects", true);
        builder.pop();

        SERVER_SPEC = builder.build();
    }

    private static boolean isString(Object value) {
        return value instanceof String;
    }

    private MSSUConfig() {
    }
}
