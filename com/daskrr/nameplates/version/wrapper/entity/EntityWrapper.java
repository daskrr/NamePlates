package com.daskrr.nameplates.version.wrapper.entity;

import com.daskrr.nameplates.version.wrapped.entity.WrappedDataWatcher;
import com.daskrr.nameplates.version.wrapped.entity.WrappedEntity;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import java.util.UUID;

public interface EntityWrapper {

    int getId();
    double getHeight();
    double getBBHeight();
    double getBBWidth();
    double getPassengerHeight();
    String getName();

    Entity getBukkitEntity();

    WrappedDataWatcher getWrappedDataWatcher();

    void setPosition(Location location);
    void setUniqueId(UUID uuid);

    void spawn(World world);
}
