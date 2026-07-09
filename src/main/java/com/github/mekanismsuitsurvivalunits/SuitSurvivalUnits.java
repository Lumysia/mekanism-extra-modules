package com.github.mekanismsuitsurvivalunits;

import com.github.mekanismsuitsurvivalunits.config.MSSUConfig;
import com.github.mekanismsuitsurvivalunits.registry.MSSUModules;
import com.github.mekanismsuitsurvivalunits.service.ProtectionService;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.InterModEnqueueEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(SuitSurvivalUnits.MODID)
public final class SuitSurvivalUnits {
    public static final String MODID = "mekanism_suit_survival_units";
    public static final String MOD_NAME = "Mekanism: Suit Survival Units";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    public SuitSurvivalUnits(IEventBus modBus, net.neoforged.fml.ModContainer modContainer) {
        MSSUModules.register(modBus);
        modBus.addListener(this::enqueueIMC);
        modContainer.registerConfig(ModConfig.Type.SERVER, MSSUConfig.SERVER_SPEC);

        NeoForge.EVENT_BUS.register(ProtectionService.class);
        NeoForge.EVENT_BUS.addListener(this::logCompatSummary);
    }

    private void enqueueIMC(InterModEnqueueEvent event) {
        event.enqueueWork(MSSUModules::sendMekanismIMC);
    }

    private void logCompatSummary(ServerStartedEvent event) {
        LOGGER.info("Compat loaded: mekanism={}, mekanismtools={}, draconicevolution={}",
                ModList.get().isLoaded("mekanism"),
                ModList.get().isLoaded("mekanismtools"),
                ModList.get().isLoaded("draconicevolution"));
    }
}
