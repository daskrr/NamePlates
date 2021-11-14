package com.daskrr.nameplates.core;

import java.util.Set;
import java.util.UUID;

public abstract class ContextNamePlate {
    protected abstract void setId(int id);
    protected abstract void setGroup(EntityGroup<?> group);
    protected abstract Set<UUID> getViewersSet();
}
