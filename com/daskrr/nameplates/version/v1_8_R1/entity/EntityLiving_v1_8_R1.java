package com.daskrr.nameplates.version.v1_8_R1.entity;

import com.daskrr.nameplates.version.wrapped.entity.WrappedEntityLiving;
import com.daskrr.nameplates.version.wrapper.entity.LivingEntityWrapper;
import net.minecraft.server.v1_8_R1.EntityCreature;
import net.minecraft.server.v1_8_R1.EntityLiving;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftLivingEntity;
import org.bukkit.entity.LivingEntity;

public class EntityLiving_v1_8_R1 extends Entity_v1_8_R1 implements WrappedEntityLiving {

    private EntityLiving entityLiving;

    public EntityLiving_v1_8_R1() {  }

    public EntityLiving getEntityLiving() {
        return entityLiving;
    }

    @Override
    public LivingEntityWrapper instantiate(LivingEntity livingEntity) {
        this.entityLiving = ((CraftLivingEntity) livingEntity).getHandle();
        return this;
    }
}
