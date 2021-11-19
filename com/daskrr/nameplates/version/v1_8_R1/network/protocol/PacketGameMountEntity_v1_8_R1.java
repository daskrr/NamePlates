package com.daskrr.nameplates.version.v1_8_R1.network.protocol;

import com.daskrr.nameplates.version.v1_8_R1.entity.Entity_v1_8_R1;
import com.daskrr.nameplates.version.wrapped.network.protocol.WrappedPacketGameMountEntity;
import com.daskrr.nameplates.version.wrapped.network.protocol.WrappedPacketGameSpawnEntity;
import com.daskrr.nameplates.version.wrapper.entity.EntityWrapper;
import net.minecraft.server.v1_8_R1.PacketPlayOutAttachEntity;
import net.minecraft.server.v1_8_R1.PacketPlayOutSpawnEntity;

public class PacketGameMountEntity_v1_8_R1 extends Packet_v1_8_R1 implements WrappedPacketGameMountEntity {

    private PacketPlayOutAttachEntity packet;
    public PacketGameMountEntity_v1_8_R1() {  }

    public PacketPlayOutAttachEntity getPacket() {
        return this.packet;
    }


    @Override
    public WrappedPacketGameMountEntity instantiate(int iHaveNoIdeaWhatThisIs_ItWorksWith0, EntityWrapper passenger, EntityWrapper mount) {
        this.packet = new PacketPlayOutAttachEntity(iHaveNoIdeaWhatThisIs_ItWorksWith0, ((Entity_v1_8_R1) passenger).getEntity(), ((Entity_v1_8_R1) mount).getEntity());
        return this;
    }
}
