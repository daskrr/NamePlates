package com.daskrr.nameplates.version.wrapped;

import com.daskrr.nameplates.version.wrapped.entity.WrappedEntity;

public interface WrappedUtils {
    boolean isPersistent(WrappedEntity entity);
    void setPersistent(WrappedEntity entity, boolean persistent);
}
