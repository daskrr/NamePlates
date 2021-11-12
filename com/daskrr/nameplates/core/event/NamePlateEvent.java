package com.daskrr.nameplates.core.event;

import com.daskrr.nameplates.api.nameplate.NamePlate;
import org.bukkit.craftbukkit.libs.jline.internal.Nullable;
import org.bukkit.entity.Entity;

public interface NamePlateEvent {
    NamePlate getNamePlate();
    @Nullable
    Entity getEntity();
}
