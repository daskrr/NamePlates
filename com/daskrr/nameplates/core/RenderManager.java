package com.daskrr.nameplates.core;

import com.daskrr.nameplates.api.NamePlateAPIOptions;
import com.daskrr.nameplates.api.nameplate.NamePlate;
import com.daskrr.nameplates.api.nameplate.NamePlateTextBuilder;
import com.daskrr.nameplates.core.event.NamePlateAttachEvent;
import com.daskrr.nameplates.core.event.NamePlateRenderToggleEvent;
import com.daskrr.nameplates.util.EntityUtils;
import com.daskrr.nameplates.version.VersionProvider;
import com.daskrr.nameplates.version.wrapped.WrappedItem;
import com.daskrr.nameplates.version.wrapped.entity.WrappedEntityArmorStand;
import com.daskrr.nameplates.version.wrapped.network.WrappedNetworkManager;
import com.daskrr.nameplates.version.wrapped.network.protocol.WrappedPacketGameEntityDestroy;
import com.daskrr.nameplates.version.wrapped.network.protocol.WrappedPacketGameEntityMetadata;
import com.daskrr.nameplates.version.wrapped.network.protocol.WrappedPacketGameSpawnEntityLiving;
import com.daskrr.nameplates.version.wrapped.world.WrappedRayTraceResult;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class RenderManager {

    private static final double OFFSET = .275D;
    private static final double PLATE_HEIGHT = .25D;

    public final BiMap<UUID, RenderedNamePlate> renderedPlates = HashBiMap.create();
    //                   Player  Entity
    public final List<Pair<UUID, UUID>> cancelledAttachEntities = Lists.newArrayList();

    private final NamePlateUpdater updater;
    protected RenderManager(NamePlateUpdater updater) {
        this.updater = updater;
    }

    // checks for entities that can be seen and renders their plates
    // checks for entities that cannot be seen and un-renders their plates, telling the movement manager to not move them
    // this also updates the viewers of the NamePlate
    @SuppressWarnings("unchecked")
    public void tick() {
        // get view distance
        int viewDistance = updater.namePlateHandler.getOptions().getOption(NamePlateAPIOptions.Key.VIEW_DISTANCE).getValue();
        // loop through all players
        Bukkit.getOnlinePlayers().forEach((player) -> {
            // check for nearby entities
            player.getNearbyEntities(viewDistance, 255, viewDistance).forEach((entity) -> {
                // check for visibility if renderBehindWalls is disabled
                if (updater.namePlateHandler.getOptions().getOption(NamePlateAPIOptions.Key.RENDER_BEHIND_WALLS).getValue()) {
                    // ray trace the entity
                    Location playerLocation = player.getLocation();
                    Location entityLocation = entity.getLocation().add(0, 2, 0); // get the block above the entity, checking if the head is visible (I think)
                    Vector vectorToEntity = playerLocation.toVector().subtract(entityLocation.toVector());

                    WrappedRayTraceResult result = VersionProvider.getItem(WrappedItem.RAY_TRACE).rayTrace(playerLocation, vectorToEntity, 160);
                    if (result.getHitBlock() != null) {
                        // didn't hit the entity
                        // check if player is disabled from viewing any entities
                        if (updater.namePlateHandler.disabledPlayers.contains(player.getUniqueId()))
                            return;

                        // try to get the plate
                        RenderedNamePlate renderedNamePlate = this.renderedPlates.get(entity.getUniqueId());

                        // plate exists
                        if (renderedNamePlate != null) {
                            // check for its own view distance
                            if (renderedNamePlate.getPlate().getViewDistance() > player.getLocation().distance(entityLocation))
                                return;

                            // check if plate is rendered permanently
                            if (renderedNamePlate.getPlate().getRenderPermanently())
                                return;

                            // remove it from render
                            this.remove(player, renderedNamePlate, entity);
                            // remove the player from the viewers list
                            renderedNamePlate.getPlate().viewers.remove(player.getUniqueId());
                        }
                    }
                    else {
                        // get nameplate for entity (if any)
                        int plateId = -1;
                        for (Map.Entry<Integer, EntityGroup<?>> entry : this.updater.namePlateHandler.entityGroups.entrySet()) {
                            EntityGroup<?> group = entry.getValue();
                            if (group.getType() == EntityGroup.Type.ENTITY_TYPE) {
                                if (!Lists.newArrayList(((EntityGroup<EntityType>) group).get()).contains(entity.getType()))
                                    return;
                            }
                            else
                                if (!Lists.newArrayList(((EntityGroup<UUID>) group).get()).contains(entity.getUniqueId()))
                                    return;
                            plateId = entry.getKey();
                        }

                        // check for results
                        if (plateId > -1) {
                            NamePlate namePlate = this.updater.namePlateHandler.namePlates.get(plateId);

                            // check if player is disabled from view
                            if (updater.namePlateHandler.disabledPlayers.contains(player.getUniqueId()))
                                return;

                            // check if player is disallowed from view in plate context
                            if (namePlate.getDisabledViewPlayers().contains(player.getUniqueId()))
                                return;

                            // render plates
                            this.render(player, namePlate, entity);
                            // update viewing players
                            namePlate.viewers.add(player.getUniqueId());
                        }
                    }
                }
                // entities can be rendered behind walls
                else {
                    // get nameplate for entity (if any)
                    int plateId = -1;
                    for (Map.Entry<Integer, EntityGroup<?>> entry : this.updater.namePlateHandler.entityGroups.entrySet()) {
                        EntityGroup<?> group = entry.getValue();
                        if (group.getType() == EntityGroup.Type.ENTITY_TYPE) {
                            if (!Lists.newArrayList(((EntityGroup<EntityType>) group).get()).contains(entity.getType()))
                                return;
                        }
                        else
                        if (!Lists.newArrayList(((EntityGroup<UUID>) group).get()).contains(entity.getUniqueId()))
                            return;
                        plateId = entry.getKey();
                    }

                    // check for results
                    if (plateId > -1) {
                        NamePlate namePlate = this.updater.namePlateHandler.namePlates.get(plateId);

                        // check if player is disabled from view
                        if (updater.namePlateHandler.disabledPlayers.contains(player.getUniqueId()))
                            return;

                        // check if player is disallowed from view in plate context
                        if (namePlate.getDisabledViewPlayers().contains(player.getUniqueId()))
                            return;

                        // render plates
                        this.render(player, namePlate, entity);
                        // update viewing players
                        namePlate.viewers.add(player.getUniqueId());
                    }
                }
            });

            // remove entities outside the view distance
            // iterate through the rendered plates
            this.renderedPlates.forEach((entityUUID, renderedNamePlate) -> {
                // get entity
                Entity entity = EntityUtils.getEntityInLoadedChunks(entityUUID);
                if (entity == null)
                    return;

                // check if plate is rendered permanently
                if (renderedNamePlate.getPlate().getRenderPermanently())
                    return;

                // get distance between player and entity
                double distance = player.getLocation().distance(entity.getLocation());

                // check if entity is in default view
                // if not, remove it
                if (viewDistance < distance) {
                    // remove from render
                    this.remove(player, renderedNamePlate, entity);
                    // remove the player from the viewers list
                    renderedNamePlate.getPlate().viewers.remove(player.getUniqueId());
                }

                // check if the entity is in plate's view
                // if not, remove it
                if (renderedNamePlate.getPlate().getViewDistance() < distance) {
                    // remove from render
                    this.remove(player, renderedNamePlate, entity);
                    // remove the player from the viewers list
                    renderedNamePlate.getPlate().viewers.remove(player.getUniqueId());
                }
            });
        });
    }

    // gets rendered plate by id
    public Pair<UUID, RenderedNamePlate> getRenderedNamePlate(int id) {
        for (Map.Entry<UUID, RenderedNamePlate> entry : this.renderedPlates.entrySet()) {
            if (entry.getValue().getPlate().getId() == id)
                return Pair.of(entry.getKey(), entry.getValue());
        }

        return null;
    }

    // creates the nameplates and places them into the world
    // tells the movement manager to move them (by adding them to this.renderedPlates)
    // this only applies for entities
    private void render(Player player, NamePlate plate, Entity attachedTo) {
        UUID entityUUID = attachedTo.getUniqueId();

        // check cancelled
        for (Pair<UUID, UUID> pair : this.cancelledAttachEntities)
            if (pair.getLeft().equals(player.getUniqueId()))
                if (pair.getRight().equals(entityUUID))
                    return;

        RenderedNamePlate renderedNamePlate;
        if (!NamePlateFactory.getInstance().contains(entityUUID)) {
            final boolean[] cancelled = {false};
            // fire event for first attach
            this.updater.namePlateHandler.eventHandler.fireEvent(new NamePlateAttachEvent() {
                @Override
                public Entity getEntity() {
                    return attachedTo;
                }

                @Override
                public void setCancelled(boolean isCancelled) {
                    cancelled[0] = isCancelled;
                }

                @Override
                public boolean isCancelled() {
                    return cancelled[0];
                }

                @Override
                public NamePlate getNamePlate() {
                    return null;
                }
            });

            // cancel execution
            if (cancelled[0]) {
                this.cancelledAttachEntities.add(Pair.of(player.getUniqueId(), entityUUID));
                return;
            }

            renderedNamePlate = NamePlateFactory.getInstance().create(entityUUID, plate);
        }
        else {
            renderedNamePlate = NamePlateFactory.getInstance().get(entityUUID);

            // create the plates
            renderedNamePlate.putArmorStands(this.createArmorStands(plate, attachedTo));
        }

        // render the plates
        this.sendArmorStands(false, player, plate, renderedNamePlate.getArmorStands(), attachedTo);

        // set as being in render if it isn't
        if (!this.renderedPlates.containsKey(entityUUID))
            this.renderedPlates.put(entityUUID, renderedNamePlate);

        // fire event
        this.updater.namePlateHandler.eventHandler.fireEvent(new NamePlateRenderToggleEvent() {
            @Override
            public Player getPlayer() {
                return player;
            }

            @Override
            public World getWorld() {
                return null; // TODO render for static nameplates
            }

            @Override
            public NamePlate getNamePlate() {
                return renderedNamePlate.getPlate();
            }

            @Override
            public Entity getEntity() {
                return attachedTo;
            }
        });
    }

    private void remove(Player player, RenderedNamePlate renderedNamePlate, Entity attachedTo) {
        this.removeArmorStands(player, renderedNamePlate.getArmorStands());
        // remove if it's no longer being rendered to any player
        if (renderedNamePlate.getPlate().getViewers().length == 0)
            this.renderedPlates.remove(attachedTo.getUniqueId());
    }

    public NamePlate removePermanently(Entity attachedTo) {
        NamePlate namePlate = this.renderedPlates.get(attachedTo.getUniqueId()).getPlate();

        Bukkit.getOnlinePlayers().forEach((player) -> this.removeArmorStands(player, this.renderedPlates.get(attachedTo.getUniqueId()).getArmorStands()));
        this.renderedPlates.remove(attachedTo.getUniqueId());
        NamePlateFactory.getInstance().remove(attachedTo.getUniqueId());

        // remove from cancelledAttachEntities to prevent pollution
        this.cancelledAttachEntities.forEach(pair -> {
            if (pair.getRight().equals(attachedTo.getUniqueId()))
                this.cancelledAttachEntities.remove(pair);
        });

        return namePlate;
    }

    protected ArmorStand[] createArmorStands(NamePlate plate, Entity entity) {
        int lines = plate.getBuilder().getLines().length;
        ArmorStand[] armorStands = new ArmorStand[lines];

        for (int i = 0; i < lines; i++) {
            NamePlateTextBuilder.Line line = plate.getBuilder().getLines()[i];

            StringBuilder finalText = new StringBuilder();
            for (NamePlateTextBuilder.Component component: line.getComponents()) {
                // apply color
                finalText.append(component.getColor());

                // apply modifiers
                if (component.isBold())
                    finalText.append(ChatColor.BOLD);
                if (component.isUnderlined())
                    finalText.append(ChatColor.UNDERLINE);
                if (component.isStrikethrough())
                    finalText.append(ChatColor.STRIKETHROUGH);
                if (component.isItalic())
                    finalText.append(ChatColor.ITALIC);
                if (component.isObfuscated())
                    finalText.append(ChatColor.MAGIC);

                // execute function and ignore type and format
                if (component.hasFunction()) {
                    component.getFunction().apply(entity);
                }
                else {
                    // fill format vars
                    if (entity instanceof LivingEntity livingEntity) {
                        finalText.append(component.getFormat().replaceAll("\\{HP}", String.format("%.0f", livingEntity.getHealth())));
                        finalText.append(component.getFormat().replaceAll("\\{MAX}", String.format("%.0f", livingEntity.getMaxHealth())));
                        finalText.append(component.getFormat().replaceAll(
                                "\\{HP_SQUARES}",
                                EntityUtils.healthToSquares(component.getColor(), livingEntity.getHealth(), livingEntity.getMaxHealth())));
                    }

                    finalText.append(component.getFormat().replaceAll("\\{NAME}", VersionProvider.getItem(WrappedItem.ENTITY).instantiate(entity).getName()));
                }
            }

            // build armor stand
            WrappedEntityArmorStand armorStand = VersionProvider.getItem(WrappedItem.ENTITY_ARMOR_STAND).instantiate(entity.getWorld(), entity.getLocation());
            armorStand.setInvisible(true);
            armorStand.setInvulnerable(true);
            armorStand.setMarker(true);
            armorStand.setNoGravity(true);
            armorStand.setCustomName(finalText.toString());
            armorStand.setCustomNameVisible(true);

            armorStands[i] = armorStand.getArmorStand();
        }

        return armorStands;
    }

    public double calculateY(int lineIndex, NamePlate plate, Entity entity) {
        double y = calculateY(lineIndex, plate, entity.getLocation().getY());
        y += VersionProvider.getItem(WrappedItem.ENTITY).instantiate(entity).getHeight(); // add entity height

        // check if entity is player
        if (entity instanceof Player || entity.getType() == EntityType.PLAYER)
            y += PLATE_HEIGHT * 2; // add 2 plate height space to prevent overlap

        return y;
    }
    public double calculateY(int lineIndex, NamePlate plate, double y) {
        // lines go from down to up, contrary to popular usage, since we want to get closer to the entity
        // hence, the first line will be the lowest
        y -= OFFSET; // now the y for the plate is right above the entity's head (or block) (hit box-wise)

        y += PLATE_HEIGHT * lineIndex; // don't add +1 since first line is the first
        y += plate.getMarginBottom(); // add margin bottom of the text
        y += plate.getBuilder().getLines()[lineIndex].getMargin()[1]; // add margin bottom of this plate
        if (lineIndex > 0) // add the margin top of previous plate
            y += plate.getBuilder().getLines()[lineIndex - 1].getMargin()[0];

        return y;
    }

    protected void sendArmorStands (boolean metadataOnly, Player player, NamePlate plate, ArmorStand[] armorStands, Entity entity) {
        // add entity offset
        double offset = VersionProvider.getItem(WrappedItem.ENTITY).instantiate(entity).getHeight();
        sendArmorStands(metadataOnly, player, plate, armorStands, entity.getLocation(), offset);
    }
    protected void sendArmorStands (boolean metadataOnly, Player player, NamePlate plate, ArmorStand[] armorStands, Location location) {
        sendArmorStands(metadataOnly, player, plate, armorStands, location, 0);
    }

    protected void sendArmorStands (boolean metadataOnly, Player player, NamePlate plate, ArmorStand[] armorStands, Location location, double offset) {
        for (int lineIndex = 0; lineIndex < plate.getBuilder().getLines().length; lineIndex++) {
            // get the armor stand of this line
            WrappedEntityArmorStand armorStand = VersionProvider.getItem(WrappedItem.ENTITY_ARMOR_STAND).instantiate(armorStands[lineIndex]);

            // update location
            armorStand.getArmorStand().getLocation().setX(location.getX());
            armorStand.getArmorStand().getLocation().setY(this.calculateY(lineIndex, plate, location.getY()) + offset);
            armorStand.getArmorStand().getLocation().setZ(location.getZ());

            // send spawn packet and metadata packet
            WrappedPacketGameSpawnEntityLiving spawnPacket = VersionProvider.getItem(WrappedItem.PACKET_GAME_SPAWN_ENTITY_LIVING).instantiate(armorStand);
            WrappedPacketGameEntityMetadata metadataPacket = VersionProvider.getItem(WrappedItem.PACKET_GAME_ENTITY_METADATA).instantiate(
                                                                armorStand.getId(),
                                                                armorStand.getDataWatcher(),
                                                                false);

            WrappedNetworkManager networkManager = VersionProvider.getItem(WrappedItem.NETWORK_MANAGER).instantiate(player);
            if (!metadataOnly)
                networkManager.sendPacket(spawnPacket);
            networkManager.sendPacket(metadataPacket);
        }
    }

    private void removeArmorStands(Player player, ArmorStand[] armorStands) {
        // send entity destroy packet
        int[] ids = new int[armorStands.length];
        for (int i = 0; i < armorStands.length; i++) ids[i] = armorStands[i].getEntityId();

        WrappedPacketGameEntityDestroy destroyPacket = VersionProvider.getItem(WrappedItem.PACKET_GAME_DESTROY_ENTITY).instantiate(ids);

        WrappedNetworkManager networkManager = VersionProvider.getItem(WrappedItem.NETWORK_MANAGER).instantiate(player);
        networkManager.sendPacket(destroyPacket);
    }
}