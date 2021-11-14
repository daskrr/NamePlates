package com.daskrr.nameplates.version.wrapped.network.protocol;

import com.daskrr.nameplates.version.wrapper.entity.EntityWrapper;
import com.daskrr.nameplates.version.wrapper.network.PacketWrapper;

public interface WrappedPacketGameEntityTeleport extends PacketWrapper {

    WrappedPacketGameEntityTeleport instantiate(EntityWrapper entity);
}
