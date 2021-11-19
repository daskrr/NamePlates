package com.daskrr.nameplates.version.v1_8_R1;

import com.daskrr.nameplates.version.v1_8_R1.entity.Entity_v1_8_R1;
import com.daskrr.nameplates.version.wrapped.WrappedUtils;
import com.daskrr.nameplates.version.wrapped.entity.WrappedEntity;
import com.google.common.collect.Lists;
import net.minecraft.server.v1_8_R1.EntityInsentient;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class Utils_v1_8_R1 implements WrappedUtils {

    @Override
    public boolean isPersistent(WrappedEntity entity) {
        if (!(((Entity_v1_8_R1) entity).getEntity() instanceof EntityInsentient))
            return true; // ??

        return ((EntityInsentient) ((Entity_v1_8_R1) entity).getEntity()).isPersistent();
    }

    @Override
    public void setPersistent(WrappedEntity entity, boolean persistent) {
        if (((Entity_v1_8_R1) entity).getEntity() instanceof EntityInsentient)
            ((EntityInsentient) ((Entity_v1_8_R1) entity).getEntity()).persistent = persistent;
    }

    private List<Entity> recursiveGetEntityPassengers(Entity entity, List<Entity> previousEntities) {
        if (entity.getPassenger() != null) {
            previousEntities.add(entity.getPassenger());
            return this.recursiveGetEntityPassengers(entity.getPassenger(), previousEntities);
        }

        return previousEntities;
    }

    @Override
    public List<Entity> getEntityPassengers(Entity entity) {
        return this.recursiveGetEntityPassengers(entity, Lists.newArrayList());
    }

    private List<Entity> recursiveGetEntityVehicles(Entity entity, List<Entity> previousEntities) {
        if (entity.getVehicle() != null && entity.isInsideVehicle()) {
            previousEntities.add(entity.getVehicle());
            return this.recursiveGetEntityVehicles(entity.getVehicle(), previousEntities);
        }

        return previousEntities;
    }

    @Override
    public List<Entity> getEntityVehicles(Entity entity) {
        return this.recursiveGetEntityVehicles(entity, Lists.newArrayList());
    }

    @Override
    public ItemStack getItemInMainHand(LivingEntity entity) {
        return entity.getEquipment().getItemInHand();
    }

    @Override
    public ItemStack getItemInOffHand(LivingEntity entity) {
        return null;
    }
}
