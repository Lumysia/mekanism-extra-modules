package com.github.mekanismextramodules.service;

import com.github.mekanismextramodules.registry.ExtraModuleRegistry;
import mekanism.api.gear.IModule;
import mekanism.api.gear.IModuleContainer;
import mekanism.api.gear.IModuleHelper;
import mekanism.api.gear.ModuleData;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public final class EnergyService {
    public static boolean tryConsumeEnergy(ServerPlayer player, long amount, EnergyContext context) {
        if (amount <= 0) {
            return true;
        }
        return consumeFromMekaSuit(player, amount, context, false) >= amount;
    }

    public static long consumeFromMekaSuit(ServerPlayer player, long amount, EnergyContext context, boolean simulate) {
        if (amount <= 0) {
            return 0L;
        }
        ModuleStack moduleStack = findModuleStack(player, moduleFor(context));
        if (moduleStack == null || !moduleStack.module().canUseEnergy(player, moduleStack.stack(), amount)) {
            return 0L;
        }
        if (simulate) {
            return amount;
        }
        moduleStack.module().useEnergy(player, moduleStack.stack(), amount);
        return amount;
    }

    private static ModuleStack findModuleStack(ServerPlayer player, Holder<ModuleData<?>> moduleData) {
        for (EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.CHEST, EquipmentSlot.HEAD, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
            ItemStack stack = player.getItemBySlot(slot);
            if (stack.isEmpty()) {
                continue;
            }
            IModuleContainer container = IModuleHelper.INSTANCE.getModuleContainer(stack);
            if (container != null && container.has(moduleData)) {
                IModule<?> module = container.get(moduleData);
                return new ModuleStack(stack, module);
            }
        }
        return null;
    }

    private static Holder<ModuleData<?>> moduleFor(EnergyContext context) {
        return switch (context) {
            case EMERGENCY_REVIVAL -> ExtraModuleRegistry.EMERGENCY_REVIVAL;
            case CHAOS_ANCHOR -> ExtraModuleRegistry.CHAOS_ANCHOR;
            case PHASE_GUARD -> ExtraModuleRegistry.PHASE_GUARD;
        };
    }

    private EnergyService() {
    }

    private record ModuleStack(ItemStack stack, IModule<?> module) {
    }
}
