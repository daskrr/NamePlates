package com.daskrr.nameplates.version.wrapped.entity;

import com.daskrr.nameplates.version.wrapper.entity.EntityWrapper;
import org.bukkit.entity.Entity;

public interface WrappedEntity extends EntityWrapper {

    EntityWrapper instantiate(Entity entity);
}
