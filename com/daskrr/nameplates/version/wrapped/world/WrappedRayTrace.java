package com.daskrr.nameplates.version.wrapped.world;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public interface WrappedRayTrace {

    WrappedRayTraceResult rayTrace(Location start, Vector direction, double maxDistance);
}
