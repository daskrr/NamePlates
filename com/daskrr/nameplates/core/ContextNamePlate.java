package com.daskrr.nameplates.core;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public abstract class ContextNamePlate {
    protected abstract void setId(int id);
    protected abstract void setGroup(EntityGroup<?> group);
    protected abstract void setStatic();
    protected abstract Set<UUID> getViewersSet();

    protected abstract Map<UUID, PlateRender> getRenders();
    protected abstract PlateRender getRender(UUID entityUUID);

    protected abstract void setUpdaters(UpdateCriteria... criteria);
    protected abstract List<UpdateCriteria> getUpdaters();
    protected abstract void checkUpdaters();

    public enum UpdateCriteria {
        PLAYER_JOIN,
        TIME(20),
        DATE(200),
        DISPLAY_NAME_CHANGE(200),
        ENTITY_EQUIPMENT(200);

        private int ticks = -1;
        UpdateCriteria() {  }
        UpdateCriteria(int ticks) {
            this.ticks = ticks;
        }

        public int getTicks() {
            return ticks;
        }
    }
}
