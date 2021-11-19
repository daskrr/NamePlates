package com.daskrr.nameplates.version.v1_8_R1.world;

import com.daskrr.nameplates.version.wrapped.world.WrappedRayTrace;
import com.daskrr.nameplates.version.wrapped.world.WrappedRayTraceResult;
import net.minecraft.server.v1_8_R1.Vec3D;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R1.CraftWorld;
import org.bukkit.util.Vector;

public class RayTrace_v1_8_R1 implements WrappedRayTrace {

    @Override
    public WrappedRayTraceResult rayTrace(Location start, Vector direction, double maxDistance) {
        Vector dir = direction.clone().normalize().multiply(maxDistance);
        Vec3D startPos = new Vec3D(start.getX(), start.getY(), start.getZ());
        Vec3D endPos = new Vec3D(start.getX() + dir.getX(), start.getY() + dir.getY(), start.getZ() + dir.getZ());

        //                                                                                                                                    ignorePassableBlocks
        return RayTraceResult_v1_8_R1.fromNMS(start.getWorld(), ((CraftWorld) start.getWorld()).getHandle().rayTrace(startPos, endPos, false, true, false));
    }
}
