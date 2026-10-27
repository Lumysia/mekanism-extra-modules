package com.github.mekanismextramodules.service;

import mekanism.api.energy.IEnergyConversionHelper;
import mekanism.api.gear.IModule;
import mekanism.api.gear.ModuleData;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public final class EnergyService {
    /**
     * Draws {@code amountFE} from the suit through the given enabled module. Draws nothing and returns false
     * when the module is missing, disabled, or the suit cannot pay the full amount.
     */
    public static boolean tryConsume(Player player, Holder<ModuleData<?>> module, long amountFE) {
        IModule<?> installed = SuitModuleService.enabledModule(player, module);
        if (installed == null) {
            return false;
        }
        if (amountFE <= 0) {
            return true;
        }
        ItemStack stack = SuitModuleService.bodyArmor(player);
        long joules = feToJoules(amountFE);
        if (!installed.canUseEnergy(player, stack, joules)) {
            return false;
        }
        installed.useEnergy(player, stack, joules);
        return true;
    }

    public static long feToJoules(long amountFE) {
        return amountFE <= 0 ? 0L : IEnergyConversionHelper.INSTANCE.feConversion().convertFrom(amountFE);
    }

    private EnergyService() {
    }
}
