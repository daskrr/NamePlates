package com.daskrr.nameplates.version.v1_8_R1;

import com.daskrr.nameplates.version.v1_8_R1.entity.Entity_v1_8_R1;
import com.daskrr.nameplates.version.wrapped.WrappedUtils;
import com.daskrr.nameplates.version.wrapped.entity.WrappedEntity;
import net.minecraft.server.v1_8_R1.EntityInsentient;

public class Utils_v1_8_R1 implements WrappedUtils {

    @Override
    public boolean isPersistent(WrappedEntity entity) {
        if (!(((Entity_v1_8_R1) entity).getEntity() instanceof EntityInsentient))
            return true; // ??

        return ((EntityInsentient) ((Entity_v1_8_R1) entity).getEntity()).isPersistent();
    }

    @Override
    public void setPersistent(WrappedEntity entity, boolean persistent) {
        if (((Entity_v1_8_R1) entity).getEntity() instanceof EntityInsentient entityInsentient)
            entityInsentient.persistent = persistent;
    }
}
