package com.daskrr.nameplates.core;

import com.daskrr.nameplates.api.NamePlateAPIOptions;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class NamePlateUpdater {

    protected NamePlates methodHandler;
    private final BukkitTask ticker;
    private int wait = 0;

    protected final MovementManager movementManager;
    protected final RenderChecker renderChecker;

    public NamePlateUpdater(NamePlates methodHandler) {
        this.methodHandler = methodHandler;
        this.movementManager = new MovementManager(this);
        this.renderChecker = new RenderChecker(this);

        // make runnable here for the ticking
        this.ticker = new BukkitRunnable() {
            @Override
            public void run() {
                // ticking based on interval
                boolean canTick = false;
                if (++wait == methodHandler.getOptions().getOption(NamePlateAPIOptions.Key.POSITION_UPDATE_TIME).getValue()) {
                    canTick = true;
                    wait = 0;
                }

                // still tick every tick, just let it know when the interval controlled tick is allowed
                NamePlateUpdater.this.tick(canTick);
            }
        }.runTaskTimer(methodHandler.plugin, 0, 1);
    }

    // This will resend the entity's metadata, created using the NamePlate's data
    public void update(int id) {

    }

    // this is the core, it constantly teleports the armor stands using a MovementChecker around while they are in view;
    // uses a ViewChecker to check if the entity is visible and a DistanceChecker to see if the entity is in view distance
    // from here, the NamePlateRenderToggleEvent is triggered
    private void tick(boolean canTick) {
        if (canTick) {
            this.movementManager.tick();
        }
    }

    protected void createNamePlate() {

    }
}