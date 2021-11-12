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
                squares.append("§7§l=§r");
            else
                squares.append(color).append("§l=§r");

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
}
