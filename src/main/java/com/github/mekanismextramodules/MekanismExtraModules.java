package com.github.mekanismextramodules;

import com.github.mekanismextramodules.config.ExtraModulesConfig;
import com.github.mekanismextramodules.registry.ExtraModuleRegistry;
import com.github.mekanismextramodules.service.ProtectionService;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.InterModEnqueueEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(MekanismExtraModules.MODID)
public final class MekanismExtraModules {
    public static final String MODID = "mekanism_extra_modules";
    public static final Logger LOGGER = LoggerFactory.getLogger("Mekanism: Extra Modules");

    public MekanismExtraModules(IEventBus modBus, ModContainer modContainer) {
        ExtraModuleRegistry.register(modBus);
        modBus.addListener((InterModEnqueueEvent event) -> event.enqueueWork(ExtraModuleRegistry::sendMekanismIMC));
        modContainer.registerConfig(ModConfig.Type.SERVER, ExtraModulesConfig.SERVER_SPEC);
        NeoForge.EVENT_BUS.register(ProtectionService.class);
    }
}
