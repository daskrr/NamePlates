package com.daskrr.nameplates.version.v1_8_R1.entity;

import com.daskrr.nameplates.version.wrapped.entity.WrappedEntityItem;
import net.minecraft.server.v1_8_R1.EntityItem;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftItem;
import org.bukkit.craftbukkit.v1_8_R1.inventory.CraftItemStack;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class EntityItem_v1_8_R1 extends Entity_v1_8_R1 implements WrappedEntityItem {

    public EntityItem_v1_8_R1() {  }

    @Override
    public void setMotion(double x, double y, double z) {
        this.entity.motX = x;
        this.entity.motY = y;
        this.entity.motZ = z;
        this.entity.getBukkitEntity().setVelocity(new Vector(x, y, z));
    }

    @Override
    public void setOnGround(boolean onGround) {
        this.entity.onGround = onGround;
    }

    @Override
    public WrappedEntityItem instantiate(ItemStack itemStack, Location location) {
        this.entity = new EntityItem(((CraftWorld) location.getWorld()).getHandle(), location.getX(), location.getY(), location.getZ(), CraftItemStack.asNMSCopy(itemStack));
        return this;
    }

    @Override
    public WrappedEntityItem instantiate(Item item) {
        this.entity = ((CraftItem) item).getHandle();
        return this;
    }
}
