package com.github.mekanismextramodules.service;

import com.github.mekanismextramodules.MekanismExtraModules;
import com.github.mekanismextramodules.config.ExtraModulesConfig;
import com.github.mekanismextramodules.registry.ExtraModuleRegistry;
import mekanism.api.gear.ModuleData;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityInvulnerabilityCheckEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class ProtectionService {
    /** Players whose Phase Guard was paid for on their last server tick. Runtime state only, never saved. */
    private static final Set<UUID> PHASE_ACTIVE = ConcurrentHashMap.newKeySet();
    /** True while the /kill command is killing its targets. Commands run on the server thread. */
    private static boolean commandKill;

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            updatePhaseGuard(player);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onInvulnerabilityCheck(EntityInvulnerabilityCheckEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || isCommandKill(event.getSource()) || !isPhaseGuardActive(player)) {
            return;
        }
        event.setInvulnerable(true);
        if (!event.getOriginalInvulnerability()) {
            if (ExtraModulesConfig.PHASE_SET_INVULNERABLE_TIME.get()) {
                player.invulnerableTime = Math.max(player.invulnerableTime, ExtraModulesConfig.PHASE_INVULNERABILITY_TICKS.get());
            }
            logProtection("Blocked damage from {}", event.getSource().getMsgId());
        }
    }

    /** Catches damage applied without the invulnerability check, for example by other mods calling actuallyHurt. */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onDamagePre(LivingDamageEvent.Pre event) {
        if (event.getEntity() instanceof ServerPlayer player && !isCommandKill(event.getSource()) && isPhaseGuardActive(player)) {
            event.setNewDamage(0.0F);
            stabilizePhaseGuard(player);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || isCommandKill(event.getSource())) {
            return;
        }
        if (isPhaseGuardActive(player)) {
            event.setCanceled(true);
            stabilizePhaseGuard(player);
            logProtection("Canceled death from {}", event.getSource().getMsgId());
        } else if (tryEmergencyRevival(player, event.getSource())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        PHASE_ACTIVE.remove(event.getEntity().getUUID());
    }

    /** Called by the Draconic Evolution mixin. Returns true when the laser's direct health write must be skipped. */
    public static boolean absorbChaosLaser(Player player) {
        if (!(player instanceof ServerPlayer serverPlayer) || !isChaosAnchorActive(serverPlayer)) {
            return false;
        }
        stabilizePhaseGuard(serverPlayer);
        logProtection("Chaos Anchor absorbed the Chaos Guardian laser");
        return true;
    }

    public static boolean canUsePhaseGuard(Player player) {
        return ExtraModulesConfig.PHASE_ENABLED.get() && canUse(player, ExtraModuleRegistry.PHASE_GUARD);
    }

    public static boolean canUseEmergencyRevival(Player player) {
        return ExtraModulesConfig.REVIVAL_ENABLED.get() && canUse(player, ExtraModuleRegistry.EMERGENCY_REVIVAL);
    }

    private static boolean isPhaseGuardActive(ServerPlayer player) {
        return PHASE_ACTIVE.contains(player.getUUID()) && canUsePhaseGuard(player);
    }

    private static boolean isChaosAnchorActive(ServerPlayer player) {
        return ExtraModulesConfig.CHAOS_ENABLED.get()
                && SuitModuleService.hasEnabled(player, ExtraModuleRegistry.CHAOS_ANCHOR)
                && isPhaseGuardActive(player);
    }

    /** Shared gate: the module is enabled on the suit, the dimension allows it, and the suit requirement passes. */
    private static boolean canUse(Player player, Holder<ModuleData<?>> module) {
        return SuitModuleService.hasEnabled(player, module)
                && isDimensionAllowed(player)
                && (!ExtraModulesConfig.REQUIRE_FULL_MEKASUIT.get() || SuitModuleService.hasFullMekaSuit(player));
    }

    private static void updatePhaseGuard(ServerPlayer player) {
        boolean active = canUsePhaseGuard(player)
                && EnergyService.tryConsume(player, ExtraModuleRegistry.PHASE_GUARD, ExtraModulesConfig.PHASE_ENERGY_COST_PER_TICK.get());
        boolean changed = active ? PHASE_ACTIVE.add(player.getUUID()) : PHASE_ACTIVE.remove(player.getUUID());
        if (active) {
            stabilizePhaseGuard(player);
        }
        if (changed) {
            logProtection("Phase Guard {} for {}", active ? "active" : "inactive", player.getGameProfile().getName());
        }
    }

    private static boolean tryEmergencyRevival(ServerPlayer player, DamageSource source) {
        if (!canUseEmergencyRevival(player)
                || !EnergyService.tryConsume(player, ExtraModuleRegistry.EMERGENCY_REVIVAL, ExtraModulesConfig.REVIVAL_ENERGY_COST.get())) {
            return false;
        }
        float restoredHealth = (float) Math.max(
                ExtraModulesConfig.REVIVAL_HEALTH.get(),
                player.getMaxHealth() * ExtraModulesConfig.REVIVAL_HEALTH_PERCENTAGE.get()
        );
        player.setHealth(Math.min(player.getMaxHealth(), restoredHealth));
        if (ExtraModulesConfig.REVIVAL_CLEAR_FIRE.get()) {
            player.clearFire();
        }
        if (ExtraModulesConfig.REVIVAL_RESET_FALL_DISTANCE.get()) {
            player.fallDistance = 0.0F;
        }
        player.invulnerableTime = Math.max(player.invulnerableTime, ExtraModulesConfig.REVIVAL_INVULNERABILITY_TICKS.get());
        if (ExtraModulesConfig.REVIVAL_CURE_HARMFUL_EFFECTS.get()) {
            player.getActiveEffects().stream()
                    .map(MobEffectInstance::getEffect)
                    .filter(effect -> effect.value().getCategory() == MobEffectCategory.HARMFUL)
                    .toList()
                    .forEach(player::removeEffect);
        }
        if (ExtraModulesConfig.REVIVAL_APPLY_TOTEM_EFFECTS.get()) {
            addEffect(player, MobEffects.REGENERATION, ExtraModulesConfig.REVIVAL_REGENERATION_DURATION.get(), ExtraModulesConfig.REVIVAL_REGENERATION_AMPLIFIER.get());
            addEffect(player, MobEffects.ABSORPTION, ExtraModulesConfig.REVIVAL_ABSORPTION_DURATION.get(), ExtraModulesConfig.REVIVAL_ABSORPTION_AMPLIFIER.get());
            addEffect(player, MobEffects.FIRE_RESISTANCE, ExtraModulesConfig.REVIVAL_FIRE_RESISTANCE_DURATION.get(), ExtraModulesConfig.REVIVAL_FIRE_RESISTANCE_AMPLIFIER.get());
        }
        if (ExtraModulesConfig.REVIVAL_PLAY_TOTEM_ANIMATION.get()) {
            // Entity event 35 is the vanilla Totem of Undying activation (particles, sound, item animation).
            player.level().broadcastEntityEvent(player, (byte) 35);
        }
        if (ExtraModulesConfig.REVIVAL_SHOW_MESSAGE.get()) {
            player.displayClientMessage(Component.translatable("message.mekanism_extra_modules.emergency_revival.triggered"), true);
        }
        logProtection("Emergency Revival triggered for {} from {}", player.getGameProfile().getName(), source.getMsgId());
        return true;
    }

    private static void stabilizePhaseGuard(Player player) {
        float minHealth = ExtraModulesConfig.PHASE_MIN_HEALTH.get().floatValue();
        if (player.getHealth() < minHealth) {
            player.setHealth(minHealth);
        }
        if (ExtraModulesConfig.PHASE_CLEAR_FIRE.get()) {
            player.clearFire();
        }
        if (ExtraModulesConfig.PHASE_RESET_FALL_DISTANCE.get()) {
            player.fallDistance = 0.0F;
        }
    }

    private static void addEffect(ServerPlayer player, Holder<MobEffect> effect, int duration, int amplifier) {
        if (duration > 0) {
            player.addEffect(new MobEffectInstance(effect, duration, amplifier));
        }
    }

    private static boolean isDimensionAllowed(Player player) {
        String dimension = player.level().dimension().location().toString();
        if (ExtraModulesConfig.BLOCKED_DIMENSIONS.get().contains(dimension)) {
            return false;
        }
        return ExtraModulesConfig.ALLOWED_DIMENSIONS.get().isEmpty() || ExtraModulesConfig.ALLOWED_DIMENSIONS.get().contains(dimension);
    }

    /** Runs a kill issued by the /kill command; Phase Guard and Emergency Revival let it through. */
    public static void runCommandKill(Runnable kill) {
        boolean previous = commandKill;
        commandKill = true;
        try {
            kill.run();
        } finally {
            commandKill = previous;
        }
    }

    private static boolean isCommandKill(DamageSource source) {
        return commandKill && source.is(DamageTypes.GENERIC_KILL);
    }

    private static void logProtection(String message, Object... args) {
        if (ExtraModulesConfig.LOG_PROTECTION_EVENTS.get()) {
            MekanismExtraModules.LOGGER.info(message, args);
        }
    }

    private ProtectionService() {
    }
}
