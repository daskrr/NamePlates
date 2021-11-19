package com.daskrr.nameplates.core;

import com.daskrr.nameplates.api.NamePlateAPIOptions;
import com.daskrr.nameplates.api.nameplate.NamePlate;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;

public class NamePlateUpdater {

    protected NamePlateHandler namePlateHandler;
    protected final BukkitTask ticker;
    private int wait = 0;

    protected final MovementManager movementManager;
    public final RenderManager renderManager;
    public final EntityChecker entityChecker;

    public Multimap<Integer, ContextNamePlate.UpdateCriteria> namePlateUpdaters = HashMultimap.create();
    private Multimap<Integer, BukkitTask> namePlateUpdaterTasks = HashMultimap.create();

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
        this.update(id, false);
    }
    public void update(int id, boolean inherit) {
        NamePlate namePlate = this.namePlateHandler.getNamePlate(id);
        // check if the name plate exists
        if (namePlate == null) return;

        // update plate
        this.renderManager.update(namePlate, inherit);
    }

    public void putNamePlateUpdaters(int id, ContextNamePlate.UpdateCriteria... updaters) {
        this.removeNamePlateUpdaters(id);

        this.namePlateUpdaters.putAll(id, Lists.newArrayList(updaters));
        Lists.newArrayList(updaters).forEach(criteria -> {
            if (criteria.getTicks() == -1)
                return;

            int updateTime = criteria.getTicks();

            BukkitTask task = new BukkitRunnable() {
                @Override
                public void run() {
                    update(id, true);
                }
            }.runTaskTimer(this.namePlateHandler.plugin, 0, updateTime);

            this.namePlateUpdaterTasks.put(id, task);
        });
    }

    public void removeNamePlateUpdaters(int id) {
        this.namePlateUpdaters.removeAll(id);
        this.namePlateUpdaterTasks.removeAll(id).forEach(BukkitTask::cancel);

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