package com.github.mekanismextramodules.mixin;

import com.github.mekanismextramodules.service.ProtectionService;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * The Chaos Guardian's charged laser writes health directly and calls {@code die} itself, so neither damage nor
 * death events can stop it. Skip both calls while the target's Chaos Anchor is active.
 */
@Mixin(targets = "com.brandon3055.draconicevolution.entity.guardian.control.LaserBeamPhase", remap = false)
public abstract class LaserBeamPhaseMixin {
    @WrapWithCondition(
            method = "serverTick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;setHealth(F)V")
    )
    private boolean mekanismExtraModules$guardSetHealth(Player player, float health) {
        return !ProtectionService.absorbChaosLaser(player);
    }

    @WrapWithCondition(
            method = "serverTick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;die(Lnet/minecraft/world/damagesource/DamageSource;)V")
    )
    private boolean mekanismExtraModules$guardDie(Player player, DamageSource source) {
        return !ProtectionService.absorbChaosLaser(player);
    }
}
