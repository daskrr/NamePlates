package com.daskrr.nameplates.version.wrapper.entity;

import com.daskrr.nameplates.version.wrapped.entity.WrappedDataWatcher;

public interface EntityWrapper {

    int getId();
    double getHeight();
    String getName();

    WrappedDataWatcher getDataWatcher();
}
