package com.daskrr.nameplates.api;

import com.daskrr.nameplates.api.nameplate.NamePlate;
import com.daskrr.nameplates.api.util.BlockLocation;
import com.daskrr.nameplates.core.NamePlateHandler;
import com.daskrr.nameplates.core.NamePlatesPlugin;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.UUID;

public class NamePlateAPI {

	protected NamePlateHandler handler;

	public NamePlateAPI(NamePlateHandler handler) throws InstantiationException {
		if (NamePlatesPlugin.instance().apiHandler != null)
			throw new InstantiationException("NamePlateAPI has already been instantiated.");
		this.handler = handler;
	}

	// setters

	public int addToPlayer(NamePlate plate, Player player) {
		return this.addToPlayer(plate, player.getUniqueId());
	}
//	public int addToPlayer(NamePlate plate, OfflinePlayer player) {
//		return this.addToPlayer(plate, player.getUniqueId());
//	}
	public int addToPlayer(NamePlate plate, UUID player) {
		return this.handler.addToPlayer(plate, player);
	}

	public int addToPlayers(NamePlate plate, Player... players) {
		UUID[] uuids = new UUID[players.length];
		for (int i = 0; i < players.length; i++) uuids[i] = players[i].getUniqueId();
		return this.addToPlayers(plate, uuids);
	}
//	public int addToPlayers(NamePlate plate, OfflinePlayer... players) {
//		UUID[] uuids = new UUID[players.length];
//		for (int i = 0; i < players.length; i++) uuids[i] = players[i].getUniqueId();
//		return this.addToPlayers(plate, uuids);
//	}
	public int addToPlayers(NamePlate plate, UUID... players) {
		return this.handler.addToPlayers(plate, players);
	}

	public int addToEntity(NamePlate plate, UUID entity) {
		return this.handler.addToEntities(plate, entity);
	}
	public int addToEntities(NamePlate plate, UUID[] entities) {
		return this.handler.addToEntities(plate, entities);
	}
	public int addToEntities(NamePlate plate, EntityType... types) {
		return this.handler.addToEntities(plate, types);
	}
	public int addToEntities(NamePlate plate, EntityType[] types, UUID... exclude) {
		return this.handler.addToEntities(plate, types, exclude);
	}

	public int addToWorld(NamePlate plate, Location location) {
		return this.handler.addToWorld(plate, location);
	}

	// getters

	// get nameplate of an existing specified entity(/player) uuid/type
	public NamePlate getNamePlate(Player player) {
		return this.getNamePlate(player.getUniqueId());
	}
	public NamePlate getNamePlate(Entity entity) {
		return this.getNamePlate(entity.getUniqueId());
	}
	public NamePlate getNamePlate(UUID uuid) {
		return this.handler.getNamePlate(uuid);
	}
	public NamePlate getNamePlate(EntityType entityType) {
		return this.handler.getNamePlate(entityType);
	}
	public NamePlate getNamePlate(int id) {
		return this.handler.getNamePlate(id);
	}

	public NamePlate getNamePlateOf(Player player) {
		return this.getNamePlateOf(player.getUniqueId());
	}
	public NamePlate getNamePlateOf(Player player, boolean forceUnloadedChunks) {
		return this.getNamePlateOf(player.getUniqueId(), forceUnloadedChunks);
	}

	public NamePlate getNamePlateOf(Entity entity) {
		return this.getNamePlateOf(entity.getUniqueId());
	}
	public NamePlate getNamePlateOf(Entity entity, boolean forceUnloadedChunks) {
		return this.getNamePlateOf(entity.getUniqueId(), forceUnloadedChunks);
	}

	public NamePlate getNamePlateOf(UUID uuid) {
		return this.getNamePlateOf(uuid, false);
	}
	public NamePlate getNamePlateOf(UUID uuid, boolean forceUnloadedChunks) {
		return this.handler.getNamePlateOf(uuid, forceUnloadedChunks);
	}

	public NamePlate getNamePlate(Location location) {
		return this.handler.getNamePlate(new BlockLocation(location));
	}

	// removers

	public NamePlate removeNamePlate(int id) {
		return this.handler.removeNamePlate(id);
	}

	public NamePlate removeNamePlateFrom(Player player) {
		return this.removeNamePlateFrom(player.getUniqueId());
	}
	public NamePlate removeNamePlateFrom(Player player, boolean autoExcludeIfNotSpecific) {
		return this.removeNamePlateFrom(player.getUniqueId(), autoExcludeIfNotSpecific);
	}

	public NamePlate removeNamePlateFrom(Entity entity) {
		return this.removeNamePlateFrom(entity.getUniqueId());
	}
	public NamePlate removeNamePlateFrom(Entity entity, boolean autoExcludeIfNotSpecific) {
		return this.removeNamePlateFrom(entity.getUniqueId(), autoExcludeIfNotSpecific);
	}

	public NamePlate removeNamePlateFrom(UUID uuid) {
		return this.removeNamePlateFrom(uuid, false);
	}

	public NamePlate removeNamePlateFrom(UUID uuid, boolean autoExcludeIfNotSpecific) {
		return this.handler.removeFrom(uuid, autoExcludeIfNotSpecific);
	}

	// checks

	public boolean hasNamePlate(Player player) {
		return this.hasNamePlate(player.getUniqueId());
	}
	public boolean hasNamePlate(Entity entity) {
		return this.hasNamePlate(entity.getUniqueId());
	}
	public boolean hasNamePlate(UUID uuid) {
		return this.handler.hasNamePlate(uuid);
	}

	public boolean hasNamePlateUnsafe(Player player) {
		return this.hasNamePlateUnsafe(player.getUniqueId());
	}
	public boolean hasNamePlateUnsafe(Player player, boolean forceUnloadedChunks) {
		return this.hasNamePlateUnsafe(player.getUniqueId(), forceUnloadedChunks);
	}
	public boolean hasNamePlateUnsafe(Entity entity) {
		return this.hasNamePlateUnsafe(entity.getUniqueId());
	}
	public boolean hasNamePlateUnsafe(Entity entity, boolean forceUnloadedChunks) {
		return this.hasNamePlateUnsafe(entity.getUniqueId(), forceUnloadedChunks);
	}
	public boolean hasNamePlateUnsafe(UUID uuid) {
		return this.handler.hasNamePlateUnsafe(uuid, false);
	}
	public boolean hasNamePlateUnsafe(UUID uuid, boolean forceUnloadedChunks) {
		return this.handler.hasNamePlateUnsafe(uuid, forceUnloadedChunks);
	}

	// other

	public NamePlateAPIOptions getOptions() {
		return this.handler.getOptions();
	}

	public static NamePlateAPI getInstance() {
		if (NamePlatesPlugin.instance().apiHandler != null)
			return NamePlatesPlugin.instance().apiHandler;
		
		return null;
	}

	public void disableView(Player... players) {
		this.handler.disableView(players);
	}
	public void enableView(Player... players) {
		this.handler.enableView(players);
	}
}
