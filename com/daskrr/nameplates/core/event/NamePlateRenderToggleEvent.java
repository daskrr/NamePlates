package com.daskrr.nameplates.core.event;

import org.bukkit.entity.Player;

public interface NamePlateRenderToggleEvent extends StaticNamePlateEvent{
    Player getPlayer();
}
