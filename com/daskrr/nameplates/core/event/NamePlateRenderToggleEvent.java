package com.daskrr.nameplates.core.event;

import org.bukkit.entity.Player;

// this fires every time a nameplate (for any player) gets rendered or un-rendered.
// this can fire even if the nameplate is already rendered to another player
public interface NamePlateRenderToggleEvent extends StaticNamePlateEvent{
    Player getPlayer();
    RenderType getRenderType();

    enum RenderType {
        ADD,
        REMOVE
    }
}
