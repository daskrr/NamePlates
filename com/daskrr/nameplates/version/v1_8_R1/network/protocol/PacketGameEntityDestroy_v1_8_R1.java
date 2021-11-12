package com.daskrr.nameplates.version.v1_8_R1.network.protocol;

import com.daskrr.nameplates.version.wrapped.network.protocol.WrappedPacketGameEntityDestroy;
import net.minecraft.server.v1_8_R1.Packet;
import net.minecraft.server.v1_8_R1.PacketPlayOutEntityDestroy;

public class PacketGameEntityDestroy_v1_8_R1 extends Packet_v1_8_R1 implements WrappedPacketGameEntityDestroy {

    PacketPlayOutEntityDestroy packet;

    @Override
    public Packet getPacket() {
        return this.packet;
    }

    @Override
    public WrappedPacketGameEntityDestroy instantiate(int... entityIds) {
        this.packet = new PacketPlayOutEntityDestroy(entityIds);
        return this;
    }
}
