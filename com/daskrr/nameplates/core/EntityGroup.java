package com.daskrr.nameplates.core;

import org.bukkit.entity.EntityType;

import java.util.UUID;

public class EntityGroup<T> {

    public static final EntityGroup.GroupBuilder<EntityType> ENTITY_TYPE = new GroupBuilder<>(Type.ENTITY_TYPE);
    public static final EntityGroup.GroupBuilder<UUID> ENTITY = new GroupBuilder<>(Type.ENTITY);

    private final Type type;

    private T[] who;
    private UUID[] excluded = null;

    private EntityGroup(Type type) {
        this.type = type;
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

    public static class GroupBuilder<T> {
        private final Type type;
        public GroupBuilder(Type type) {
            this.type = type;
        }

        public EntityGroup<T> create() {
            return new EntityGroup<T>(this.type);
        }
    }

    public enum Type {
        ENTITY_TYPE, ENTITY;
    }
}
