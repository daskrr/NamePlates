package com.daskrr.nameplates.core;

import com.daskrr.nameplates.api.nameplate.NamePlate;
import com.google.common.collect.Sets;
import org.bukkit.entity.ArmorStand;

import java.util.Set;
import java.util.UUID;

public class RenderedNamePlate {

    private NamePlate plate;
    private final boolean isStatic;
    private ArmorStand[] armorStands;

    private final Set<UUID> viewers = Sets.newHashSet();

    public RenderedNamePlate (NamePlate plate, boolean isStatic) {
        this.plate = plate;
        this.isStatic = isStatic;
    }

    public NamePlate getPlate() {
        return this.plate;
    }
    protected void setPlate(NamePlate plate) {
        this.plate = plate;
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

    public Set<UUID> getViewers() {
        return this.viewers;
    }
}
