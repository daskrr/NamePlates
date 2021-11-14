package com.daskrr.nameplates.core;

import com.daskrr.nameplates.api.nameplate.NamePlate;
import com.google.common.collect.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Deprecated
public class __NamePlateFactory {

    private static final __NamePlateFactory INSTANCE = new __NamePlateFactory();

    private final Map<UUID, RenderedNamePlate> plates = Maps.newHashMap();

    private __NamePlateFactory() {  }

    public RenderedNamePlate create(UUID uuid, NamePlate plate) {
        RenderedNamePlate renderedNamePlate = new RenderedNamePlate(plate, false);
        this.plates.put(uuid, renderedNamePlate);

        return renderedNamePlate;
    }

    public RenderedNamePlate createStatic(UUID uuid, NamePlate plate) {
        RenderedNamePlate renderedNamePlate = new RenderedNamePlate(plate, true);
        this.plates.put(uuid, renderedNamePlate);

        return renderedNamePlate;
    }

    public boolean contains(UUID uuid) {
        return this.plates.containsKey(uuid);
    }
    public boolean contains(NamePlate plate) {
        for (Map.Entry<UUID, RenderedNamePlate> entry : this.plates.entrySet()) {
            if (entry.getValue().getPlate().equals(plate))
                return true;
        }

        return false;
    }

    public RenderedNamePlate get(UUID uuid) {
        return this.plates.get(uuid);
    }
    public RenderedNamePlate get(NamePlate plate) {
        for (Map.Entry<UUID, RenderedNamePlate> entry : this.plates.entrySet()) {
            if (entry.getValue().getPlate().equals(plate))
                return entry.getValue();
        }

        return null;
    }

    public void remove(UUID uuid) {
        this.plates.remove(uuid);
    }

    public static __NamePlateFactory getInstance() {
        return INSTANCE;
    }
}
