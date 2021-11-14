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
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
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

    public final Map<UUID, RenderedNamePlate> renderedPlates = Maps.newHashMap();
    //                   Player  Entity
    public final List<Pair<UUID, UUID>> cancelledAttachEntities = Lists.newArrayList();

    private final NamePlateUpdater updater;
    protected RenderManager(NamePlateUpdater updater) {
        this.updater = updater;
    }

    // checks for entities that can be seen and renders their plates
    // checks for entities that cannot be seen and un-renders their plates, telling the movement manager to not move them
    // this also updates the viewers of the NamePlate, RenderedNamePlate
    public void tick() {
        // get view distance
        int viewDistance = updater.namePlateHandler.getOptions().getOption(NamePlateAPIOptions.Key.VIEW_DISTANCE).getValue();
        // loop through all players
        Bukkit.getOnlinePlayers().forEach((player) -> {
            // check for nearby entities
            List<Entity> nearbyEntities;
            (nearbyEntities = player.getNearbyEntities(viewDistance, 255, viewDistance)).forEach((entity) -> {
                // get nameplate
                NamePlate namePlate = this.updater.namePlateHandler.getNamePlateOf(entity.getUniqueId(), false);

                // things change
                // check if entity still has nameplate
                if (namePlate == null) {
                    // remove from rendered (since entities can be excluded after instantiation)
                    this.removePermanently(entity);
                    return; // continue forEach
                }

                // nameplate exists, get rendered plate
                RenderedNamePlate renderedNamePlate = this.renderedPlates.get(entity.getUniqueId());

                // also updating the namePlate of the RenderedNamePlate is required, since the plate can change too
                renderedNamePlate.setPlate(namePlate);

                // this is kind of redundant, as the namePlate would be null if this were true, since it's automatically
                // not returned if it's excluded
                // TODO check
                // check if entity is excluded from nameplate
                if (Lists.newArrayList(namePlate.getSharedGroup().getExcluded()).contains(entity.getUniqueId()))
                    return; // continue forEach

                // check if rendering behind walls is globally allowed
                if (!updater.namePlateHandler.getOptions().getOption(NamePlateAPIOptions.Key.RENDER_BEHIND_WALLS).getValue()) {
                    // rendering behind walls is not globally allowed
                    // check if nameplate allows rendering behind walls
                    if (!namePlate.getRenderBehindWalls()) {
                        // nameplate doesn't, so
                        // ray trace the entity
                        // gets removed automatically if result is true
                        if (this.isVisible(player, entity, viewDistance))
                            // not behind a wall
                            this.conditionedRender(renderedNamePlate, player, entity);
                    }
                    // nameplate settings says entity can render behind walls
                    else
                        this.conditionedRender(renderedNamePlate, player, entity);
                }
                else
                    // rendering behind walls is globally allowed
                    // check if nameplate allows it
                    if (!namePlate.getRenderBehindWalls())
                        // doesn't allow it, check if the entity is behind a wall
                        // gets removed automatically if result is true
                        if (this.isVisible(player, entity, viewDistance))
                            // not behind a wall
                            this.conditionedRender(renderedNamePlate, player, entity);
                    else
                        // nameplate doesn't care about walls either
                        this.conditionedRender(renderedNamePlate, player, entity);

            });

            // TODO this can be done by checking the entity against the entity list of surrounding entities instead of distance
            // remove entities outside the view distance
            // iterate through the rendered plates
            this.renderedPlates.forEach((entityUUID, renderedNamePlate) -> {
                // check if entity is nearby to the player (in view range)
                for (Entity nearbyEntity : nearbyEntities)
                    if (nearbyEntity.getUniqueId().equals(entityUUID))
                        return;

                // get entity
                Entity entity = EntityUtils.getEntityInLoadedChunks(entityUUID);
                if (entity == null)
                    return;

                // check if plate is rendered permanently
                if (renderedNamePlate.getPlate().getRenderPermanently())
                    return;

                // get distance between player and entity
                double distance = player.getLocation().distance(entity.getLocation());

                //-------------- TODO probably remove this?
                // check if entity is in default view
                // if not, remove it
                if (viewDistance < distance) {
                    // remove from render
                    this.remove(player, renderedNamePlate, entity);
                    return;
                }
                //--------------

                // check if the entity is in plate's view
                // if not, remove it
                if (renderedNamePlate.getPlate().getViewDistance() < distance) {
                    // remove from render
                    this.remove(player, renderedNamePlate, entity);
                }
            });
        });
    }

    // tick internal
    // isNotBehindWall
    private boolean isVisible(Player player, Entity entity, int viewDistance) {
        Location playerLocation = player.getEyeLocation();
        Location entityLocation = entity.getLocation();
        if (entity instanceof LivingEntity livingEntity)
            entityLocation = livingEntity.getEyeLocation();
        else
            entityLocation.add(0, VersionProvider.getItem(WrappedItem.ENTITY).instantiate(entity).getHeight(), 0);

        Vector vectorToEntity = playerLocation.toVector().subtract(entityLocation.toVector());

        WrappedRayTraceResult result = VersionProvider.getItem(WrappedItem.RAY_TRACE).rayTrace(playerLocation, vectorToEntity, viewDistance);
        // raytrace says it's behind wall
        if (result.getHitBlock() != null) {
            // didn't hit the entity

            // check block transparency, solidity and fluidity
            if (!result.getHitBlock().getType().isOccluding()
            ||  result.getHitBlock().getType().isTransparent()
            || !result.getHitBlock().getType().isSolid()
            || result.getHitBlock().isLiquid())
                return true;
            // TODO raytrace past this block, see if there's other blocks blocking the entity

            // check if player is disabled from viewing any entities
            if (updater.namePlateHandler.disabledPlayers.contains(player.getUniqueId()))
                return false;

            // try to get the plate
            RenderedNamePlate renderedNamePlate = this.renderedPlates.get(entity.getUniqueId());

            // plate exists
            if (renderedNamePlate != null) {
                // check if plate is rendered permanently
                if (renderedNamePlate.getPlate().getRenderPermanently())
                    return true;

                // check for its own view distance
                if (renderedNamePlate.getPlate().getViewDistance() > player.getLocation().distance(entityLocation))
                    return false;

                // remove it from render
                this.remove(player, renderedNamePlate, entity);

                return false;
            }
        }

        return true;
    }

    private void conditionedRender(RenderedNamePlate renderedNamePlate, Player player, Entity entity) {
        NamePlate namePlate = renderedNamePlate.getPlate();
        // check if player is disabled from view
        if (updater.namePlateHandler.disabledPlayers.contains(player.getUniqueId()))
            return;

        // check if player is disallowed from view in plate context
        if (namePlate.getDisabledViewPlayers().contains(player.getUniqueId()))
            return;

        // get distance between player and entity
        double distance = player.getLocation().distance(entity.getLocation());

        // check if the entity is in plate's view
        // if not, don't render it
        if (namePlate.getViewDistance() < distance)
            return;

        // render plates
        this.render(player, namePlate, entity);
        // update viewing players
        ((ContextNamePlate) namePlate).getViewersSet().add(player.getUniqueId());
        renderedNamePlate.getViewers().add(player.getUniqueId());
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
        // check if the NamePlate is already in render
        if (!this.renderedPlates.containsKey(entityUUID)) {
            // not being rendered to any player

            final boolean[] cancelled = { false };
            // fire event for attach
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

            renderedNamePlate = new RenderedNamePlate(plate, false);
        }
        else {
            // the plate is already rendered to at least 1 player
            renderedNamePlate = this.renderedPlates.get(entityUUID);

            // create the "plates" (armor stands)
            renderedNamePlate.putArmorStands(this.createArmorStands(plate, attachedTo));
        }

        // update renderedNamePlate on the nameplate
        // just in case something changed
        renderedNamePlate.setPlate(plate);

        // render the plates
        this.sendArmorStands(false, player, plate, renderedNamePlate.getArmorStands(), attachedTo);

        // set as being in render if it isn't
        if (!this.renderedPlates.containsKey(entityUUID))
            this.renderedPlates.put(entityUUID, renderedNamePlate);

        // fire render toggle event
        // this will fire every time a player is sent a render of a plate
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

            @Override
            public RenderType getRenderType() {
                return RenderType.ADD;
            }
        });
    }

    private void remove(Player player, RenderedNamePlate renderedNamePlate, Entity attachedTo) {
        this.removeArmorStands(player, renderedNamePlate.getArmorStands());
        // remove the player from the viewers list
        ((ContextNamePlate) renderedNamePlate.getPlate()).getViewersSet().remove(player.getUniqueId());
        renderedNamePlate.getViewers().remove(player.getUniqueId());
        // remove if it's no longer being rendered to any player
        if (renderedNamePlate.getViewers().size() == 0)
            this.renderedPlates.remove(attachedTo.getUniqueId());

        // fire render toggle event
        // this will fire every time a player is sent an un-render of a plate
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

            @Override
            public RenderType getRenderType() {
                return RenderType.REMOVE;
            }
        });
    }

    public NamePlate removePermanently(Entity attachedTo) {
        RenderedNamePlate renderedNamePlate = this.renderedPlates.get(attachedTo.getUniqueId());
        if (renderedNamePlate == null) return null;

        // remove plate from all players
        Bukkit.getOnlinePlayers().forEach((player) -> this.remove(player, renderedNamePlate, attachedTo));

        // remove from cancelledAttachEntities to prevent pollution
        this.cancelledAttachEntities.forEach(pair -> {
            if (pair.getRight().equals(attachedTo.getUniqueId()))
                this.cancelledAttachEntities.remove(pair);
        });

        return renderedNamePlate.getPlate();
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