package com.github.mekanismextramodules.service;

import mekanism.api.gear.IModule;
import mekanism.api.gear.IModuleContainer;
import mekanism.api.gear.IModuleHelper;
import mekanism.api.gear.ModuleData;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * Looks up this mod's modules on the MekaSuit. All of them are registered for the body armor only.
 */
public final class SuitModuleService {
    private static final ResourceLocation HELMET = ResourceLocation.fromNamespaceAndPath("mekanism", "mekasuit_helmet");
    private static final ResourceLocation BODYARMOR = ResourceLocation.fromNamespaceAndPath("mekanism", "mekasuit_bodyarmor");
    private static final ResourceLocation PANTS = ResourceLocation.fromNamespaceAndPath("mekanism", "mekasuit_pants");
    private static final ResourceLocation BOOTS = ResourceLocation.fromNamespaceAndPath("mekanism", "mekasuit_boots");

    public static boolean hasFullMekaSuit(Player player) {
        return isItem(player.getItemBySlot(EquipmentSlot.HEAD), HELMET)
                && isItem(player.getItemBySlot(EquipmentSlot.CHEST), BODYARMOR)
                && isItem(player.getItemBySlot(EquipmentSlot.LEGS), PANTS)
                && isItem(player.getItemBySlot(EquipmentSlot.FEET), BOOTS);
    }

    public static boolean hasEnabled(Player player, Holder<ModuleData<?>> module) {
        return enabledModule(player, module) != null;
    }

    @Nullable
    public static IModule<?> enabledModule(Player player, Holder<ModuleData<?>> module) {
        IModuleContainer container = IModuleHelper.INSTANCE.getModuleContainer(player, EquipmentSlot.CHEST);
        return container == null ? null : container.getIfEnabled(module);
    }

    public static ItemStack bodyArmor(Player player) {
        return player.getItemBySlot(EquipmentSlot.CHEST);
    }

    private static boolean isItem(ItemStack stack, ResourceLocation id) {
        return !stack.isEmpty() && id.equals(BuiltInRegistries.ITEM.getKey(stack.getItem()));
    }

    private SuitModuleService() {
    }
}
