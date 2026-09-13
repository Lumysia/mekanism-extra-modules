package com.github.mekanismextramodules.client;

import com.github.mekanismextramodules.MekanismExtraModules;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = MekanismExtraModules.MODID, dist = Dist.CLIENT)
public final class MekanismExtraModulesClient {
    public MekanismExtraModulesClient(ModContainer modContainer) {
        modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }
}
