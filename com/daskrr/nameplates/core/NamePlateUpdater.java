package com.daskrr.nameplates.core;

import com.daskrr.nameplates.api.NamePlateAPIOptions;
import com.daskrr.nameplates.util.EntityUtils;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.UUID;

public class NamePlateUpdater {

    protected NamePlateHandler namePlateHandler;
    protected final BukkitTask ticker;
    private int wait = 0;

    protected final MovementManager movementManager;
    public final RenderManager renderManager;
    protected final EntityChecker entityChecker;

    public NamePlateUpdater(NamePlateHandler namePlateHandler) {
        this.namePlateHandler = namePlateHandler;
        this.movementManager = new MovementManager(this);
        this.renderManager = new RenderManager(this);
        this.entityChecker = new EntityChecker(this);

        // make runnable here for the ticking
        this.ticker = new BukkitRunnable() {
            @Override
            public void run() {
                // ticking based on interval
                boolean canTick = false;
                if (++wait == namePlateHandler.getOptions().getOption(NamePlateAPIOptions.Key.POSITION_UPDATE_TIME).getValue()) {
                    canTick = true;
                    wait = 0;
                }

                // still tick every tick, just let it know when the interval controlled tick is allowed
                NamePlateUpdater.this.tick(canTick);
            }
        }.runTaskTimer(namePlateHandler.plugin, 0, 1);
    }

    // This will resend the entity's metadata, created using the NamePlate's data
    // this is used when the api changes nameplate settings or text
    public void update(int id) {
        Pair<UUID, RenderedNamePlate> renderedNamePlate = this.renderManager.getRenderedNamePlate(id);
        // check if the entity is in render
        if (renderedNamePlate == null) return;

        Entity entity = EntityUtils.getEntityInLoadedChunks(renderedNamePlate.getLeft());
        // check if the entity exists (redundant?)
        if (entity == null) return;

        // generate new armor stands
        ArmorStand[] armorStands = this.renderManager.createArmorStands(renderedNamePlate.getRight().getPlate(), entity);

        // update armor stands
        renderedNamePlate.getRight().putArmorStands(armorStands);

        // send armor stands
        Lists.newArrayList(renderedNamePlate.getRight().getPlate().getViewers()).forEach(
                player -> this.renderManager.sendArmorStands(true, player, renderedNamePlate.getRight().getPlate(), armorStands, entity)
        );
    }

    // this is the core, it constantly teleports the armor stands using a MovementChecker around while they are in view;
    // uses a ViewChecker to check if the entity is visible and a DistanceChecker to see if the entity is in view distance
    // from here, the NamePlateRenderToggleEvent is triggered
    private void tick(boolean canTick) {
        if (canTick) {
            this.movementManager.tick();
            this.renderManager.tick();
        }
    }
}