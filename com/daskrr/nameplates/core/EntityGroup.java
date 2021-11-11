package com.daskrr.nameplates.core;

import org.bukkit.entity.EntityType;

import java.util.UUID;

public class EntityGroup<T> {

    public static final EntityGroup<EntityType> ENTITY_TYPE = new EntityGroup<>("ENTITY_TYPE");
    public static final EntityGroup<UUID> PLAYER = new EntityGroup<>("PLAYER");
    public static final EntityGroup<UUID> ENTITY = new EntityGroup<>("ENTITY");

    private String internalId;

    private T[] who;
    private EntityGroup<?> excluded;

    private EntityGroup(String internalId) {
        this.internalId = internalId;
    }

    public EntityGroup<T> set(T... who) {
        this.who = who;
        return this;
    }
    public EntityGroup<T> setExcluded(EntityGroup<?> excluded) {
        this.excluded = excluded;

        return this;
    }

    public T[] get() {
        return this.who;
    }
    public EntityGroup<?> getExcluded() {
        return this.excluded;
    }

    protected String getInternalId() {
        return this.internalId;
    }
}
