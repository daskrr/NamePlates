package com.daskrr.nameplates.version.wrapper.entity;

import com.daskrr.nameplates.version.wrapped.entity.WrappedDataWatcher;
import org.bukkit.Location;

public interface EntityWrapper {

    int getId();
    double getHeight();
    String getName();

    WrappedDataWatcher getDataWatcher();

    void setPosition(Location location);
}
