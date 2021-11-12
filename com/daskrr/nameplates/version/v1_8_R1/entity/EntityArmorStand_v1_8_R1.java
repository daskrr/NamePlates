package com.daskrr.nameplates.version.v1_8_R1.entity;

import com.daskrr.nameplates.version.wrapped.entity.WrappedEntityArmorStand;
import net.minecraft.server.v1_8_R1.EntityArmorStand;
import net.minecraft.server.v1_8_R1.NBTTagCompound;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftArmorStand;
import org.bukkit.entity.ArmorStand;

public class EntityArmorStand_v1_8_R1 extends EntityLiving_v1_8_R1 implements WrappedEntityArmorStand {

    private EntityArmorStand armorStand;

    @Override
    public void setCustomName(String customName) {
        this.armorStand.setCustomName(customName);
    }

    @Override
    public void setCustomNameVisible(boolean customNameVisible) {
        this.armorStand.setCustomNameVisible(customNameVisible);
    }

    @Override
    public void setNoGravity(boolean noGravity) {
        this.armorStand.setGravity(noGravity);
    }

    @Override
    public void setInvulnerable(boolean invulnerable) {
//      we're stuck with this shit for 1.8
        NBTTagCompound nbt = new NBTTagCompound();
        this.armorStand.c(nbt);
        nbt.setBoolean("Invulnerable", invulnerable);
        this.armorStand.f(invulnerable);
    }

    @Override
    public void setInvisible(boolean invisible) {
        this.armorStand.setInvisible(invisible);
    }

    @Override
    public void setMarker(boolean marker) {
//      Unfortunately this doesn't exist in v1_8_R1, it was added in v1_8_R2 (1.8.1) (as NBTTag)
//      this.armorStand.setMarker(marker);
//      So we're stuck with making our own bounding box
        // TODO
    }

    @Override
    public ArmorStand getArmorStand() {
        return (ArmorStand) this.armorStand.getBukkitEntity();
    }

    public EntityArmorStand getEntityArmorStand() {
        return this.armorStand;
    }

    @Override
    public WrappedEntityArmorStand instantiate(World world, Location location) {
        this.armorStand = new EntityArmorStand(((CraftWorld) world).getHandle(), location.getX(), location.getY(), location.getZ());

        return this;
    }

    @Override
    public WrappedEntityArmorStand instantiate(ArmorStand armorStand) {
        this.armorStand = ((CraftArmorStand) armorStand).getHandle();

        return this;
    }
}
