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

public final class PhaseGuardModule implements ICustomModule<PhaseGuardModule> {
    private static final ResourceLocation ICON = ResourceLocation.fromNamespaceAndPath(SuitSurvivalUnits.MODID, "gui/hud/phase_guard_unit.png");

    @Override
    public void addHUDElements(IModule<PhaseGuardModule> module, IModuleContainer moduleContainer, ItemStack stack, Player player, Consumer<IHUDElement> hudElementAdder) {
        if (!module.isEnabled()) {
            return;
        }
        boolean active = module.canUseEnergy(player, stack, MSSUConfig.PHASE_ENERGY_COST_PER_TICK.get());
        hudElementAdder.accept(IModuleHelper.INSTANCE.hudElement(
                ICON,
                Component.translatable(active
                        ? "hud.mekanism_suit_survival_units.phase_guard.active"
                        : "hud.mekanism_suit_survival_units.phase_guard.inactive"),
                active ? IHUDElement.HUDColor.REGULAR : IHUDElement.HUDColor.FADED
        ));
    }
}
