package com.daskrr.nameplates.version.v1_8_R1.entity;

import com.daskrr.nameplates.version.wrapped.entity.WrappedDataWatcher;
import net.minecraft.server.v1_8_R1.DataWatcher;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;

public class DataWatcher_v1_8_R1 implements WrappedDataWatcher {

    private DataWatcher dataWatcher;

    public DataWatcher_v1_8_R1() {  }

    public DataWatcher_v1_8_R1 (DataWatcher dataWatcher) {
        this.dataWatcher = dataWatcher;
    }

    public DataWatcher getDataWatcher() {
        return this.dataWatcher;
    }

    @Override
    public WrappedDataWatcher instantiate(Entity entity) {
        this.dataWatcher = new DataWatcher(((CraftEntity) entity).getHandle());
        return this;
    }
}
