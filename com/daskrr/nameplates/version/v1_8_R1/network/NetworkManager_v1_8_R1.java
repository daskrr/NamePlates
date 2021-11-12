package com.daskrr.nameplates.version.v1_8_R1.network;

import com.daskrr.nameplates.version.v1_8_R1.network.protocol.Packet_v1_8_R1;
import com.daskrr.nameplates.version.wrapped.network.WrappedNetworkManager;
import com.daskrr.nameplates.version.wrapper.network.PacketWrapper;
import net.minecraft.server.v1_8_R1.NetworkManager;
import net.minecraft.server.v1_8_R1.PlayerConnection;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class NetworkManager_v1_8_R1 implements WrappedNetworkManager {

    private PlayerConnection playerConnection;
    private NetworkManager networkManager;

    @Override
    public void sendPacket(PacketWrapper packet) {
        this.playerConnection.sendPacket(((Packet_v1_8_R1) packet).getPacket());
    }

    @Override
    public WrappedNetworkManager instantiate(Player player) {
        this.playerConnection = ((CraftPlayer) player).getHandle().playerConnection;
        this.networkManager = this.playerConnection.networkManager;
        return this;
    }
}
