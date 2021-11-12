package com.daskrr.nameplates.core;

import com.daskrr.nameplates.api.NamePlateAPIOptions;
import com.daskrr.nameplates.api.nameplate.NamePlate;
import com.daskrr.nameplates.core.event.EventsHandler;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class NamePlateHandler {
	
	public final NamePlatesPlugin plugin;
	private final NamePlateAPIOptions options;
	public final NamePlateUpdater updater;
	protected final EventsHandler eventHandler;

	private int lastId = 0;

	public final BiMap<Integer, NamePlate> namePlates = HashBiMap.create();
	public final BiMap<Integer, EntityGroup<?>> entityGroups = HashBiMap.create();

	public final BiMap<Integer, NamePlate> staticNamePlates = HashBiMap.create();
	public final BiMap<Integer, Location> staticLocations = HashBiMap.create();

	protected List<UUID> disabledPlayers = Lists.newArrayList();
	
	protected NamePlateHandler(NamePlatesPlugin plugin) {
		this.plugin = plugin;
		this.updater = new NamePlateUpdater(this);
		this.eventHandler = new EventsHandler(this); // - this handles entity spawn (attach nameplates), entity death (remove nameplates)
		this.options = new NamePlateAPIOptions();
	}

//	private void init() {
//
//	}

	public int addToPlayer(NamePlate plate, UUID player) {
		int id = this.lastId++;
		EntityGroup<?> group = EntityGroup.ENTITY.set(player);

		((ContextNamePlate) plate).setId(id);
		((ContextNamePlate) plate).setGroup(group);
		this.namePlates.put(id, plate);
		this.entityGroups.put(id, group);

		return id;
	}

	public int addToPlayers(NamePlate plate, UUID... players) {
		int id = this.lastId++;
		EntityGroup<?> group = EntityGroup.ENTITY.set(players);

		((ContextNamePlate) plate).setId(id);
		((ContextNamePlate) plate).setGroup(group);
		this.namePlates.put(id, plate);
		this.entityGroups.put(id, group);

		return id;
	}

	public int addToEntities(NamePlate plate, EntityType[] types, UUID... exclude) {
		int id = this.lastId++;
		EntityGroup<?> group = EntityGroup.ENTITY_TYPE.set(types).setExcluded(EntityGroup.ENTITY.set(exclude));

		((ContextNamePlate) plate).setId(id);
		((ContextNamePlate) plate).setGroup(group);
		this.namePlates.put(id, plate);
		this.entityGroups.put(id, group);

		return id;
	}

	public int addToWorld(NamePlate plate, Location location) {
		int id = this.lastId++;

		((ContextNamePlate) plate).setId(id);
		this.staticNamePlates.put(id, plate);
		this.staticLocations.put(id, location);

		return id;
	}

	public void disableView(Player... players) {
		List<UUID> playerUUIDS = Lists.newArrayList();
		for (Player player : players) playerUUIDS.add(player.getUniqueId());
		this.disabledPlayers.addAll(playerUUIDS);
	}

	public void enableView(Player... players) {
		List<UUID> playerUUIDS = Lists.newArrayList();
		for (Player player : players) playerUUIDS.add(player.getUniqueId());
		this.disabledPlayers.removeAll(playerUUIDS);
	}

	// cancelled attaches on entities (undo) from updater/renderManager
	// allows specified entity to attach again
	public void allowAttachEntity(UUID playerUUID, UUID entityUUID) {
		// TODO not sure if this can work by making a new pair, will have to test
		this.updater.renderManager.cancelledAttachEntities.remove(Pair.of(playerUUID, entityUUID));
	}

	public NamePlateAPIOptions getOptions() {
		return this.options;
	}
}
