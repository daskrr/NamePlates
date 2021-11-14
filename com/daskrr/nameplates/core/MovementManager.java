package com.daskrr.nameplates.core;

import com.daskrr.nameplates.util.EntityUtils;
import com.daskrr.nameplates.version.VersionProvider;
import com.daskrr.nameplates.version.wrapped.WrappedItem;
import com.daskrr.nameplates.version.wrapped.entity.WrappedEntityArmorStand;
import com.daskrr.nameplates.version.wrapped.network.WrappedNetworkManager;
import com.daskrr.nameplates.version.wrapped.network.protocol.WrappedPacketGameEntityTeleport;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class MovementManager {

    private final NamePlateUpdater updater;
    protected MovementManager(NamePlateUpdater updater) {
        this.updater = updater;
    }

    // teleports all plates to the position of the hosting entity
    public void tick() {
        // get all plates in render (since we only handle those - no need to send teleport packets if the entity is not visible)
        this.updater.renderManager.renderedPlates.forEach((entityUUID, renderedNamePlate) -> {
            // check if entity is loaded (should be)
            Entity entity = EntityUtils.getEntityInLoadedChunks(entityUUID);
            if (entity == null)
                return;

            ((ContextNamePlate) renderedNamePlate.getPlate()).getViewersSet().forEach(playerUUID -> {
                this.move(Bukkit.getPlayer(playerUUID), renderedNamePlate, entity);
            });
        });

    }

    public void move(Player player, RenderedNamePlate renderedNamePlate, Entity entity) {
        Location location = entity.getLocation().clone();

        for (int index = 0; index < renderedNamePlate.getArmorStands().length; index++) {
            ArmorStand armorStand = renderedNamePlate.getArmorStands()[index];
            WrappedEntityArmorStand wrappedArmorStand = VersionProvider.getItem(WrappedItem.ENTITY_ARMOR_STAND)
                                                                       .instantiate(armorStand);
            location.setY(this.updater.renderManager.calculateY(index, renderedNamePlate.getPlate(), entity));
            wrappedArmorStand.setPosition(location);

            WrappedPacketGameEntityTeleport packetTeleport = VersionProvider.getItem(WrappedItem.PACKET_GAME_ENTITY_TELEPORT)
                                                                            .instantiate(wrappedArmorStand);

            WrappedNetworkManager networkManager = VersionProvider.getItem(WrappedItem.NETWORK_MANAGER).instantiate(player);
            networkManager.sendPacket(packetTeleport);
        }
    }
}
