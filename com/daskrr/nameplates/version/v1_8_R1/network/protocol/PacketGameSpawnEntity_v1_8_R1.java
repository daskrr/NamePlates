package com.daskrr.nameplates.version.v1_8_R1.network.protocol;

import com.daskrr.nameplates.version.v1_8_R1.entity.Entity_v1_8_R1;
import com.daskrr.nameplates.version.wrapped.network.protocol.WrappedPacketGameSpawnEntity;
import com.daskrr.nameplates.version.wrapper.entity.EntityWrapper;
import net.minecraft.server.v1_8_R1.PacketPlayOutSpawnEntity;

public class PacketGameSpawnEntity_v1_8_R1 extends Packet_v1_8_R1 implements WrappedPacketGameSpawnEntity {

    private PacketPlayOutSpawnEntity packet;
    public PacketGameSpawnEntity_v1_8_R1() {  }

    public PacketPlayOutSpawnEntity getPacket() {
        return this.packet;
    }


    @Override
    public WrappedPacketGameSpawnEntity instantiate(EntityWrapper entity, int id) {
        this.packet = new PacketPlayOutSpawnEntity(((Entity_v1_8_R1) entity).getEntity(), id);
        return this;
    }
}
