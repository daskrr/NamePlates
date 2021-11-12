package com.daskrr.nameplates.version.wrapped.network.protocol;

import com.daskrr.nameplates.version.wrapper.entity.LivingEntityWrapper;
import com.daskrr.nameplates.version.wrapper.network.PacketWrapper;

public interface WrappedPacketGameSpawnEntityLiving extends PacketWrapper {
	// TODO add more methods here to extract data from the packet

    WrappedPacketGameSpawnEntityLiving instantiate(LivingEntityWrapper entity);

}
