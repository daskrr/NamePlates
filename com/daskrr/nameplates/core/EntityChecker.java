package com.daskrr.nameplates.core;

public class EntityChecker {

    private final NamePlateUpdater updater;
    public EntityChecker(NamePlateUpdater updater) {
        this.updater = updater;
    }

    // checks entity for changes (based on events, not ticking)
    // to change nameplates (vars), permanently remove specific plates, remove rendered plates (for dead entities)
}
