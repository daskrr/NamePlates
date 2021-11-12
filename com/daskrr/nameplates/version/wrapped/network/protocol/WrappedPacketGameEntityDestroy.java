package com.daskrr.nameplates.version.wrapped.network.protocol;

import com.daskrr.nameplates.version.wrapper.network.PacketWrapper;

public interface WrappedPacketGameEntityDestroy extends PacketWrapper {

    WrappedPacketGameEntityDestroy instantiate(int... entityIds);
}
