package com.daskrr.nameplates.version.wrapped.network.protocol;

import com.daskrr.nameplates.version.wrapper.entity.EntityWrapper;
import com.daskrr.nameplates.version.wrapper.network.PacketWrapper;

public interface WrappedPacketGameMountEntity extends PacketWrapper {
	// TODO add more methods here to extract data from the packet

    WrappedPacketGameMountEntity instantiate(int noIdeaWhatThisIs_WorksWith0, EntityWrapper passenger, EntityWrapper mount);

}
