package com.daskrr.nameplates.version.v1_8_R1.entity.custom;

import com.daskrr.nameplates.core.NamePlatesPlugin;
import net.minecraft.server.v1_8_R1.EntityItem;
import net.minecraft.server.v1_8_R1.ItemStack;
import net.minecraft.server.v1_8_R1.World;

public class MonitoredEntityItem_v1_8_R1 extends EntityItem {

    public MonitoredEntityItem_v1_8_R1(World world, double d0, double d1, double d2) {
        super(world, d0, d1, d2);
        System.out.println("i'm spawned");
    }

    public MonitoredEntityItem_v1_8_R1(World world, double d0, double d1, double d2, ItemStack itemstack) {
        super(world, d0, d1, d2, itemstack);
    }

    public MonitoredEntityItem_v1_8_R1(World world) {
        super(world);
    }

    @Override
    public void die() {
        super.die();
//        if (NamePlatesPlugin.instance() != null)
//            NamePlatesPlugin.instance().plateHandler.updater.entityChecker.itemDeath(this.getBukkitEntity());
    }
}
