package com.github.mekanismsuitsurvivalunits.data;

import com.github.mekanismsuitsurvivalunits.SuitSurvivalUnits;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

public final class MSSUPlayerData {
    private static final String PHASE_ACTIVE = "phaseGuardActive";

    public static CompoundTag root(Player player) {
        CompoundTag persistent = player.getPersistentData();
        if (!persistent.contains(SuitSurvivalUnits.MODID)) {
            persistent.put(SuitSurvivalUnits.MODID, new CompoundTag());
        }
        return persistent.getCompound(SuitSurvivalUnits.MODID);
    }

    public static boolean isPhaseActive(Player player) {
        return root(player).getBoolean(PHASE_ACTIVE);
    }

    public static void setPhaseActive(Player player, boolean active) {
        root(player).putBoolean(PHASE_ACTIVE, active);
    }

    public static void copyForRespawn(Player original, Player clone) {
        CompoundTag from = root(original).copy();
        root(clone).merge(from);
        setPhaseActive(clone, false);
    }

    private MSSUPlayerData() {
    }
}
