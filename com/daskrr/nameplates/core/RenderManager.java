package com.daskrr.nameplates.core;

import com.daskrr.nameplates.api.NamePlateAPIOptions;
import com.daskrr.nameplates.api.nameplate.NamePlate;
import com.daskrr.nameplates.api.nameplate.NamePlateTextBuilder;
import com.daskrr.nameplates.api.util.BlockLocation;
import com.daskrr.nameplates.core.event.NamePlateAttachEvent;
import com.daskrr.nameplates.core.event.NamePlateRenderToggleEvent;
import com.daskrr.nameplates.util.EntityUtils;
import com.daskrr.nameplates.util.ItemUtils;
import com.daskrr.nameplates.util.NamePlateUtils;
import com.daskrr.nameplates.version.Version;
import com.daskrr.nameplates.version.VersionProvider;
import com.daskrr.nameplates.version.wrapped.WrappedItem;
import com.daskrr.nameplates.version.wrapped.bukkit.CompatibilityEntityType;
import com.daskrr.nameplates.version.wrapped.entity.WrappedEntityArmorStand;
import com.daskrr.nameplates.version.wrapped.entity.WrappedEntityItem;
import com.daskrr.nameplates.version.wrapped.network.WrappedNetworkManager;
import com.daskrr.nameplates.version.wrapped.network.protocol.*;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class RenderManager implements Listener {
    // REFACTORED
    // How it should work:
    // Entity enters player view
    //      check the entity for nameplate
    //          the entity has a nameplate, get ContextNamePlate
    //          everything is contained in the ContextNamePlate (no more rendered plates) <entity -> render(viewers, armor stands)>
    //          check if nameplate has armor stands stored for the entity it's attached to
    //              if it doesn't, make some and send them
    //              if it does just do nothing
    //              make sure the player is in the viewers set
    // Entity exists player view
    //      check the entity for nameplate
    //          the entity has a nameplate, get ContextNamePlate
    //          remove
    // Note: NamePlates can now only be updated using their #update method
    //     : Every method of rendering/removing is based on NamePlate id
    //     : Upon removal of the nameplate/entity group, the removing method will have to notify the renderManager about the change

    protected static final double OFFSET = .275D; // MARKER VALUE (1.8 R1 USERS ARE OUT OF LUCK)
    protected static final double PLATE_HEIGHT = .25D;

    private final BiMap<Integer, Entity> entities = HashBiMap.create();
    //                   Player  Entity
    public final List<Pair<UUID, UUID>> cancelledAttachEntities = Lists.newArrayList();

    private final NamePlateUpdater updater;

    protected RenderManager(NamePlateUpdater updater) {
        this.updater = updater;

        Bukkit.getPluginManager().registerEvents(this, updater.namePlateHandler.plugin);
    }

    // checks for entities that can be seen and renders their plates
    // checks for entities that cannot be seen and un-renders their plates, telling the movement manager to not move them
    // this also forces updates to the viewers of the NamePlate
    public void tick() {
        // get view distance
        int viewDistance = updater.namePlateHandler.getOptions().getOption(NamePlateAPIOptions.Key.VIEW_DISTANCE).getValue();
        // loop through all players
        Bukkit.getOnlinePlayers().forEach(player -> {

            // check if player is disabled from viewing entities
            if (this.updater.namePlateHandler.disabledPlayers.contains(player.getUniqueId())) {
                // remove all entities from the player's view
                this.updater.namePlateHandler.namePlates.forEach((id, namePlate) ->
                    ((ContextNamePlate) namePlate).getRenders().forEach((entityUUID, render) -> {
                        if (render.isInRender(player.getUniqueId())) {
                            Entity entity = EntityUtils.getEntityInLoadedChunks(entityUUID);
                            if (entity != null)
                                this.unrender(namePlate, player, entity);
                        }
                    })
                );

                // static as well
                this.updater.namePlateHandler.staticNamePlates.forEach((id, namePlate) ->
                    ((ContextNamePlate) namePlate).getRenders().forEach((entityUUID, render) -> {
                        if (render.isInRender(player.getUniqueId())) {
                            Entity entity = this.createEntityAttachment(entityUUID, this.updater.namePlateHandler.staticLocations.get(id));
                            if (entity != null)
                                this.unrender(namePlate, player, entity);
                        }
                    })
                );
            }

            // check for nearby entities
            List<Entity> nearbyEntities = Lists.newArrayList();

            // inject static holders
            // check if any of the locations is in the area of the player's view distance
            this.updater.namePlateHandler.staticLocations.forEach((plateId, location) -> {
                if (!location.getWorld().equals(player.getWorld()))
                    return;

                if (location.distance(player.getLocation()) <= viewDistance) {
                    // player can see static holder location
                    // inject entity
                    Location spawnLocation = location.center();
                    spawnLocation.setY(location.getBlockY());

                    nearbyEntities.add(this.createEntityAttachment(this.updater.namePlateHandler.staticAttachments.inverse().get(plateId), spawnLocation));
                }
            });

            nearbyEntities.addAll(player.getNearbyEntities(viewDistance, viewDistance, viewDistance));
            nearbyEntities.forEach(entity -> {
                // check if entity is dead
                if (entity.isDead())
                    return;

                // check if player is disconnecting
                // TODO check
                if (entity instanceof Player)
                    if (!((Player) entity).isOnline())
                        return;

                NamePlate namePlate;
                // check if entity is static entity holder
                if (this.updater.namePlateHandler.staticAttachments.containsKey(entity.getUniqueId()))
                    // entity is static holder
                    namePlate = this.updater.namePlateHandler.getNamePlate(this.updater.namePlateHandler.staticAttachments.get(entity.getUniqueId()));
                else
                    // nameplate is attached to an actual entity, get it
                    namePlate = this.updater.namePlateHandler.getNamePlateOf(entity);

                // check if entity has nameplate
                if (namePlate == null)
                    return;

                // check if entity is disabled
                if (!namePlate.isStatic())
                    if (entity.getType() == CompatibilityEntityType.FISHING_HOOK
                     || entity.getType() == CompatibilityEntityType.EXPERIENCE_ORB
                     || entity.getType() == CompatibilityEntityType.COMPLEX_PART
                     || entity.getType() == CompatibilityEntityType.LIGHTNING
                     || entity.getType() == CompatibilityEntityType.LEASH_HITCH
                     || entity.getType() == CompatibilityEntityType.UNKNOWN
                     || entity.getType() == CompatibilityEntityType.WEATHER

                     || entity.getType() == CompatibilityEntityType.ENDER_SIGNAL
                     || entity.getType() == CompatibilityEntityType.MINECART
                     || entity.getType() == CompatibilityEntityType.MINECART_CHEST
                     || entity.getType() == CompatibilityEntityType.MINECART_COMMAND
                     || entity.getType() == CompatibilityEntityType.MINECART_FURNACE
                     || entity.getType() == CompatibilityEntityType.MINECART_HOPPER
                     || entity.getType() == CompatibilityEntityType.MINECART_TNT
                     || entity.getType() == CompatibilityEntityType.MINECART_MOB_SPAWNER
                     || entity.getType() == CompatibilityEntityType.BOAT
                     || entity.getType() == CompatibilityEntityType.ARMOR_STAND) {
                        // remove entity from nameplates, since it's disabled
                        this.updater.namePlateHandler.removeFrom(entity.getUniqueId(), false);
                        return;
                    }

                if (VersionProvider.getInstance().getVersion().ordinal() < Version.v1_14_R1.ordinal() && // TODO check in which version those entity's events were added
                    (entity.getType() == CompatibilityEntityType.DROPPED_ITEM || entity.getType() == CompatibilityEntityType.FIREWORK || entity.getType() == CompatibilityEntityType.ARROW)) {
                    // remove entity from nameplates, since it's disabled
                    this.updater.namePlateHandler.removeFrom(entity.getUniqueId(), false);
                    return;
                }

                if (!namePlate.isStatic())
                    // TODO this may be unnecessary, check
                    // check if entity is excluded from nameplate
                    if (Lists.newArrayList(namePlate.getSharedGroup().getExcluded()).contains(entity.getUniqueId()))
                        return; // continue forEach

                // check if entity is disabled from attaching
                for (Pair<UUID, UUID> pair : this.cancelledAttachEntities)
                    if (pair.equals(Pair.of(player.getUniqueId(), entity.getUniqueId()))) {
                        this.unrender(namePlate, player, entity);
                        return;
                    }

                // rendering behind walls
                // check nameplate-specific setting
                if (!namePlate.getRenderBehindWalls()) {
                    // nameplate doesn't allow rendering, so
                    // ray trace the entity
                    if (this.rayTrace(player, entity, viewDistance))
                        // behind a wall
                        this.unrender(namePlate, player, entity);
                    else
                        // not behind a wall
                        this.render(namePlate, player, entity);
                }
                // nameplate setting says entity can render behind walls
                // check if global settings allow it
                else if (updater.namePlateHandler.getOptions().getOption(NamePlateAPIOptions.Key.RENDER_BEHIND_WALLS).getValue())
                    // rendering behind walls is allowed, render
                    this.render(namePlate, player, entity);
                else
                    // rendering behind walls is not globally allowed, check if entity is visible
                    if (this.rayTrace(player, entity, viewDistance))
                        // behind a wall
                        this.unrender(namePlate, player, entity);
                    else
                        // not behind a wall
                        this.render(namePlate, player, entity);
            });

            List<UUID> nearbyEntitiesUUID = Lists.newArrayList();
            nearbyEntities.forEach(entity -> nearbyEntitiesUUID.add(entity.getUniqueId()));

            // get all nameplates and filter the players that can't see the entity anymore
            this.updater.namePlateHandler.namePlates.forEach((id, namePlate) -> {
                ((ContextNamePlate) namePlate).getRenders().forEach((entityUUID, render) -> {
                    if (nearbyEntitiesUUID.contains(entityUUID))
                        return; // continue

                    // the entity is outside the player's view, unrender
//                    this.unrender(namePlate, player, nearbyEntities.get(nearbyEntitiesUUID.indexOf(entityUUID)));
                    Entity entity = EntityUtils.getEntityInLoadedChunks(entityUUID);
                    if (entity != null)
                        this.unrender(namePlate, player, entity);

                    // note: since the plate's view cannot exceed the global view, no additional distance check is required
                });
            });

            // static as well
            this.updater.namePlateHandler.staticNamePlates.forEach((id, namePlate) -> {
                ((ContextNamePlate) namePlate).getRenders().forEach((entityUUID, render) -> {
                    if (nearbyEntitiesUUID.contains(entityUUID))
                        return; // continue

                    // the entity is outside the player's view, unrender
                    Entity entity = this.createEntityAttachment(entityUUID, this.updater.namePlateHandler.staticLocations.get(id));
                    this.unrender(namePlate, player, entity);

                    // note: since the plate's view cannot exceed the global view, no additional distance check is required
                });
            });
        });
    }

    // rendering
    private void render(NamePlate namePlate, Player player, Entity entity) {
        // check if player is disallowed from view in plate context
        if (namePlate.getDisabledViewPlayers().contains(player.getUniqueId()))
            return;

        // check if entity is in the same dimension with the entity
        if (!player.getWorld().equals(entity.getWorld()))
            return;

        // check namePlate's view distance (which is supposed to be smaller than the global distance)
        // get distance between player and entity
        double distance = player.getLocation().distance(entity.getLocation());

        PlateRender render = ((ContextNamePlate) namePlate).getRender(entity.getUniqueId());

        // check if the entity is in plate's view
        if (namePlate.getViewDistance() < distance) {
            // and unrender it if it's in view
            if (render.isInRender(player.getUniqueId()))
                this.unrender(namePlate, player, entity);

            // don't render it
            return;
        }

        // render plates

        // check if armor stands exist
        if (render.getArmorStands().isEmpty()) {
            // they don't, make some
            if (namePlate.isStatic())
                this.createRender(player, render, namePlate, new BlockLocation(entity.getLocation()));
            else
                this.createRender(player, render, namePlate, entity);

//            ((ContextNamePlate) namePlate).getRender(entity.getUniqueId()).setArmorStands(armorStands);

            // fire event for attach
            final boolean[] cancelled = { false };
            this.updater.namePlateHandler.eventHandler.fireEvent(new NamePlateAttachEvent() {
                @Override
                public Entity getEntity() {
                    return entity;
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
                this.cancelledAttachEntities.add(Pair.of(player.getUniqueId(), entity.getUniqueId()));
                return; // no need to unrender, since we are certain that this plate was never rendered
            }
        }
        // armor stands exist, check if the player is not in the viewing list
        if (!render.isInRender(player.getUniqueId())) {
            // send the player the armor stands
            this.sendRender(false, player, namePlate, render, entity);

            // add the player as viewer
            render.getViewers().add(player.getUniqueId());

            // fire event for render
            this.updater.namePlateHandler.eventHandler.fireEvent(new NamePlateRenderToggleEvent() {
                @Override
                public Player getPlayer() {
                    return player;
                }

                @Override
                public BlockLocation getLocation() {
                    return namePlate.isStatic() ? new BlockLocation(entity.getLocation()) : null;
                }

                @Override
                public NamePlate getNamePlate() {
                    return namePlate;
                }

                @Override
                public Entity getEntity() {
                    return entity;
                }

                @Override
                public RenderType getRenderType() {
                    return RenderType.ADD;
                }
            });
        }
    }

    private void renderUpdate(Player player, NamePlate namePlate, Entity entity, boolean inherit) {
        // remove old ones if new ones were created
        if (!inherit)
            this.unrender(namePlate, player, entity, false);

        // update armor stands
//        List<ArmorStand> armorStands;
        PlateRender render = ((ContextNamePlate) namePlate).getRender(entity.getUniqueId());
        if (inherit) {
            if (namePlate.isStatic())
                this.updateRender(player, render, namePlate, new BlockLocation(entity.getLocation()));
            else
                this.updateRender(player, render, namePlate, entity);
        } else {
            if (namePlate.isStatic())
                this.createRender(player, render, namePlate, new BlockLocation(entity.getLocation()));
            else
                this.createRender(player, render, namePlate, entity);
        }

//        ((ContextNamePlate) namePlate).getRender(entity.getUniqueId()).setArmorStands(armorStands);

        // send them
        // THROUGH THE PIIIPE!! SWOOSH...
        this.sendRender(true, player, namePlate, render, entity);
    }

    private void unrender(NamePlate namePlate, Player player, Entity entity) {
        this.unrender(namePlate, player, entity, true);
    }
    private void unrender(NamePlate namePlate, Player player, Entity entity, boolean fireEvent) {
        // send unrender
        this.sendUnrender(player, ((ContextNamePlate) namePlate).getRender(entity.getUniqueId()));

        // remove the player as viewer
        ((ContextNamePlate) namePlate).getRender(entity.getUniqueId()).getViewers().remove(player.getUniqueId());

        // fire event for render
        if (fireEvent)
            this.updater.namePlateHandler.eventHandler.fireEvent(new NamePlateRenderToggleEvent() {
                @Override
                public Player getPlayer() {
                    return player;
                }

                @Override
                public BlockLocation getLocation() {
                    return namePlate.isStatic() ? new BlockLocation(entity.getLocation()) : null;
                }

                @Override
                public NamePlate getNamePlate() {
                    return namePlate;
                }

                @Override
                public Entity getEntity() {
                    return entity;
                }

                @Override
                public RenderType getRenderType() {
                    return RenderType.REMOVE;
                }
            });
    }

    // public

    public void remove(Entity entity) {
        // unrender
        this.updater.namePlateHandler.namePlates.forEach((id, namePlate) -> {
            ((ContextNamePlate) namePlate).getRenders().forEach((uuid, render) -> {
                if (entity.getUniqueId().equals(uuid))
                    render.getViewers().forEach(playerUUID ->
                        this.unrender(namePlate, Bukkit.getPlayer(playerUUID), entity)
                    );
            });
            ((ContextNamePlate) namePlate).getRenders().remove(entity.getUniqueId());
        });

        this.updater.namePlateHandler.staticNamePlates.forEach((id, namePlate) -> {
            ((ContextNamePlate) namePlate).getRenders().forEach((uuid, render) -> {
                if (entity.getUniqueId().equals(uuid))
                    render.getViewers().forEach(playerUUID ->
                        this.unrender(namePlate, Bukkit.getPlayer(playerUUID), entity)
                    );
            });
            ((ContextNamePlate) namePlate).getRenders().remove(entity.getUniqueId());
        });
    }

    public void update(NamePlate namePlate, boolean inherit) {
        ((ContextNamePlate) namePlate).getRenders().forEach((uuid, render) -> {
            Entity entity;
            if (!namePlate.isStatic()) {
                entity = EntityUtils.getEntity(uuid);
                if (entity == null)
                    return;
            }
            else
                entity = this.createEntityAttachment(uuid, this.updater.namePlateHandler.staticLocations.get(namePlate.getId()));

            if (entity.getUniqueId().equals(uuid))
                render.getViewers().forEach(playerUUID ->
                    this.renderUpdate(Bukkit.getPlayer(playerUUID), namePlate, entity, inherit)
                );
        });
    }
    public void update(Entity entity) {
        this.updater.namePlateHandler.namePlates.forEach((id, namePlate) -> {
            ((ContextNamePlate) namePlate).getRenders().forEach((uuid, render) -> {
                if (entity.getUniqueId().equals(uuid))
                    render.getViewers().forEach(playerUUID ->
                        this.renderUpdate(Bukkit.getPlayer(playerUUID), namePlate, entity, true)
                    );
            });
        });

        this.updater.namePlateHandler.staticNamePlates.forEach((id, namePlate) -> {
            ((ContextNamePlate) namePlate).getRenders().forEach((uuid, render) -> {
                if (entity.getUniqueId().equals(uuid))
                    render.getViewers().forEach(playerUUID ->
                        this.renderUpdate(Bukkit.getPlayer(playerUUID), namePlate, entity, true)
                    );
            });
        });
    }

    // event handlers
    @EventHandler
    public void onPlayerDisconnect(PlayerQuitEvent event) {
        this.onPlayerDisconnect(event.getPlayer());
    }

    @EventHandler
    public void onPlayerDisconnect(PlayerKickEvent event) {
        this.onPlayerDisconnect(event.getPlayer());
    }

    private void onPlayerDisconnect(Player player) {
        this.updater.namePlateHandler.namePlates.forEach((id, namePlate) -> {
            ((ContextNamePlate) namePlate).getRenders().forEach((entityUUID, render) -> {
                render.getViewers().remove(player.getUniqueId());
            });
        });

        this.updater.namePlateHandler.staticNamePlates.forEach((id, namePlate) -> {
            ((ContextNamePlate) namePlate).getRenders().forEach((entityUUID, render) -> {
                render.getViewers().remove(player.getUniqueId());
            });
        });
    }




    // util
    // THIS IS STILL EXPERIMENTAL
    // AND CAN USE A LOT OF RESOURCES
    // DISABLED BY DEFAULT
    private boolean rayTrace(Player player, Entity entity, int viewDistance) {
        Location playerLocation = player.getEyeLocation();
        Location entityLocation = entity.getLocation();
        entityLocation.add(0, VersionProvider.getItem(WrappedItem.ENTITY).instantiate(entity).getHeight(), 0);

        // floor locations Y
//        playerLocation.setY(playerLocation.getBlockY());
//        entityLocation.setY(entityLocation.getBlockY());

        // check if entity is in the same dimension with the entity
        if (!player.getWorld().equals(entity.getWorld()))
            return true; // solid world xD

        Vector vectorToEntity = entityLocation.clone().toVector().subtract(playerLocation.toVector());

        BlockIterator iterator = new BlockIterator(playerLocation.getWorld(), playerLocation.toVector(), vectorToEntity, 0, viewDistance);

        // iterate through the blocks in the line
        int dist = 0;
        while (iterator.hasNext()) {
            Block next = iterator.next();
            dist++;


            // check to see if distance exceeds distance between player and entity
            if (dist >= Math.floor(entityLocation.distance(playerLocation)))
                break;

            if (next == null) continue;
            if (next.getType() == null) continue;
            if (next.getType() == Material.AIR) continue;

            if (!next.getType().isSolid() || next.getType().isTransparent() || next.isLiquid())
                continue;

            // found a solid block
            return true;
        }

        return false;

//        WrappedRayTraceResult result = VersionProvider.getItem(WrappedItem.RAY_TRACE).rayTrace(playerLocation, vectorToEntity, viewDistance);
//
//        // this shouldn't be null
//        if (result == null)
//            return false; //TODO ??
//
//        // raytrace says it's behind wall
//        return result.getHitBlock() == null;
    }

    // injecting armor stands
    public Entity createEntityAttachment(UUID uuid, Location location) {
        WrappedEntityArmorStand armorStand = VersionProvider.getItem(WrappedItem.ENTITY_ARMOR_STAND).instantiate(location);
        armorStand.setUniqueId(uuid);

        return armorStand.getArmorStand();
    }

    protected void createRender(Player player, PlateRender render, NamePlate plate, Entity entity) {
        this.generatePacketEntities(player, render, plate, entity, new BlockLocation(entity.getLocation()), false);
    }
    protected void createRender(Player player, PlateRender render, NamePlate plate, BlockLocation location) {
        this.generatePacketEntities(player, render, plate, null, location, false);
    }

    protected void updateRender(Player player, PlateRender render, NamePlate plate, Entity entity) {
        this.generatePacketEntities(player, render, plate, entity, new BlockLocation(entity.getLocation()), true);
    }
    protected void updateRender(Player player, PlateRender render, NamePlate plate, BlockLocation location) {
        this.generatePacketEntities(player, render, plate, null, location, true);
    }

    public void generatePacketEntities(Player player, PlateRender render, NamePlate plate, @Nullable Entity entity, BlockLocation location, boolean inherit) {
        List<ArmorStand> armorStands = Lists.newArrayList();
        Map<Integer, Item> items = Maps.newHashMap();

        if (inherit) {
            armorStands = render.getArmorStands().isEmpty() ? Lists.newArrayList() : render.getArmorStands();
            items = render.getItems().isEmpty() ? Maps.newHashMap() : render.getItems();
        }


        for (int i = 0; i < plate.getBuilder().getLines().length; i++) {
            NamePlateTextBuilder.Line line = plate.getBuilder().getLines()[i];

            StringBuilder finalText = new StringBuilder();
            if (!line.hasItem())
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

                    String toFormat = component.getFormat();
                    // execute function and ignore type and format
                    if (entity != null) {
                        if (component.hasFunction()) {
                            toFormat = component.getFunction().apply(entity);
                        } else {
                            // fill format vars
                            if (entity instanceof LivingEntity) {
                                LivingEntity livingEntity = (LivingEntity) entity;
                                toFormat = toFormat.replaceAll("\\{HP}", String.format("%.0f", livingEntity.getHealth()));
                                toFormat = toFormat.replaceAll("\\{HP_MAX}", String.format("%.0f", livingEntity.getMaxHealth()));
                                toFormat = toFormat.replaceAll(
                                        "\\{HP_SQUARES}",
                                        EntityUtils.healthToSquares(component.getColor(), livingEntity.getHealth(), livingEntity.getMaxHealth()));

                                ItemStack mainHand = VersionProvider.getItem(WrappedItem.UTILS).getItemInMainHand(livingEntity);
                                ItemStack offHand = VersionProvider.getItem(WrappedItem.UTILS).getItemInOffHand(livingEntity);
                                ItemStack helmet = livingEntity.getEquipment().getHelmet();
                                ItemStack chestPlate = livingEntity.getEquipment().getChestplate();
                                ItemStack leggings = livingEntity.getEquipment().getLeggings();
                                ItemStack boots = livingEntity.getEquipment().getBoots();

                                toFormat = toFormat.replaceAll("\\{ENTITY_HOLDING}", ItemUtils.getName(mainHand));
                                toFormat = toFormat.replaceAll("\\{ENTITY_OFFHAND}", ItemUtils.getName(offHand));
                                toFormat = toFormat.replaceAll("\\{ENTITY_HELMET}", ItemUtils.getName(helmet));
                                toFormat = toFormat.replaceAll("\\{ENTITY_CHESTPLATE}", ItemUtils.getName(chestPlate));
                                toFormat = toFormat.replaceAll("\\{ENTITY_LEGGINGS}", ItemUtils.getName(leggings));
                                toFormat = toFormat.replaceAll("\\{ENTITY_BOOTS}", ItemUtils.getName(boots));
                                toFormat = toFormat.replaceAll("\\{NAME}", VersionProvider.getItem(WrappedItem.ENTITY).instantiate(entity).getName());
                            }
                        }
                    }

                    SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss"); // TODO add config section for this
                    String timeFormatted = formatter.format(new Date());

                    formatter = new SimpleDateFormat("MM/dd/yyyy"); // TODO add config section for this
                    String dateFormatted = formatter.format(new Date());

                    toFormat = toFormat.replaceAll("\\{ONLINE_PLAYERS}", String.valueOf(Bukkit.getOnlinePlayers().size()));
                    toFormat = toFormat.replaceAll("\\{MAX_PLAYERS}", String.valueOf(Bukkit.getMaxPlayers()));
                    toFormat = toFormat.replaceAll("\\{VIEWING_PLAYER}", player.getName());
                    toFormat = toFormat.replaceAll("\\{VIEWING_PLAYER_DISPLAY_NAME}", player.getDisplayName());
                    toFormat = toFormat.replaceAll("\\{TIME}", timeFormatted);
                    toFormat = toFormat.replaceAll("\\{DATE}", dateFormatted);
    //                    toFormat = toFormat.replaceAll("\\{TIME_MINECRAFT}", ?); TODO

                    finalText.append(toFormat);
                }
            else
                if (inherit && items.containsKey(i))
                    // modify item
                    items.get(i).setItemStack(line.getItem());
                else {
                    // build item
                    WrappedEntityItem item = VersionProvider.getItem(WrappedItem.ENTITY_ITEM).instantiate(line.getItem(), location.center());
                    items.put(i, (Item) item.getBukkitEntity());
                }



            // build armor stand
            WrappedEntityArmorStand armorStand = inherit ?
                    VersionProvider.getItem(WrappedItem.ENTITY_ARMOR_STAND).instantiate(armorStands.get(i))
                    :
                    VersionProvider.getItem(WrappedItem.ENTITY_ARMOR_STAND).instantiate(location.center());
            armorStand.setInvisible(true);
            armorStand.setInvulnerable(true);
            armorStand.setMarker(true);
            armorStand.setNoGravity(true);

            if (line.hasItem() || line.isEmpty())
                armorStand.setCustomNameVisible(false);
            else {
                armorStand.setCustomName(finalText.toString());
                armorStand.setCustomNameVisible(true);
            }

            if (inherit)
                armorStands.set(i, armorStand.getArmorStand());
            else
                armorStands.add(armorStand.getArmorStand());
        }

        render.setArmorStands(armorStands);
        render.setItems(items);
    }

    public double calculateY(int lineIndex, NamePlate plate, Entity entity) {
        double y = calculateY(lineIndex, plate, entity.getLocation().getY());

        if (plate.isStatic())
            return y;

        y += VersionProvider.getItem(WrappedItem.ENTITY).instantiate(entity).getHeight(); // add entity height

        // check if entity is player
        if (entity instanceof Player || entity.getType() == CompatibilityEntityType.PLAYER)
            y += PLATE_HEIGHT * 2; // add 2 plate height space to prevent overlap

        // TODO test, tweak
        // prevent overlap if entity has custom name
        if (entity.getCustomName() != null)
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
        if (lineIndex > 0) {
            // add all margins of all previous lines
            for (int i = 0; i < lineIndex; i++) {
                y += plate.getBuilder().getLines()[i].getMargin()[0];
                y += plate.getBuilder().getLines()[i].getMargin()[1];

                // add margin from previous lines if they have an item
                if (plate.getBuilder().getLine(i).hasItem())
                    y+= .35D;
            }
        }

        return y;
    }

    public double calculateYItem(int lineIndex, NamePlate plate, Entity entity) {
        double y = this.calculateY(lineIndex, plate, entity);

        // add item height
        y += .35D;

        return y;
    }

    public Location calculateOffsetPassengerOverlapScenario(int index, NamePlate plate, Entity entity) {
        return this.calculateOffsetPassengerOverlapScenario(index, plate, entity, entity.getLocation());
    }

    public Location calculateOffsetPassengerOverlapScenario(int index, NamePlate plate, Entity entity, Location entityLocation) {
        Location whereToRender = entityLocation.clone();
        double y = 0D;

        // overlap Scenario (in case of passengers)
        // check if entity has passengers
        List<Entity> passengers = VersionProvider.getItem(WrappedItem.UTILS).getEntityPassengers(entity);
        if (!passengers.isEmpty() || entity.isInsideVehicle()) {
            // get largest plate
            double largestPlate = 0D;
            for (int i = 0; i < ((ContextNamePlate) plate).getRender(entity.getUniqueId()).getArmorStands().size(); i++) {
                double plateWidth = NamePlateUtils.calculatePlateWidth(((ContextNamePlate) plate).getRender(entity.getUniqueId()).getArmorStands().get(i).getCustomName());
                if (plateWidth > largestPlate)
                    largestPlate = plateWidth;
            }

            // switch scenario
            switch (plate.getOverlapScenario()) {
                case OVERLAP -> {
                    ;
                }
                case ORDERED_ON_TOP -> {
                    // get all passengers' height
                    for (Entity passenger : passengers)
                        // add entity's passenger height
                        y += VersionProvider.getItem(WrappedItem.ENTITY).instantiate(passenger).getPassengerHeight();

                    // add all plate heights of vehicles
                    List<Entity> vehicles = VersionProvider.getItem(WrappedItem.UTILS).getEntityVehicles(entity);
                    for (Entity vehicle : vehicles) {
                        if (this.updater.namePlateHandler.getNamePlateOf(vehicle) != null) {
                            // add passenger plate height
                            NamePlateTextBuilder.Line[] lines = this.updater.namePlateHandler.getNamePlateOf(vehicle).getBuilder().getLines();
                            for (int i = 0; i < lines.length; i++) {
                                y += PLATE_HEIGHT; // don't add +1 since first line is the first
                                y += plate.getBuilder().getLines()[i].getMargin()[1]; // add margin bottom of this plate
                                y += plate.getBuilder().getLines()[i].getMargin()[0]; // add margin top of this plate

                                if (lines[i].hasItem())
                                    y += .35D;
                            }
//                            y -= OFFSET; // now the y for the plate is right above the entity's head (or block) (hit box-wise)
                            y += plate.getMarginBottom(); // add margin bottom of the text
                        }
                    }
                }
                case LEFT -> {
                    // get entity bounding box width
                    double offsetDistance = VersionProvider.getItem(WrappedItem.ENTITY).instantiate(entity).getBBWidth() / 2;

                    // shift the yaw to the left (negative) 90 degrees
                    Location location = whereToRender.clone();
                    location.setYaw(EntityUtils.shiftYaw(location.getYaw(), -90f));
                    // and remove pitch as it can screw with height
                    location.setPitch(0.0f);
                    Vector direction = location.getDirection();

                    direction.normalize().multiply(offsetDistance + largestPlate / 2);

                    // add the vector to the location
                    whereToRender.add(direction);

                    // add items height as well
                    double platesHeight = plate.getBuilder().getLines().length * PLATE_HEIGHT;
                    for (int i = 0; i < plate.getBuilder().getLines().length; i++) platesHeight += plate.getBuilder().getLine(i).hasItem() ? .35D : 0D;

                    // calculate plates height and translate it by half of its own height
                    whereToRender.subtract(0D, platesHeight / 2, 0D);

                    // remove half the entity's bounding box from the y (as it is being added as a whole and this needs to stay centered)
                    whereToRender.subtract(0D, VersionProvider.getItem(WrappedItem.ENTITY).instantiate(entity).getBBHeight() / 2, 0D);
                }
                case RIGHT -> {
                    // get entity bounding box width
                    double offsetDistance = VersionProvider.getItem(WrappedItem.ENTITY).instantiate(entity).getBBWidth() / 2;

                    // shift the yaw to the left (negative) 90 degrees
                    Location location = whereToRender.clone();
                    location.setYaw(EntityUtils.shiftYaw(location.getYaw(), 90f));
                    // and remove pitch as it can screw with height
                    location.setPitch(0.0f);
                    Vector direction = location.getDirection();

                    direction.normalize().multiply(offsetDistance + largestPlate / 2);

                    // add the vector to the location
                    whereToRender.add(direction);

                    // add items height as well
                    double platesHeight = plate.getBuilder().getLines().length * PLATE_HEIGHT;
                    for (int i = 0; i < plate.getBuilder().getLines().length; i++) platesHeight += plate.getBuilder().getLine(i).hasItem() ? .35D : 0D;

                    // calculate plates height and translate it by half of its own height
                    whereToRender.subtract(0D, platesHeight / 2, 0D);

                    // remove half the entity's bounding box from the y (as it is being added as a whole and this needs to stay centered)
                    whereToRender.subtract(0D, VersionProvider.getItem(WrappedItem.ENTITY).instantiate(entity).getBBHeight() / 2, 0D);
                }
                case HIDDEN -> {
                    return null;
                }
            }
        }

        whereToRender.add(0, y, 0);
        return whereToRender;
    }

    protected void sendRender (boolean metadataOnly, Player player, NamePlate plate, PlateRender render, Entity entity) {
        Location location = entity.getLocation();

        for (int lineIndex = 0; lineIndex < plate.getBuilder().getLines().length; lineIndex++) {
            // get the armor stand of this line
            WrappedEntityArmorStand armorStand = VersionProvider.getItem(WrappedItem.ENTITY_ARMOR_STAND).instantiate(render.getArmorStands().get(lineIndex));

            // check overlap scenario
            if (!plate.isStatic())
                location = this.calculateOffsetPassengerOverlapScenario(lineIndex, plate, entity);

            // check cancelled
            if (location == null)
                return;

            // update location
            location.setY(this.calculateY(lineIndex, plate, entity));

            // check for version v1_8_R1, as it's the only one that doesn't have "marker"
            if (VersionProvider.getInstance().getVersion() == Version.v1_8_R1)
                // it is, remove the height of the armor stand from the Y (since it's nameplate is displayed above the armor stand's bounding box
                location.setY((location.getY() - armorStand.getBBHeight()) + OFFSET);

            // check if line has item
            if (plate.getBuilder().getLines()[lineIndex].hasItem())
                // check if the version doesn't allow no-gravity on items
                if (VersionProvider.getInstance().getVersion().ordinal() <= Version.v1_8_R3.ordinal())
                    // set armor stand position to accommodate item position
                    location.setY(this.calculateYItem(lineIndex, plate, entity) - (armorStand.getBBHeight() * .65D));
                else
                    location.setY(this.calculateYItem(lineIndex, plate, entity));

            armorStand.setPosition(location);

            // send spawn packet and metadata packet
            WrappedPacketGameSpawnEntityLiving spawnPacket = VersionProvider.getItem(WrappedItem.PACKET_GAME_SPAWN_ENTITY_LIVING).instantiate(armorStand);
            WrappedPacketGameEntityMetadata metadataPacket = VersionProvider.getItem(WrappedItem.PACKET_GAME_ENTITY_METADATA).instantiate(
                    armorStand.getId(),
                    armorStand.getWrappedDataWatcher(),
                    false);

            WrappedNetworkManager networkManager = VersionProvider.getItem(WrappedItem.NETWORK_MANAGER).instantiate(player);
            if (!metadataOnly)
                networkManager.sendPacket(spawnPacket);
            networkManager.sendPacket(metadataPacket);

            // check if line has item
            if (plate.getBuilder().getLine(lineIndex).hasItem()) {
                WrappedEntityItem item = VersionProvider.getItem(WrappedItem.ENTITY_ITEM).instantiate(render.getItems().get(lineIndex));
                location.setY(this.calculateYItem(lineIndex, plate, entity));
                item.setPosition(location);
                item.setMotion(0D, 0D, 0D);
                item.setOnGround(true);

                WrappedPacketGameSpawnEntity spawnItemPacket = VersionProvider.getItem(WrappedItem.PACKET_GAME_SPAWN_ENTITY).instantiate(item, 2);
                WrappedPacketGameEntityMetadata metadataItemPacket = VersionProvider.getItem(WrappedItem.PACKET_GAME_ENTITY_METADATA).instantiate(
                        item.getId(),
                        item.getWrappedDataWatcher(),
                        false);

                WrappedPacketGameMountEntity mountItemPacket = VersionProvider.getItem(WrappedItem.PACKET_GAME_MOUNT_ENTITY).instantiate(0, item, armorStand);

                if (!metadataOnly)
                    networkManager.sendPacket(spawnItemPacket);
                networkManager.sendPacket(metadataItemPacket);
                if (!metadataOnly)
                    networkManager.sendPacket(mountItemPacket);
            }
        }
    }

    private void sendUnrender(Player player, PlateRender render) {
        // send entity destroy packet
        int[] ids = new int[render.getArmorStands().size() + render.getItems().size()];
        for (int i = 0; i < render.getArmorStands().size(); i++) ids[i] = render.getArmorStands().get(i).getEntityId();
        int i = render.getArmorStands().size();
        for (Map.Entry<Integer, Item> entry : render.getItems().entrySet()) ids[i++] = entry.getValue().getEntityId();

        WrappedPacketGameEntityDestroy destroyPacket = VersionProvider.getItem(WrappedItem.PACKET_GAME_DESTROY_ENTITY).instantiate(ids);

        WrappedNetworkManager networkManager = VersionProvider.getItem(WrappedItem.NETWORK_MANAGER).instantiate(player);
        networkManager.sendPacket(destroyPacket);
    }
}

