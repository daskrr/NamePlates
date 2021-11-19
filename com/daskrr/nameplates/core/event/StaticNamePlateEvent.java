package com.daskrr.nameplates.core.event;

import com.daskrr.nameplates.api.util.BlockLocation;
import org.bukkit.World;
import org.bukkit.craftbukkit.libs.jline.internal.Nullable;

/* THIS DOES NOT IMPLY THAT THE EVENT HAS TAKEN PLACE IN THE CONTEXT OF A STATIC NAME PLATE */
public interface StaticNamePlateEvent extends NamePlateEvent {
    @Nullable
    BlockLocation getLocation();
}
