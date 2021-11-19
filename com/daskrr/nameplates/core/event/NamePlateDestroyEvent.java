package com.daskrr.nameplates.core.event;

public interface NamePlateDestroyEvent extends StaticNamePlateEvent {

    Cause getCause();
    boolean isPermanentlyDestroyed();

    public enum Cause {
        ENTITY_DEATH,
        ENTITY_SELF_DEATH,
        ENTITY_DESPAWN,
        ENTITY_BREAK, // paintings and item frames
        ITEM_PICK_UP,
        ITEM_MERGE,
        PROJECTILE_HIT,
        PLAYER_DISCONNECT,
        MANUAL_REMOVAL
    }
}
