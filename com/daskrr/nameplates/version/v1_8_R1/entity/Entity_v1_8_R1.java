package com.daskrr.nameplates.version.v1_8_R1.entity;

import com.daskrr.nameplates.version.wrapped.entity.WrappedDataWatcher;
import com.daskrr.nameplates.version.wrapper.entity.EntityWrapper;
import com.daskrr.nameplates.version.wrapped.entity.WrappedEntity;
import net.minecraft.server.v1_8_R1.AxisAlignedBB;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftEntity;
import net.minecraft.server.v1_8_R1.Entity;

import java.util.UUID;


public class Entity_v1_8_R1 implements WrappedEntity {

    protected Entity entity;

    public Entity_v1_8_R1() {  }

    public Entity getEntity() {
        return entity;
    }

    public org.bukkit.entity.Entity getBukkitEntity() {
        return this.entity.getBukkitEntity();
    }

    @Override
    public int getId() {
        return this.entity.getId();
    }

    @Override
    public double getHeight() {
        return entity.length;
    }

    @Override
    public double getBBHeight() {
        /*
          C1
            *  *  *
            **    '*
            * *  *' *
            * *   ' *
            M *-  ' *   M = C2M (z of C2 is changed with C1's z ; x of C2 is changed for C1's x)
             **    '*
              *  *  *
                      C2

                      y
                      |
                      |
                      |
                      |
         x -----------o
                       \
                        \
                         z

             a = c1.x
             b = c1.y
             c = c1.z

             d = c2.x
             e = c2.y
             f = c2.z
         */

        AxisAlignedBB boundingBox = entity.getBoundingBox();
        // POINTLESS
//        Location corner1 = new Location((World) entity.getWorld().getWorld(), boundingBox.a, boundingBox.b, boundingBox.c);
//        Location corner2m = new Location((World) entity.getWorld().getWorld(), boundingBox.a, boundingBox.e, boundingBox.c);

        // this could also be done using Math.abs
        double higherY = Math.max(boundingBox.b, boundingBox.e);
        double smallerY = Math.min(boundingBox.b, boundingBox.e);
        return higherY - smallerY;
    }

    @Override
    // BBs have equal width and length (x1-x2 = z1-z2)
    public double getBBWidth() {
        AxisAlignedBB boundingBox = entity.getBoundingBox();

        // this could also be done using Math.abs
        double higherX = Math.max(boundingBox.a, boundingBox.d);
        double smallerX = Math.min(boundingBox.a, boundingBox.d);
        return higherX - smallerX;
    }

    @Override
    public double getPassengerHeight() {
        Entity vehicle = this.entity.vehicle;
        double bbHeight = this.getBBHeight();
        if (vehicle == null)
            return bbHeight;

        // so how do we do this?
        // we take the entity's y position (which is updated even when the entity is mounted)
        // and we extract the part where the bounding boxes (of vehicle and entity) overlap from the entity's bounding box height

        // TODO test that locY is accurate (considering that the entity is mounted)
        // get the end of the vehicle's bb
        double vehicleBBTopY = vehicle.locY + new Entity_v1_8_R1().instantiate(vehicle.getBukkitEntity()).getBBHeight();
        // get overlapped bounding box height (subtracting from the vehicle's top the "feet" of the entity
        double overlappedHeight = vehicleBBTopY - this.entity.locY;

        // return height without overlapped height
        return bbHeight - overlappedHeight;
    }

    @Override
    public String getName() {
        return entity.getName();
    }

    @Override
    public WrappedDataWatcher getWrappedDataWatcher() {
        return new DataWatcher_v1_8_R1(this.entity.getDataWatcher());
    }


    @Override
    public void setPosition(Location location) {
        this.entity.setPosition(location.getX(), location.getY(), location.getZ());
        this.entity.yaw = location.getYaw();
        this.entity.pitch = location.getPitch();
    }

    @Override
    public void setUniqueId(UUID uuid) {
        this.entity.uniqueID = uuid;
    }

    @Override
    public void spawn(World world) {
        ((CraftWorld) world).getHandle().addEntity(this.entity);
    }

    @Override
    public EntityWrapper instantiate(org.bukkit.entity.Entity Entity) {
        this.entity = ((CraftEntity) Entity).getHandle();
        return this;
    }
}
