package com.daskrr.nameplates.version.wrapped.network;

import com.daskrr.nameplates.version.wrapper.network.PacketWrapper;
import org.bukkit.entity.Player;

public interface WrappedNetworkManager {

    void sendPacket(PacketWrapper packet);
    WrappedNetworkManager instantiate(Player player);
}
