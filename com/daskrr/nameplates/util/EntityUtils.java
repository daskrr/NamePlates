package com.daskrr.nameplates.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import java.util.UUID;

public class EntityUtils {
    public static String healthToSquares(ChatColor color, double current, double max) {
        StringBuilder squares = new StringBuilder();
        double currentSquares = Math.ceil((10 * current) / max);

        for (int i = 0; i < 10; i++)
            if (i < currentSquares)
                squares.append(color).append("§l§m=§r");
            else
                squares.append("§7§l§m=§r");

        return squares.toString();
    }

    public static Entity getEntityInLoadedChunks(UUID uuid) {
        for (World world : Bukkit.getWorlds())
            for (Chunk chunk : world.getLoadedChunks())
                for (Entity entity : chunk.getEntities())
                    if (entity.getUniqueId().equals(uuid))
                        return entity;

        return null;
    }

    public static Entity getEntity(UUID uuid) {
        for (World world : Bukkit.getWorlds())
            for (Entity entity : world.getEntities())
                if (entity.getUniqueId().equals(uuid))
                    return entity;

        return null;
    }

    // + to the right
    // - to the left
    public static float shiftYaw(float yaw, float amount) {
        if (yaw + amount > 180f)
            return -179.9f + ((yaw + amount) - 180f);
        else if (yaw + amount < -179.9f)
            return 180f + ((yaw + amount) + 180f);
        else
            return yaw + amount;
    }
}
