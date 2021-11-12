package com.daskrr.nameplates.version.v1_8_R1.entity;

import com.daskrr.nameplates.version.wrapped.entity.WrappedDataWatcher;
import com.daskrr.nameplates.version.wrapper.entity.EntityWrapper;
import com.daskrr.nameplates.version.wrapped.entity.WrappedEntity;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftEntity;
import net.minecraft.server.v1_8_R1.Entity;


public class Entity_v1_8_R1 implements WrappedEntity {

    private Entity entity;

    public Entity_v1_8_R1() {  }

    public Entity getEntity() {
        return entity;
    }

    @Override
    public int getId() {
        return this.entity.getId();
    }

    @Override
    public double getHeight() {
        return entity.length;
    }

    @Override
    public String getName() {
        return entity.getName();
    }

    @Override
    public WrappedDataWatcher getDataWatcher() {
        return new DataWatcher_v1_8_R1(this.entity.getDataWatcher());
    }

    @Override
    public EntityWrapper instantiate(org.bukkit.entity.Entity Entity) {
        this.entity = ((CraftEntity) Entity).getHandle();
        return this;
    }
}
