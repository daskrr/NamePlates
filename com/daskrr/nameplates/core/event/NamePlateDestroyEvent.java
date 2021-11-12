package com.daskrr.nameplates.core.event;

import org.bukkit.craftbukkit.libs.jline.internal.Nullable;
import org.bukkit.entity.Entity;

public interface NamePlateDestroyEvent extends NamePlateEvent {

    Cause getCause();
    boolean isPermanentlyDestroyed();

    public enum Cause {
        ENTITY_DEATH,
        ENTITY_DESPAWN,
        MANUAL_REMOVAL
    }
}
