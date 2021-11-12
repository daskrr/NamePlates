package com.daskrr.nameplates.version.wrapped.network.protocol;

import com.daskrr.nameplates.version.wrapped.entity.WrappedDataWatcher;
import com.daskrr.nameplates.version.wrapped.entity.WrappedEntity;
import com.daskrr.nameplates.version.wrapped.entity.WrappedEntityLiving;
import com.daskrr.nameplates.version.wrapper.network.PacketWrapper;

public interface WrappedPacketGameEntityMetadata extends PacketWrapper {
	// TODO add more methods here to extract data from the packet

    WrappedPacketGameEntityMetadata instantiate(int id, WrappedDataWatcher watcher, boolean sendAllItems);

}
