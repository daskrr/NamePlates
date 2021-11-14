package com.daskrr.nameplates.core;

import org.bukkit.entity.EntityType;

import java.util.UUID;

public class EntityGroup<T> {

    public static final EntityGroup<EntityType> ENTITY_TYPE = create(new EntityGroup<>(Type.ENTITY_TYPE));
    public static final EntityGroup<UUID> ENTITY = create(new EntityGroup<>(Type.ENTITY));

    private final Type type;

    private T[] who;
    private UUID[] excluded = null;

    private EntityGroup(Type type) {
        this.type = type;
    }
    private EntityGroup(EntityGroup<T> group) {
        this.who = group.who;
        this.type = group.type;
        this.excluded = group.excluded;
    }

    public EntityGroup<T> set(T... who) {
        this.who = who;
        return this;
    }

    // to add to docs: this is only used for Type.ENTITY_TYPE
    public EntityGroup<T> setExcluded(UUID... excluded) {
        if (this.type != Type.ENTITY_TYPE)
            return this;

        this.excluded = excluded;
        return this;
    }

    public T[] get() {
        return this.who;
    }
    public UUID[] getExcluded() {
        return this.excluded;
    }

    public Type getType() {
        return this.type;
    }

    private static <T> EntityGroup<T> create(EntityGroup<T> group) {
        return new EntityGroup<T>(group);
    }

    public enum Type {
        ENTITY_TYPE, ENTITY;
    }
}
