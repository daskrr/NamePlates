package com.daskrr.nameplates.version.v1_8_R1.entity;

import com.daskrr.nameplates.version.wrapped.entity.WrappedEntityLiving;
import net.minecraft.server.v1_8_R1.EntityLiving;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftLivingEntity;
import org.bukkit.entity.LivingEntity;

public class EntityLiving_v1_8_R1 implements WrappedEntityLiving {

    private EntityLiving entityLiving;

    public EntityLiving_v1_8_R1() {  }

    public EntityLiving getEntityLiving() {
        return entityLiving;
    }

    @Override
    public WrappedEntityLiving instantiate(LivingEntity livingEntity) {
        this.entityLiving = ((CraftLivingEntity) livingEntity).getHandle();
        return this;
    }
}
