package com.github.mekanismsuitsurvivalunits.service;

import com.github.mekanismsuitsurvivalunits.config.MSSUConfig;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.ModList;

public final class ChaosCompatService {
    public static boolean shouldProtectChaosLaser(Player player, DamageSource source) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return false;
        }
        if (!MSSUConfig.CHAOS_ENABLED.get() || !MSSUConfig.CHAOS_ALLOW_MIXIN_PROTECTION.get()) {
            return false;
        }
        if (!ModList.get().isLoaded("draconicevolution") || !SuitModuleService.hasChaosAnchorEnabled(serverPlayer)) {
            return false;
        }
        if (MSSUConfig.REQUIRE_FULL_MEKASUIT.get() && !SuitModuleService.hasFullMekaSuit(serverPlayer)) {
            return false;
        }
        if (MSSUConfig.CHAOS_REQUIRE_PHASE_GUARD_ACTIVE.get() && !ProtectionService.isPhaseGuardActive(serverPlayer)) {
            return false;
        }
        return !MSSUConfig.CHAOS_STRICT_DRACONIC_ONLY.get() || isDraconicGuardianLaser(source);
    }

    public static boolean protectChaosLaser(Player player, DamageSource source) {
        if (!shouldProtectChaosLaser(player, source)) {
            return false;
        }
        ProtectionService.stabilize(player);
        return true;
    }

    public static boolean protectDirectLaserHealthWrite(Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return false;
        }
        if (!MSSUConfig.CHAOS_ENABLED.get() || !MSSUConfig.CHAOS_ALLOW_MIXIN_PROTECTION.get()) {
            return false;
        }
        if (!ModList.get().isLoaded("draconicevolution") || !SuitModuleService.hasChaosAnchorEnabled(serverPlayer)) {
            return false;
        }
        if (MSSUConfig.REQUIRE_FULL_MEKASUIT.get() && !SuitModuleService.hasFullMekaSuit(serverPlayer)) {
            return false;
        }
        if (MSSUConfig.CHAOS_REQUIRE_PHASE_GUARD_ACTIVE.get() && !ProtectionService.isPhaseGuardActive(serverPlayer)) {
            return false;
        }
        ProtectionService.stabilize(serverPlayer);
        return true;
    }

    private static boolean isDraconicGuardianLaser(DamageSource source) {
        String msgId = source.getMsgId();
        return msgId != null && (msgId.contains("guardian_laser") || msgId.contains("draconicevolution"));
    }

    private ChaosCompatService() {
    }
}
