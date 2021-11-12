package com.daskrr.nameplates.core;

import com.daskrr.nameplates.api.nameplate.NamePlate;
import com.daskrr.nameplates.core.event.NamePlateDestroyEvent;
import com.daskrr.nameplates.util.EntityUtils;
import com.daskrr.nameplates.version.VersionProvider;
import com.daskrr.nameplates.version.wrapped.WrappedItem;
import com.daskrr.nameplates.version.wrapped.entity.WrappedEntity;
import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

import java.util.UUID;

public class EntityChecker implements Listener {

    private final NamePlateUpdater updater;
    public EntityChecker(NamePlateUpdater updater) {
        this.updater = updater;
        Bukkit.getPluginManager().registerEvents(this, this.updater.namePlateHandler.plugin);
    }

    // checks entity for changes (based on events, not ticking)
    // to change nameplates (vars), permanently remove specific plates, remove rendered plates (for dead entities)

    // handle events for the nameplate handler

    // NOT NEEDED?
    // handle event for player join (check all entity groups)
//    @SuppressWarnings("unchecked")
//    @EventHandler
//    public void onEvent(PlayerJoinEvent event) {
//        int plateId = -1;
//        for (Map.Entry<Integer, EntityGroup<?>> entry : this.namePlateHandler.entityGroups.entrySet()) {
//            EntityGroup<?> group = entry.getValue();
//            if (group.getType() == EntityGroup.Type.ENTITY)
//                if (!Lists.newArrayList(((EntityGroup<UUID>) group).get()).contains(event.getPlayer().getUniqueId()))
//                    return;
//            plateId = entry.getKey();
//        }
//
//        if (plateId == -1)
//            return;
//
//        this.namePlateHandler.namePlates.get(plateId)
//    }

    @EventHandler
    public void onPlayerDisconnect(PlayerQuitEvent event) {
        this.entityDeath(event.getPlayer());
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        this.entityDeath(event.getPlayer());
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        this.entityDeath(event.getEntity());
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        this.entityDeath(event.getEntity());
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        Lists.newArrayList(event.getChunk().getEntities()).forEach(entity -> {
            // check if entity is persistent
            if (!VersionProvider.getItem(WrappedItem.UTILS).isPersistent((WrappedEntity) VersionProvider.getItem(WrappedItem.ENTITY).instantiate(entity)))
                // it's not, so it's "as good as dead" when the chunk unloads
                this.entityDeath(entity);
        });
    }

    @SuppressWarnings("unchecked")
    private void entityDeath(Entity entity) {
        // this will remove the rendered plate from the entity for good
        NamePlate namePlate = this.updater.renderManager.removePermanently(entity);

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
            public NamePlate getNamePlate() {
                return namePlate;
            }

            @Override
            public Entity getEntity() {
                return entity;
            }

            @Override
            public Cause getCause() {
                return Cause.ENTITY_DEATH;
            }

            @Override
            public boolean isPermanentlyDestroyed() {
                return finalIsPermanentlyDestroyed;
            }
        });
    }
}
