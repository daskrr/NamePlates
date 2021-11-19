package com.daskrr.nameplates.version.wrapped;

import com.daskrr.nameplates.version.wrapped.entity.WrappedEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface WrappedUtils {
    boolean isPersistent(WrappedEntity entity);
    void setPersistent(WrappedEntity entity, boolean persistent);
    List<Entity> getEntityPassengers(Entity entity);
    List<Entity> getEntityVehicles(Entity entity);

    ItemStack getItemInMainHand(LivingEntity entity);
    ItemStack getItemInOffHand(LivingEntity entity);
}
