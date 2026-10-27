package com.github.mekanismextramodules.mixin;

import com.github.mekanismextramodules.service.ProtectionService;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.commands.KillCommand;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Marks kills issued through the vanilla /kill command, which already requires operator permission, so Phase Guard
 * lets them through. Mods that call {@code Entity.kill()} directly are still blocked.
 */
@Mixin(KillCommand.class)
public abstract class KillCommandMixin {
    @WrapOperation(
            method = "kill",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;kill()V")
    )
    private static void mekanismExtraModules$markCommandKill(Entity entity, Operation<Void> original) {
        ProtectionService.runCommandKill(() -> original.call(entity));
    }
}
