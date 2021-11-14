package com.daskrr.nameplates.core.event;

import org.bukkit.entity.Entity;

// fires when a nameplate is attached to an entity every time the nameplate is attached for the first time to the entity
// and when the nameplate reattaches to the entity after being completely un-rendered (all players lost view of the entity)
public interface NamePlateAttachEvent extends NamePlateEvent {
    Entity getEntity();
    // This cancels the entity attach for good. Undo this by using API#allowAttachEntity
    void setCancelled(boolean isCancelled);
    boolean isCancelled();
}
