package com.daskrr.nameplates.version.v1_8_R1.network.protocol;

import com.daskrr.nameplates.version.v1_8_R1.entity.DataWatcher_v1_8_R1;
import com.daskrr.nameplates.version.wrapped.entity.WrappedDataWatcher;
import com.daskrr.nameplates.version.wrapped.network.protocol.WrappedPacketGameEntityMetadata;
import net.minecraft.server.v1_8_R1.PacketPlayOutEntityMetadata;

public class PacketGameEntityMetadata_v1_8_R1 extends Packet_v1_8_R1 implements WrappedPacketGameEntityMetadata {

    private PacketPlayOutEntityMetadata packet;
    public PacketGameEntityMetadata_v1_8_R1() {  }

    public PacketPlayOutEntityMetadata getPacket() {
        return this.packet;
    }

    @Override
    public WrappedPacketGameEntityMetadata instantiate(int id, WrappedDataWatcher watcher, boolean sendAllItems) {
        this.packet = new PacketPlayOutEntityMetadata(id, ((DataWatcher_v1_8_R1) watcher).getDataWatcher(), sendAllItems);
        return this;
    }
}
