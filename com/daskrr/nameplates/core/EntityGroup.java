package com.daskrr.nameplates.core;

import org.bukkit.entity.EntityType;

import java.util.UUID;

public class EntityGroup<T> {

    public static final EntityGroup<EntityType> ENTITY_TYPE = new EntityGroup<>(Type.ENTITY_TYPE);
    public static final EntityGroup<UUID> ENTITY = new EntityGroup<>(Type.ENTITY);

    private final Type type;

    private T[] who;
    private EntityGroup<?> excluded;

    private EntityGroup(Type type) {
        this.type = type;
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

    public Type getType() {
        return this.type;
    }

    public static enum Type {
        ENTITY_TYPE, ENTITY;
    }
}
