package com.daskrr.nameplates.version.wrapped.entity;

import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

public interface WrappedEntityItem extends WrappedEntity {

    void setMotion(double x, double y, double z);
    void setOnGround(boolean onGround);

    WrappedEntityItem instantiate(ItemStack itemStack, Location location);
    WrappedEntityItem instantiate(Item item);
}
