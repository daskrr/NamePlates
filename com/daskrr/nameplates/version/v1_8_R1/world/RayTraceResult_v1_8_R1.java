package com.daskrr.nameplates.version.v1_8_R1.world;

import com.daskrr.nameplates.version.wrapped.world.WrappedRayTraceResult;
import net.minecraft.server.v1_8_R1.BlockPosition;
import net.minecraft.server.v1_8_R1.EnumMovingObjectType;
import net.minecraft.server.v1_8_R1.MovingObjectPosition;
import net.minecraft.server.v1_8_R1.Vec3D;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.libs.jline.internal.Nullable;
import org.bukkit.craftbukkit.v1_8_R1.block.CraftBlock;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import java.util.Objects;

public class RayTraceResult_v1_8_R1 implements WrappedRayTraceResult {

    public RayTraceResult_v1_8_R1() {  }

    protected static RayTraceResult_v1_8_R1 fromNMS(World world, MovingObjectPosition nmsHitResult) {
        if (nmsHitResult == null || nmsHitResult.type == EnumMovingObjectType.MISS) return null;

        Vec3D nmsHitPos = nmsHitResult.pos;
        Vector hitPosition = new Vector(nmsHitPos.a, nmsHitPos.b, nmsHitPos.c);
        BlockFace hitBlockFace = null;

        if (nmsHitResult.type == EnumMovingObjectType.ENTITY) {
            Entity hitEntity = nmsHitResult.entity.getBukkitEntity();
            return new RayTraceResult_v1_8_R1(hitPosition, hitEntity, null);
        }

        Block hitBlock = null;
        BlockPosition nmsBlockPos = null;
        if (nmsHitResult.type == EnumMovingObjectType.BLOCK) {
            hitBlockFace = CraftBlock.notchToBlockFace(nmsHitResult.direction);
            nmsBlockPos = nmsHitResult.a();
        }
        if (nmsBlockPos != null && world != null) {
            hitBlock = world.getBlockAt(nmsBlockPos.getX(), nmsBlockPos.getY(), nmsBlockPos.getZ());
        }

        return new RayTraceResult_v1_8_R1(hitPosition, hitBlock, hitBlockFace);
    }

    // BACK-PORTED FROM Bukkit
    // https://hub.spigotmc.org/stash/projects/SPIGOT/repos/bukkit/browse/src/main/java/org/bukkit/util/RayTraceResult_v1_8_R1.java
    private Vector hitPosition;

    private Block hitBlock;
    private BlockFace hitBlockFace;
    private Entity hitEntity;

    private RayTraceResult_v1_8_R1(Vector hitPosition, @Nullable Block hitBlock, @Nullable BlockFace hitBlockFace, @Nullable Entity hitEntity) {
//        Validate.notNull(hitPosition, "Hit position is null!");
        this.hitPosition = hitPosition.clone();
        this.hitBlock = hitBlock;
        this.hitBlockFace = hitBlockFace;
        this.hitEntity = hitEntity;
    }

    /**
     * Creates a RayTraceResult_v1_8_R1.
     *
     * @param hitPosition the hit position
     */
    public RayTraceResult_v1_8_R1(Vector hitPosition) {
        this(hitPosition, null, null, null);
    }

    /**
     * Creates a RayTraceResult_v1_8_R1.
     *
     * @param hitPosition the hit position
     * @param hitBlockFace the hit block face
     */
    public RayTraceResult_v1_8_R1(Vector hitPosition, @Nullable BlockFace hitBlockFace) {
        this(hitPosition, null, hitBlockFace, null);
    }

    /**
     * Creates a RayTraceResult_v1_8_R1.
     *
     * @param hitPosition the hit position
     * @param hitBlock the hit block
     * @param hitBlockFace the hit block face
     */
    public RayTraceResult_v1_8_R1(Vector hitPosition, @Nullable Block hitBlock, @Nullable BlockFace hitBlockFace) {
        this(hitPosition, hitBlock, hitBlockFace, null);
    }

    /**
     * Creates a RayTraceResult_v1_8_R1.
     *
     * @param hitPosition the hit position
     * @param hitEntity the hit entity
     */
    public RayTraceResult_v1_8_R1(Vector hitPosition, @Nullable Entity hitEntity) {
        this(hitPosition, null, null, hitEntity);
    }

    /**
     * Creates a RayTraceResult_v1_8_R1.
     *
     * @param hitPosition the hit position
     * @param hitEntity the hit entity
     * @param hitBlockFace the hit block face
     */
    public RayTraceResult_v1_8_R1(Vector hitPosition, @Nullable Entity hitEntity, @Nullable BlockFace hitBlockFace) {
        this(hitPosition, null, hitBlockFace, hitEntity);
    }

    /**
     * Gets the exact position of the hit.
     *
     * @return a copy of the exact hit position
     */
    public Vector getHitPosition() {
        return hitPosition.clone();
    }

    /**
     * Gets the hit block.
     *
     * @return the hit block, or <code>null</code> if not available
     */
    @Nullable
    public Block getHitBlock() {
        return hitBlock;
    }

    /**
     * Gets the hit block face.
     *
     * @return the hit block face, or <code>null</code> if not available
     */
    @Nullable
    public BlockFace getHitBlockFace() {
        return hitBlockFace;
    }

    /**
     * Gets the hit entity.
     *
     * @return the hit entity, or <code>null</code> if not available
     */
    @Nullable
    public Entity getHitEntity() {
        return hitEntity;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + hitPosition.hashCode();
        result = prime * result + ((hitBlock == null) ? 0 : hitBlock.hashCode());
        result = prime * result + ((hitBlockFace == null) ? 0 : hitBlockFace.hashCode());
        result = prime * result + ((hitEntity == null) ? 0 : hitEntity.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof RayTraceResult_v1_8_R1 other)) return false;
        if (!hitPosition.equals(other.hitPosition)) return false;
        if (!Objects.equals(hitBlock, other.hitBlock)) return false;
        if (!Objects.equals(hitBlockFace, other.hitBlockFace)) return false;
        return Objects.equals(hitEntity, other.hitEntity);
    }

    @Override
    public String toString() {
        return "RayTraceResult_v1_8_R1 [hitPosition=" +
                hitPosition +
                ", hitBlock=" +
                hitBlock +
                ", hitBlockFace=" +
                hitBlockFace +
                ", hitEntity=" +
                hitEntity +
                "]";
    }
}
