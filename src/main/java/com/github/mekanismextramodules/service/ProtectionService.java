package com.github.mekanismextramodules.service;

import com.github.mekanismextramodules.MekanismExtraModules;
import com.github.mekanismextramodules.config.ExtraModulesConfig;
import com.github.mekanismextramodules.data.ExtraModulesPlayerData;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

public final class ProtectionService {
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        updatePhaseGuard(player);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        if (isPhaseGuardActive(player)) {
            event.setCanceled(true);
            if (ExtraModulesConfig.PHASE_SET_INVULNERABLE_TIME.get()) {
                event.setInvulnerabilityTicks(2);
            }
            stabilize(player);
            logProtection("Canceled incoming damage from {}", event.getSource().getMsgId());
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onDamagePre(LivingDamageEvent.Pre event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        if (isPhaseGuardActive(player)) {
            event.setNewDamage(0.0F);
            stabilize(player);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onDeath(LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();
        if (!(entity instanceof ServerPlayer player)) {
            return;
        }
        if (isPhaseGuardActive(player)) {
            event.setCanceled(true);
            stabilize(player);
            logProtection("Canceled death from {}", event.getSource().getMsgId());
            return;
        }
        if (tryEmergencyRevival(player, event.getSource())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onClone(PlayerEvent.Clone event) {
        ExtraModulesPlayerData.copyForRespawn(event.getOriginal(), event.getEntity());
    }

    @SubscribeEvent
    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        ExtraModulesPlayerData.setPhaseActive(event.getEntity(), false);
    }

    public static boolean canUsePhaseGuard(ServerPlayer player) {
        if (!ExtraModulesConfig.PHASE_ENABLED.get()) {
            return false;
        }
        if (!SuitModuleService.hasPhaseGuardEnabled(player)) {
            return false;
        }
        if (!isDimensionAllowed(player)) {
            return false;
        }
        return !ExtraModulesConfig.REQUIRE_FULL_MEKASUIT.get() || SuitModuleService.hasFullMekaSuit(player);
    }

    public static long currentPhaseGuardCost(ServerPlayer player) {
        long cost = ExtraModulesConfig.PHASE_ENERGY_COST_PER_TICK.get();
        if (ExtraModulesConfig.CHAOS_ENABLED.get() && SuitModuleService.hasChaosAnchorEnabled(player)) {
            cost = safeAdd(cost, ExtraModulesConfig.CHAOS_EXTRA_ENERGY_COST_PER_TICK.get());
        }
        return cost;
    }

    public static boolean isPhaseGuardActive(ServerPlayer player) {
        return ExtraModulesPlayerData.isPhaseActive(player) && canUsePhaseGuard(player);
    }

    public static boolean tryEmergencyRevival(ServerPlayer player, DamageSource source) {
        if (!ExtraModulesConfig.REVIVAL_ENABLED.get() || !SuitModuleService.hasEmergencyRevival(player)) {
            return false;
        }
        if (ExtraModulesConfig.REQUIRE_FULL_MEKASUIT.get() && !SuitModuleService.hasFullMekaSuit(player)) {
            return false;
        }
        if (!isDimensionAllowed(player)) {
            return false;
        }
        if (!EnergyService.tryConsumeEnergy(player, ExtraModulesConfig.REVIVAL_ENERGY_COST.get(), EnergyContext.EMERGENCY_REVIVAL)) {
            return false;
        }
        player.setHealth((float) Math.max(ExtraModulesConfig.REVIVAL_HEALTH.get(), ExtraModulesConfig.PHASE_MIN_HEALTH.get()));
        stabilize(player);
        player.removeEffect(MobEffects.WITHER);
        player.removeEffect(MobEffects.POISON);
        if (ExtraModulesConfig.REVIVAL_APPLY_TOTEM_EFFECTS.get()) {
            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 160, 1));
            player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 100, 1));
            player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 800, 0));
            player.level().broadcastEntityEvent(player, (byte) 35);
        }
        player.displayClientMessage(Component.translatable("message.mekanism_extra_modules.emergency_revival.triggered"), true);
        logProtection("Emergency Revival triggered for {} from {}", player.getGameProfile().getName(), source.getMsgId());
        return true;
    }

    public static void stabilize(Player player) {
        float minHealth = ExtraModulesConfig.PHASE_MIN_HEALTH.get().floatValue();
        if (player.getHealth() < minHealth) {
            player.setHealth(minHealth);
        }
        player.clearFire();
        player.fallDistance = 0.0F;
        if (ExtraModulesConfig.PHASE_SET_INVULNERABLE_TIME.get()) {
            player.invulnerableTime = Math.max(player.invulnerableTime, 2);
        }
    }

    public static void updatePhaseGuard(ServerPlayer player) {
        boolean wasActive = ExtraModulesPlayerData.isPhaseActive(player);
        boolean shouldBeActive = false;
        if (canUsePhaseGuard(player)) {
            long cost = currentPhaseGuardCost(player);
            shouldBeActive = EnergyService.tryConsumeEnergy(player, cost, EnergyContext.PHASE_GUARD);
        }
        ExtraModulesPlayerData.setPhaseActive(player, shouldBeActive);
        if (shouldBeActive) {
            stabilize(player);
        }
        if (wasActive != shouldBeActive) {
            if (ExtraModulesConfig.DEBUG.get()) {
                MekanismExtraModules.LOGGER.debug("Phase Guard {} for {}", shouldBeActive ? "active" : "inactive", player.getGameProfile().getName());
            }
        }
    }

    private static boolean isDimensionAllowed(ServerPlayer player) {
        String dimension = player.level().dimension().location().toString();
        if (ExtraModulesConfig.BLOCKED_DIMENSIONS.get().contains(dimension)) {
            return false;
        }
        return ExtraModulesConfig.ALLOWED_DIMENSIONS.get().isEmpty() || ExtraModulesConfig.ALLOWED_DIMENSIONS.get().contains(dimension);
    }

    private static long safeAdd(long a, long b) {
        long result = a + b;
        if (((a ^ result) & (b ^ result)) < 0) {
            return Long.MAX_VALUE;
        }
        return result;
    }

    private static void logProtection(String message, Object... args) {
        if (ExtraModulesConfig.LOG_PROTECTION_EVENTS.get()) {
            MekanismExtraModules.LOGGER.info(message, args);
        }
    }

    private ProtectionService() {
    }
}
