package com.daskrr.nameplates.version.wrapped.world;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.libs.jline.internal.Nullable;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

public interface WrappedRayTraceResult {
    /**
     * Gets the exact position of the hit.
     *
     * @return a copy of the exact hit position
     */
    public Vector getHitPosition();

    /**
     * Gets the hit block.
     *
     * @return the hit block, or <code>null</code> if not available
     */
    @Nullable
    public Block getHitBlock();

    /**
     * Gets the hit block face.
     *
     * @return the hit block face, or <code>null</code> if not available
     */
    @Nullable
    public BlockFace getHitBlockFace();

    /**
     * Gets the hit entity.
     *
     * @return the hit entity, or <code>null</code> if not available
     */
    @Nullable
    public Entity getHitEntity();

    @Override
    public int hashCode();

    @Override
    public boolean equals(Object obj);

    @Override
    public String toString();
}
