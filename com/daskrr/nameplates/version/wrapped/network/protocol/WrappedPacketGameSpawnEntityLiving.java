package com.daskrr.nameplates.version.wrapped.network.protocol;

import com.daskrr.nameplates.version.wrapped.entity.WrappedEntityLiving;

public interface WrappedPacketGameSpawnEntityLiving {
	// TODO add more methods here to extract data from the packet

    WrappedPacketGameSpawnEntityLiving instantiate(WrappedEntityLiving entity);

}
