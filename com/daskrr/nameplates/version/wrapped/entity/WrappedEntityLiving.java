package com.daskrr.nameplates.version.wrapped.entity;

import com.daskrr.nameplates.version.wrapper.entity.LivingEntityWrapper;
import org.bukkit.entity.LivingEntity;

public interface WrappedEntityLiving extends LivingEntityWrapper {

    LivingEntityWrapper instantiate(LivingEntity livingEntity);

}
