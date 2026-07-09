package com.github.mekanismsuitsurvivalunits.module;

import com.github.mekanismsuitsurvivalunits.SuitSurvivalUnits;
import com.github.mekanismsuitsurvivalunits.config.MSSUConfig;
import mekanism.api.gear.ICustomModule;
import mekanism.api.gear.IHUDElement;
import mekanism.api.gear.IModule;
import mekanism.api.gear.IModuleContainer;
import mekanism.api.gear.IModuleHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;

public final class EmergencyRevivalModule implements ICustomModule<EmergencyRevivalModule> {
    private static final ResourceLocation ICON = ResourceLocation.fromNamespaceAndPath(SuitSurvivalUnits.MODID, "gui/hud/emergency_revival_unit.png");

    @Override
    public void addHUDElements(IModule<EmergencyRevivalModule> module, IModuleContainer moduleContainer, ItemStack stack, Player player, Consumer<IHUDElement> hudElementAdder) {
        if (!module.isEnabled()) {
            return;
        }
        boolean charged = module.canUseEnergy(player, stack, MSSUConfig.REVIVAL_ENERGY_COST.get());
        hudElementAdder.accept(IModuleHelper.INSTANCE.hudElement(
                ICON,
                Component.translatable(charged
                        ? "hud.mekanism_suit_survival_units.emergency_revival.ready"
                        : "hud.mekanism_suit_survival_units.emergency_revival.no_energy"),
                charged ? IHUDElement.HUDColor.REGULAR : IHUDElement.HUDColor.FADED
        ));
    }
}
