package com.daskrr.nameplates.core;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Item;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class PlateRender {
    private List<ArmorStand> armorStands = Lists.newArrayList();
    private Map<Integer, Item> items = Maps.newHashMap();
    private final Set<UUID> viewers = Sets.newHashSet();

    public PlateRender() {}

    public List<ArmorStand> getArmorStands() {
        return this.armorStands;
    }
    public Map<Integer, Item> getItems() {
        return this.items;
    }

    public void setArmorStands(@Nonnull List<ArmorStand> armorStands) {
        this.armorStands = armorStands;
    }
    public void setItems(@Nonnull Map<Integer, Item> items) {
        this.items = items;
    }

    public Set<UUID> getViewers() {
        return this.viewers;
    }

    public boolean isInRender(UUID playerUUID) {
        return this.viewers.contains(playerUUID);
    }
}
