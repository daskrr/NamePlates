package com.daskrr.nameplates.core;

import com.daskrr.nameplates.api.NamePlateAPI;
import com.daskrr.nameplates.api.NamePlateAPIOptions;
import com.daskrr.nameplates.api.nameplate.NamePlate;
import com.daskrr.nameplates.core.event.EventHandler;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

import java.util.UUID;

public class NamePlates {
	
	protected final NamePlatesPlugin plugin;
	private final NamePlateAPIOptions options;
	private final NamePlateUpdater updater;
	private final EventHandler eventHandler;

	private int lastId = 0;

	protected BiMap<Integer, NamePlate> namePlates = HashBiMap.create();
	protected BiMap<Integer, EntityGroup<?>> entityGroups = HashBiMap.create();

	protected BiMap<Integer, NamePlate> staticNamePlates = HashBiMap.create();
	protected BiMap<Integer, Location> staticLocations = HashBiMap.create();
	
	protected NamePlates(NamePlatesPlugin plugin) {
		this.plugin = plugin;
		this.updater = new NamePlateUpdater(this);
		this.eventHandler = new EventHandler(this); // - this handles entity spawn (attach nameplates), entity death (remove nameplates)
		this.options = new NamePlateAPIOptions();
	}

//	private void init() {
//
//	}

	public int addToPlayer(NamePlate plate, UUID player) {
		int id = this.lastId++;

		this.namePlates.put(id, plate);
		this.entityGroups.put(id, EntityGroup.PLAYER.set(player));

		return id;
	}

	public int addToPlayers(NamePlate plate, UUID... players) {
		int id = this.lastId++;

		this.namePlates.put(id, plate);
		this.entityGroups.put(id, EntityGroup.PLAYER.set(players));

		return id;
	}

	public int addToEntities(NamePlate plate, EntityType[] types, UUID... exclude) {
		int id = this.lastId++;

		this.namePlates.put(id, plate);
		this.entityGroups.put(id, EntityGroup.ENTITY_TYPE.set(types).setExcluded(EntityGroup.ENTITY.set(exclude)));

		return id;
	}

	public int addToWorld(NamePlate plate, Location location) {
		int id = this.lastId++;

		this.staticNamePlates.put(id, plate);
		this.staticLocations.put(id, location);

		return id;
	}

	public NamePlateAPIOptions getOptions() {
		return this.options;
	}
}
