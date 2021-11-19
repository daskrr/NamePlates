package com.daskrr.nameplates.core;

import com.daskrr.nameplates.api.nameplate.NamePlate;
import com.daskrr.nameplates.util.EntityUtils;
import com.daskrr.nameplates.version.Version;
import com.daskrr.nameplates.version.VersionProvider;
import com.daskrr.nameplates.version.wrapped.WrappedItem;
import com.daskrr.nameplates.version.wrapped.entity.WrappedEntityArmorStand;
import com.daskrr.nameplates.version.wrapped.entity.WrappedEntityItem;
import com.daskrr.nameplates.version.wrapped.network.WrappedNetworkManager;
import com.daskrr.nameplates.version.wrapped.network.protocol.WrappedPacketGameEntityTeleport;
import com.daskrr.nameplates.version.wrapped.network.protocol.WrappedPacketGameSpawnEntity;
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
        // loop through all plates
        this.updater.namePlateHandler.namePlates.forEach((id, namePlate) -> {
            ((ContextNamePlate) namePlate).getRenders().forEach((entityUUID, render) -> {
                // check if entity is loaded
                Entity entity = EntityUtils.getEntityInLoadedChunks(entityUUID);
                if (entity == null)
                    return;

                render.getViewers().forEach(playerUUID -> {
                    this.move(Bukkit.getPlayer(playerUUID), namePlate, render, entity);
                });
            });
        });
    }

    public void move(Player player, NamePlate plate, PlateRender render, Entity entity) {
        Location location = entity.getLocation().clone();

        for (int index = 0; index < render.getArmorStands().size(); index++) {
            ArmorStand armorStand = render.getArmorStands().get(index);
            WrappedEntityArmorStand wrappedArmorStand = VersionProvider.getItem(WrappedItem.ENTITY_ARMOR_STAND)
                                                                       .instantiate(armorStand);
            location.setY(this.updater.renderManager.calculateY(index, plate, entity));

            // check for version v1_8_R1, as it's the only one that doesn't have "marker"
            if (VersionProvider.getInstance().getVersion() == Version.v1_8_R1)
                // it is, remove the height of the armor stand from the Y (since it's nameplate is displayed above the armor stand's bounding box
                location.setY((location.getY() - wrappedArmorStand.getBBHeight()) + RenderManager.OFFSET);

            location.setX(entity.getLocation().getX());
            location.setZ(entity.getLocation().getZ());

            // check if line has item
            if (plate.getBuilder().getLines()[index].hasItem())
                // check if the version doesn't allow no-gravity on items
                if (VersionProvider.getInstance().getVersion().ordinal() <= Version.v1_8_R3.ordinal())
                    // set armor stand position to accommodate item position
                    location.setY(this.updater.renderManager.calculateYItem(index, plate, entity) - (wrappedArmorStand.getBBHeight() * .65D));

            location = this.updater.renderManager.calculateOffsetPassengerOverlapScenario(index, plate, entity, location);

            if (location == null)
                return;

            wrappedArmorStand.setPosition(location);

            WrappedPacketGameEntityTeleport packetTeleport = VersionProvider.getItem(WrappedItem.PACKET_GAME_ENTITY_TELEPORT)
                                                                            .instantiate(wrappedArmorStand);

            WrappedNetworkManager networkManager = VersionProvider.getItem(WrappedItem.NETWORK_MANAGER).instantiate(player);
            networkManager.sendPacket(packetTeleport);
        }
    }
}
