package com.github.mekanismextramodules.service;

import com.github.mekanismextramodules.config.ExtraModulesConfig;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.ModList;

public final class ChaosCompatService {
    public static boolean shouldProtectChaosLaser(Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return false;
        }
        if (!ExtraModulesConfig.CHAOS_ALLOW_MIXIN_PROTECTION.get()) {
            return false;
        }
        if (!ModList.get().isLoaded("draconicevolution") || !ProtectionService.isChaosAnchorActive(serverPlayer)) {
            return false;
        }
        return true;
    }

    public static boolean protectChaosLaser(Player player) {
        if (!shouldProtectChaosLaser(player)) {
            return false;
        }
        ProtectionService.stabilizePhaseGuard(player);
        return true;
    }

    public static boolean protectDirectLaserHealthWrite(Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return false;
        }
        if (!ExtraModulesConfig.CHAOS_ALLOW_MIXIN_PROTECTION.get()) {
            return false;
        }
        if (!ModList.get().isLoaded("draconicevolution") || !ProtectionService.isChaosAnchorActive(serverPlayer)) {
            return false;
        }
        ProtectionService.stabilizePhaseGuard(serverPlayer);
        return true;
    }

    private ChaosCompatService() {
    }
}
