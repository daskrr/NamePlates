package com.daskrr.nameplates.core.event;

import org.bukkit.entity.Entity;

public interface NamePlateAttachEvent extends NamePlateEvent {
    Entity getEntity();
    // This cancels the entity attach for good. Undo this by using API#allowAttachEntity
    void setCancelled(boolean isCancelled);
    boolean isCancelled();
}
