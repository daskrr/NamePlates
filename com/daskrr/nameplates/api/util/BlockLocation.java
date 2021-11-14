package com.daskrr.nameplates.api.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

public class BlockLocation extends Location {
    // PARTLY PORTED FROM BUKKIT
    public BlockLocation(Location location) {
        this(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public BlockLocation(World world, int x, int y, int z) {
        super(world, x, y, z, 0.0F, 0.0F);
    }

    @Override
    public void setX(double x) {
        super.setX((int) x);
    }

    @Override
    public void setY(double y) {
        super.setY((int) y);
    }

    @Override
    public void setZ(double z) {
        super.setZ((int) z);
    }

    @Override
    public void setYaw(float yaw) {
        super.setYaw(0.0F);
    }

    @Override
    public void setPitch(float pitch) {
        super.setPitch(0.0F);
    }

    @Override
    public BlockLocation setDirection(Vector vector) {
        // modification of pitch and yaw is disallowed
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        } else if (this.getClass() != obj.getClass()) {
            return false;
        } else {
            Location other = (Location)obj;
            if (this.getWorld() != other.getWorld() && (this.getWorld() == null || !this.getWorld().equals(other.getWorld()))) {
                return false;
            } else if (Double.doubleToLongBits(this.getBlockX()) != Double.doubleToLongBits(other.getBlockX())) {
                return false;
            } else if (Double.doubleToLongBits(this.getBlockY()) != Double.doubleToLongBits(other.getBlockY())) {
                return false;
            } else if (Double.doubleToLongBits(this.getBlockZ()) != Double.doubleToLongBits(other.getBlockZ())) {
                return false;
            }
            // ignore pitch and yaw
//            } else if (Float.floatToIntBits(this.pitch) != Float.floatToIntBits(other.pitch)) {
//                return false;
//            } else {
//                return Float.floatToIntBits(this.yaw) == Float.floatToIntBits(other.yaw);
//            }
        }

        return true;
    }

    @Override
    public String toString() {
        return "BlockLocation{world=" + this.getWorld() + ",x=" + this.getBlock() + ",y=" + this.getBlockY() + ",z=" + this.getBlockZ() + '}';
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();
        data.put("world", this.getWorld().getName());
        data.put("x", this.getBlockX());
        data.put("y", this.getBlockY());
        data.put("z", this.getBlockZ());
        return data;
    }

    public static BlockLocation deserialize(Map<String, Object> args) {
        World world = Bukkit.getWorld((String)args.get("world"));
        if (world == null) {
            throw new IllegalArgumentException("unknown world");
        } else {
            return new BlockLocation(world, NumberConversions.toInt(args.get("x")), NumberConversions.toInt(args.get("y")), NumberConversions.toInt(args.get("z")));
        }
    }
}
