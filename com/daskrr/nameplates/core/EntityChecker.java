package com.daskrr.nameplates.core;

import com.daskrr.nameplates.api.nameplate.NamePlate;
import com.daskrr.nameplates.api.util.BlockLocation;
import com.daskrr.nameplates.core.event.NamePlateDestroyEvent;
import com.daskrr.nameplates.util.EntityUtils;
import com.daskrr.nameplates.version.Version;
import com.daskrr.nameplates.version.VersionProvider;
import com.daskrr.nameplates.version.wrapped.WrappedItem;
import com.daskrr.nameplates.version.wrapped.entity.WrappedEntity;
import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class EntityChecker implements Listener {

    private final NamePlateUpdater updater;
    public EntityChecker(NamePlateUpdater updater) {
        this.updater = updater;
        Bukkit.getPluginManager().registerEvents(this, this.updater.namePlateHandler.plugin);
        VersionProvider.getItem(WrappedItem.COMPATIBILITY_LISTENER).registerEvents();
    }

    // checks entity for changes (based on events, not ticking)
    // to change nameplates (vars), permanently remove specific plates, remove rendered plates (for dead entities)

    // handle events for the nameplate handler

    // custom
    // TODO
//    public void itemDeath(Entity entity) {
//        this.entityRemove(entity, NamePlateDestroyEvent.Cause.ITEM_MERGE);
//    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        updater.namePlateHandler.namePlates.forEach((id, namePlate) -> {
            if (((ContextNamePlate) namePlate).getUpdaters().contains(ContextNamePlate.UpdateCriteria.PLAYER_JOIN))
                namePlate.update();
        });
    }
    @EventHandler
    public void onDisconnect(PlayerQuitEvent event) {
        updater.namePlateHandler.namePlates.forEach((id, namePlate) -> {
            if (((ContextNamePlate) namePlate).getUpdaters().contains(ContextNamePlate.UpdateCriteria.PLAYER_JOIN))
                namePlate.update();
        });
    }
    @EventHandler
    public void onDisconnect(PlayerKickEvent event) {
        updater.namePlateHandler.namePlates.forEach((id, namePlate) -> {
            if (((ContextNamePlate) namePlate).getUpdaters().contains(ContextNamePlate.UpdateCriteria.PLAYER_JOIN))
                namePlate.update();
        });
    }

    // health, name change, death, removal
    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        new BukkitRunnable() {
            public void run() {
                updater.renderManager.update(event.getEntity());
            }
        }.runTaskLater(this.updater.namePlateHandler.plugin, 1);
    }

    @EventHandler
    public void onEntityDamage(EntityRegainHealthEvent event) {
        new BukkitRunnable() {
            public void run() {
                updater.renderManager.update(event.getEntity());
            }
        }.runTaskLater(this.updater.namePlateHandler.plugin, 1);
    }

    @EventHandler
    public void onEntityInteract(PlayerInteractEntityEvent event) {
        new BukkitRunnable() {
            public void run() {
                updater.renderManager.update(event.getRightClicked());
            }
        }.runTaskLater(this.updater.namePlateHandler.plugin, 1);
    }

    @EventHandler
    public void onPlayerDisconnect(PlayerQuitEvent event) {
        this.entityRemove(event.getPlayer(), NamePlateDestroyEvent.Cause.PLAYER_DISCONNECT);
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        this.entityRemove(event.getPlayer(), NamePlateDestroyEvent.Cause.PLAYER_DISCONNECT);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        this.entityRemove(event.getEntity(), NamePlateDestroyEvent.Cause.ENTITY_DEATH);
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        this.entityRemove(event.getEntity(), NamePlateDestroyEvent.Cause.ENTITY_DEATH);
    }

    @EventHandler
    public void onEntityDeath(EntityExplodeEvent event) {
        this.entityRemove(event.getEntity(), NamePlateDestroyEvent.Cause.ENTITY_SELF_DEATH);
    }

    @EventHandler
    public void onEntityDeath(PlayerPickupItemEvent event) {
        this.entityRemove(event.getItem(), NamePlateDestroyEvent.Cause.ITEM_PICK_UP);
    }

    @EventHandler
    public void onEntityDeath(ProjectileHitEvent event) {
        if (VersionProvider.getInstance().getVersion() == Version.v1_8_R1)
            if (event.getEntity() instanceof Arrow)
                return;

        this.entityRemove(event.getEntity(), NamePlateDestroyEvent.Cause.PROJECTILE_HIT);
    }

    @EventHandler
    public void onEntityDeath(HangingBreakEvent event) {
        this.entityRemove(event.getEntity(), NamePlateDestroyEvent.Cause.ENTITY_BREAK);
    }

//    @EventHandler
//    public void onEntityDeath(FireworkExplodeEvent event) {
//        this.entityRemove(event.getEntity(), NamePlateDestroyEvent.Cause.ENTITY_BREAK);
//    }

//    @EventHandler
//    public void onEntityDeath(ItemMergeEvent event) {
//        this.entityRemove(event.getEntity(), NamePlateDestroyEvent.Cause.ENTITY_BREAK);
//    }

    @EventHandler
    public void onEntityDeath(EntityChangeBlockEvent event) {
        if (event.getEntity() instanceof FallingBlock)
            this.entityRemove(event.getEntity(), NamePlateDestroyEvent.Cause.ENTITY_BREAK);
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        Lists.newArrayList(event.getChunk().getEntities()).forEach(entity -> {
            // check if entity is persistent
            if (!VersionProvider.getItem(WrappedItem.UTILS).isPersistent((WrappedEntity) VersionProvider.getItem(WrappedItem.ENTITY).instantiate(entity)))
                // it's not, so it's "as good as dead" when the chunk unloads
                this.entityRemove(entity, NamePlateDestroyEvent.Cause.ENTITY_DESPAWN);
        });
    }

    @SuppressWarnings("unchecked")
    private void entityRemove(Entity entity, NamePlateDestroyEvent.Cause cause) {
        // static entities are not handled
        if (this.updater.namePlateHandler.staticAttachments.containsKey(entity.getUniqueId()))
            return;

        // check if entity has nameplate
        if (this.updater.namePlateHandler.getNamePlateOf(entity) == null)
            return;

        // get nameplate
        NamePlate namePlate = this.updater.namePlateHandler.getNamePlateOf(entity);

        // this will remove the rendered plate from the entity for good
        this.updater.renderManager.remove(entity);

        // check if entity had a rendered plate
        if (namePlate == null)
            return;

        boolean isPermanentlyDestroyed = true;
        // check if nameplate can still exist anywhere
        if (namePlate.getSharedGroup().getType() == EntityGroup.Type.ENTITY_TYPE)
            isPermanentlyDestroyed = false;
        else {
            // check if the entities still exist
            for (UUID uuid : ((EntityGroup<UUID>) namePlate.getSharedGroup()).get())
                if (EntityUtils.getEntity(uuid) != null)
                    isPermanentlyDestroyed = false;

            // check if players are still online
            for (UUID uuid : ((EntityGroup<UUID>) namePlate.getSharedGroup()).get())
                if (Bukkit.getPlayer(uuid).isOnline())
                    isPermanentlyDestroyed = false;
        }

        // remove nameplate from everywhere
        if (isPermanentlyDestroyed) {
            this.updater.namePlateHandler.namePlates.remove(namePlate.getId());
            this.updater.namePlateHandler.entityGroups.remove(namePlate.getId());
        }


        // fire event
        boolean finalIsPermanentlyDestroyed = isPermanentlyDestroyed;
        this.updater.namePlateHandler.eventHandler.fireEvent(new NamePlateDestroyEvent() {
            @Override
            public BlockLocation getLocation() {
                return null; // static entities are not subjected to this
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
            public Cause getCause() {
                return cause;
            }

            @Override
            public boolean isPermanentlyDestroyed() {
                return finalIsPermanentlyDestroyed;
            }
        });
    }
}
