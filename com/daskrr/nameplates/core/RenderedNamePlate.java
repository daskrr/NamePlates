package com.daskrr.nameplates.core;

import com.daskrr.nameplates.api.nameplate.NamePlate;
import org.bukkit.entity.ArmorStand;

public class RenderedNamePlate {

    private final NamePlate plate;
    private final boolean isStatic;
    private ArmorStand[] armorStands;

    public RenderedNamePlate (NamePlate plate, boolean isStatic) {
        this.plate = plate;
        this.isStatic = isStatic;
    }

    public NamePlate getPlate() {
        return this.plate;
    }

    public boolean isStatic() {
        return this.isStatic;
    }

    public ArmorStand[] getArmorStands() {
        return this.armorStands;
    }

    public void putArmorStands(ArmorStand... armorStands) {
        this.armorStands = armorStands;
    }
}
