package com.daskrr.nameplates.version.wrapped.entity;

import com.daskrr.nameplates.version.wrapper.entity.LivingEntityWrapper;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;

public interface WrappedEntityArmorStand extends LivingEntityWrapper {

    void setCustomName(String customName);
    void setCustomNameVisible(boolean customNameVisible);
    void setNoGravity(boolean noGravity);
    void setInvulnerable(boolean invulnerable);
    void setInvisible(boolean invisible);
    void setMarker(boolean marker);
    void setSmall(boolean small);

    ArmorStand getArmorStand();

    WrappedEntityArmorStand instantiate(Location location);
    WrappedEntityArmorStand instantiate(ArmorStand armorStand);
}
