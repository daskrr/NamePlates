package com.daskrr.nameplates.version.v1_8_R1.network.protocol;

import com.daskrr.nameplates.version.v1_8_R1.entity.EntityLiving_v1_8_R1;
import com.daskrr.nameplates.version.wrapped.entity.WrappedEntityLiving;
import com.daskrr.nameplates.version.wrapped.network.protocol.WrappedPacketGameSpawnEntityLiving;

import net.minecraft.server.v1_8_R1.PacketPlayOutSpawnEntityLiving;

public class PacketGameSpawnEntityLiving_v1_8_R1 implements WrappedPacketGameSpawnEntityLiving {

    private PacketPlayOutSpawnEntityLiving packet;
    public PacketGameSpawnEntityLiving_v1_8_R1() {  }

    public PacketPlayOutSpawnEntityLiving getPacket() {
        return this.packet;
    }

    @Override
    public WrappedPacketGameSpawnEntityLiving instantiate(WrappedEntityLiving entity) {
        this.packet = new PacketPlayOutSpawnEntityLiving(((EntityLiving_v1_8_R1) entity).getEntityLiving());
        return this;
    }

}
