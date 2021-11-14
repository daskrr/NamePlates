package com.daskrr.nameplates.version.v1_8_R1.network.protocol;

import com.daskrr.nameplates.version.v1_8_R1.entity.Entity_v1_8_R1;
import com.daskrr.nameplates.version.wrapped.network.protocol.WrappedPacketGameEntityTeleport;
import com.daskrr.nameplates.version.wrapper.entity.EntityWrapper;
import net.minecraft.server.v1_8_R1.PacketPlayOutEntityTeleport;

public class PacketGameEntityTeleport_v1_8_R1 extends Packet_v1_8_R1 implements WrappedPacketGameEntityTeleport {

    private PacketPlayOutEntityTeleport packet;
    public PacketGameEntityTeleport_v1_8_R1() {  }

    public PacketPlayOutEntityTeleport getPacket() {
        return this.packet;
    }

    @Override
    public WrappedPacketGameEntityTeleport instantiate(EntityWrapper entity) {
        this.packet = new PacketPlayOutEntityTeleport(((Entity_v1_8_R1) entity).getEntity());
        return this;
    }
}
