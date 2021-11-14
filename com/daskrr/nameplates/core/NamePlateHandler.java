package com.daskrr.nameplates.core;

import com.daskrr.nameplates.api.NamePlateAPIOptions;
import com.daskrr.nameplates.api.nameplate.NamePlate;
import com.daskrr.nameplates.api.util.BlockLocation;
import com.daskrr.nameplates.core.event.EventsHandler;
import com.daskrr.nameplates.util.EntityUtils;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class NamePlateHandler {
	
	public final NamePlatesPlugin plugin;
	private final NamePlateAPIOptions options;
	public final NamePlateUpdater updater;
	protected final EventsHandler eventHandler;

	private int lastId = 0;

	public final Map<Integer, NamePlate> namePlates = Maps.newHashMap();
	public final Map<Integer, EntityGroup<?>> entityGroups = Maps.newHashMap();

	public final Map<Integer, NamePlate> staticNamePlates = Maps.newHashMap();
	public final BiMap<Integer, BlockLocation> staticLocations = HashBiMap.create(); // TODO make hashmap ?
	public final BiMap<UUID, Integer> staticAttachments = HashBiMap.create();

	protected List<UUID> disabledPlayers = Lists.newArrayList();
	
	public NamePlateHandler(NamePlatesPlugin plugin) {
		this.plugin = plugin;
		this.updater = new NamePlateUpdater(this);
		this.eventHandler = new EventsHandler(this); // - this handles entity spawn (attach nameplates), entity death (remove nameplates)
		this.options = new NamePlateAPIOptions();
	}

//	private void init() {
//
//	}

	// setting

	@SuppressWarnings("unchecked")
	public int addToPlayer(NamePlate plate, UUID playerUUID) {
		Player player = Bukkit.getPlayer(playerUUID);

		// offline players cannot be added
		if (!player.isOnline())
			return -1;

		int id = -1;
		for (Map.Entry<Integer, EntityGroup<?>> entry : this.entityGroups.entrySet()) {
			EntityGroup<?> group = entry.getValue();
			if (group.getType() == EntityGroup.Type.ENTITY_TYPE) {
				if (Lists.newArrayList(((EntityGroup<EntityType>) group).get()).contains(player.getType()))
					id = entry.getKey();
			}
			else
				if (Lists.newArrayList(((EntityGroup<UUID>) group).get()).contains(playerUUID)) {
					id = entry.getKey();
					break; // UUID (specific entity) overrides entity type
				}
		}

		// make new entity group and either replace group and nameplate, either put new
		id = id == -1 ? this.lastId++ : id;
		EntityGroup<?> group = EntityGroup.ENTITY.set(playerUUID);

		((ContextNamePlate) plate).setId(id);
		((ContextNamePlate) plate).setGroup(group);
		this.namePlates.put(id, plate);
		this.entityGroups.put(id, group);

		return id;
	}

	@SuppressWarnings("unchecked")
	public int addToPlayers(NamePlate plate, UUID... players) {
		// this checks if player already has been assigned to a group (specific) and removes them from said group
		// also checks if players are online
		List<UUID> onlinePlayers = Lists.newArrayList();
		for (UUID playerUUID: players) {
			Player player = Bukkit.getPlayer(playerUUID);

			// offline players cannot be added
			if (player.isOnline())
				onlinePlayers.add(playerUUID);

			for (Map.Entry<Integer, EntityGroup<?>> entry : this.entityGroups.entrySet()) {
				EntityGroup<?> group = entry.getValue();
				if (group.getType() == EntityGroup.Type.ENTITY) { // not checking for types
					List<UUID> uuids = Lists.newArrayList(((EntityGroup<UUID>) group).get());
					if (uuids.contains(playerUUID)) {
						if (uuids.size() == 1)
							this.removeNamePlate(entry.getKey()); // removes plate permanently as there is no other criteria to follow
						else {
							uuids.remove(playerUUID);
							((EntityGroup<UUID>) this.entityGroups.get(entry.getKey())).set(uuids.toArray(new UUID[0]));
						}
					}
				}
			}
		}

		int id = this.lastId++;
		EntityGroup<?> group = EntityGroup.ENTITY.set(onlinePlayers.toArray(new UUID[0]));

		((ContextNamePlate) plate).setId(id);
		((ContextNamePlate) plate).setGroup(group);
		this.namePlates.put(id, plate);
		this.entityGroups.put(id, group);

		return id;
	}

	@SuppressWarnings("unchecked")
	public int addToEntities(NamePlate plate, EntityType[] types, UUID... exclude) {
		// remove similar types that already exist
		for (EntityType entityType : types) {
			for (Map.Entry<Integer, EntityGroup<?>> entry : this.entityGroups.entrySet()) {
				EntityGroup<?> group = entry.getValue();
				if (group.getType() == EntityGroup.Type.ENTITY_TYPE) { // check only for types
					List<EntityType> groupTypes = Lists.newArrayList(((EntityGroup<EntityType>) group).get());
					if (groupTypes.contains(entityType)) {
						if (groupTypes.size() == 1)
							this.removeNamePlate(entry.getKey()); // removes plate permanently as there is no other criteria to follow
						groupTypes.remove(entityType);
						((EntityGroup<EntityType>) this.entityGroups.get(entry.getKey())).set(groupTypes.toArray(new EntityType[0]));
					}
				}
			}
		}

		int id = this.lastId++;
		EntityGroup<?> group = EntityGroup.ENTITY_TYPE.set(types).setExcluded(exclude);

		((ContextNamePlate) plate).setId(id);
		((ContextNamePlate) plate).setGroup(group);
		this.namePlates.put(id, plate);
		this.entityGroups.put(id, group);

		return id;
	}

	@SuppressWarnings("unchecked")
	public int addToEntities(NamePlate plate, UUID... entities) {
		// remove similar types that already exist
		for (UUID entityUUID : entities) {
			for (Map.Entry<Integer, EntityGroup<?>> entry : this.entityGroups.entrySet()) {
				EntityGroup<?> group = entry.getValue();
				if (group.getType() == EntityGroup.Type.ENTITY) { // not checking for types
					List<UUID> uuids = Lists.newArrayList(((EntityGroup<UUID>) group).get());
					if (uuids.contains(entityUUID)) {
						if (uuids.size() == 1)
							this.removeNamePlate(entry.getKey()); // removes plate permanently as there is no other criteria to follow
						else {
							uuids.remove(entityUUID);
							((EntityGroup<UUID>) this.entityGroups.get(entry.getKey())).set(uuids.toArray(new UUID[0]));
						}
					}
				}
			}
		}

		int id = this.lastId++;
		EntityGroup<?> group = EntityGroup.ENTITY.set(entities);

		((ContextNamePlate) plate).setId(id);
		((ContextNamePlate) plate).setGroup(group);
		this.namePlates.put(id, plate);
		this.entityGroups.put(id, group);

		return id;
	}

	// static
	public int addToWorld(NamePlate plate, Location location) {
		return this.addToWorld(plate, new BlockLocation(location));
	}
	public int addToWorld(NamePlate plate, BlockLocation location) {
		int id = this.lastId++;

		((ContextNamePlate) plate).setId(id);
		this.staticNamePlates.put(id, plate);
		this.staticLocations.put(id, new BlockLocation(location));

		// make armor stand as "replacement" entity
		// TODO

		return id;
	}

	// getting

	public NamePlate getNamePlate(int id) {
		NamePlate namePlate = this.namePlates.get(id);
		return namePlate == null ? this.staticNamePlates.get(id) : namePlate;
	}

	@SuppressWarnings("unchecked")
	public NamePlate getNamePlate(EntityType type) {
		for (Map.Entry<Integer, EntityGroup<?>> entry : this.entityGroups.entrySet()) {
			if (entry.getValue().getType() != EntityGroup.Type.ENTITY_TYPE)
				continue;

			if (Lists.newArrayList(((EntityGroup<EntityType>) entry.getValue()).get()).contains(type))
				return this.namePlates.get(entry.getKey());
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	public NamePlate getNamePlate(UUID uuid) {
		for (Map.Entry<Integer, EntityGroup<?>> entry : this.entityGroups.entrySet()) {
			if (entry.getValue().getType() != EntityGroup.Type.ENTITY)
				continue;

			if (Lists.newArrayList(((EntityGroup<UUID>) entry.getValue()).get()).contains(uuid))
				return this.namePlates.get(entry.getKey());
		}

		return null;
	}

	// returns the nameplate of any entity (if it exists) including those that are not specifically set or in render
	@SuppressWarnings("unchecked")
	public NamePlate getNamePlateOf(UUID uuid, boolean forceUnloadedChunks) {
		NamePlate plate;

		// try to get nameplate from nameplates
		plate = this.getNamePlate(uuid);
		if (plate != null) return plate;

		// THIS IS NOT GOOD PRACTICE
		// this could lead to a plate never being able to be removed, as if the uuid changes "camps" from one type to a
		// specific or is being removed from somewhere and put elsewhere, then the rendered plate would never get removed
		// and recreated according to the new nameplate

		// try to get plate from rendered plates
//		RenderedNamePlate renderedPlate;
//		if ((renderedPlate = this.updater.renderManager.renderedPlates.get(uuid)) != null)
//			plate = renderedPlate.getPlate();
//		if (plate != null) return plate;

		// try to manually assign to entity
		Entity entity;
		if (forceUnloadedChunks)
			entity = EntityUtils.getEntity(uuid);
		else
			entity = EntityUtils.getEntityInLoadedChunks(uuid);

		if (entity == null) return null;

		for (Map.Entry<Integer, EntityGroup<?>> entry : this.entityGroups.entrySet()) {
			if (entry.getValue().getType() != EntityGroup.Type.ENTITY_TYPE)
				continue;

			// check if entity exists in this entity group and is not excluded
			if (Lists.newArrayList(((EntityGroup<EntityType>) entry.getValue()).get()).contains(entity.getType())
			 && !Lists.newArrayList(entry.getValue().getExcluded()).contains(entity.getUniqueId()))
				return this.namePlates.get(entry.getKey());
		}

		return null;
	}

	// static

	public NamePlate getNamePlate(BlockLocation location) {
		if (this.staticLocations.containsValue(location))
			return this.staticNamePlates.get(this.staticLocations.inverse().get(location));

		return null;
	}

	// removers
	public NamePlate removeNamePlate(int id) {
		NamePlate namePlate = this.namePlates.remove(id);
		this.entityGroups.remove(id);
		// the renderManager will automatically remove the entity from its records and un-render it

		return namePlate;
	}

	@SuppressWarnings("unchecked")
	public NamePlate removeFrom(UUID uuid, boolean autoExcludeIfNotSpecific) {
		Entity entity = EntityUtils.getEntity(uuid);
		if (entity == null)
			return null;

		for (Map.Entry<Integer, EntityGroup<?>> entry : this.entityGroups.entrySet()) {
			EntityGroup<?> group = entry.getValue();
			if (group.getType() == EntityGroup.Type.ENTITY) { // not checking for types
				List<UUID> uuids = Lists.newArrayList(((EntityGroup<UUID>) group).get());
				if (uuids.contains(uuid)) {
					if (uuids.size() == 1)
						this.removeNamePlate(entry.getKey()); // removes plate permanently as there is no other criteria to follow
					else {
						uuids.remove(uuid);
						((EntityGroup<UUID>) this.entityGroups.get(entry.getKey())).set(uuids.toArray(new UUID[0]));
					}
				}
			}
		}

		// automatically excludes the entity from its group if the entity is not found as specific
		if (autoExcludeIfNotSpecific) {
			// entity UUID was not found in the specific groups
			// try to find the entity anywhere
			NamePlate namePlate = this.getNamePlateOf(uuid, true);
			if (namePlate == null)
				return null;

			// now we know the entity is associated with a plate
			// we need to exclude it from it's entityGroup
			namePlate.getSharedGroup().setExcluded(uuid);
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	public NamePlate removeFrom(EntityType entityType) {
		for (Map.Entry<Integer, EntityGroup<?>> entry : this.entityGroups.entrySet()) {
			EntityGroup<?> group = entry.getValue();
			if (group.getType() == EntityGroup.Type.ENTITY_TYPE) { // not checking for types
				List<EntityType> entityTypes = Lists.newArrayList(((EntityGroup<EntityType>) group).get());
				if (entityTypes.contains(entityType)) {
					if (entityTypes.size() == 1)
						this.removeNamePlate(entry.getKey()); // removes plate permanently as there is no other criteria to follow
					else {
						entityTypes.remove(entityType);
						((EntityGroup<EntityType>) this.entityGroups.get(entry.getKey())).set(entityTypes.toArray(new EntityType[0]));
					}
				}
			}
		}

		return null;
	}

	public NamePlate removeFrom(BlockLocation location) {
		//TODO
		return null;
	}

	// checks

	public boolean hasNamePlate(EntityType entityType) {
		return this.getNamePlate(entityType) != null;
	}
	// checks if a specifically mentioned entity has a nameplate
	public boolean hasNamePlate(UUID uuid) {
		return this.getNamePlate(uuid) != null;
	}
	// check if entity has nameplate using getNameplateOf (which uses all possible ways to get an entity) - may cause lag
	// this can retrieve an entity's nameplate even if the entity is not specifically mentioned
	public boolean hasNamePlateUnsafe(UUID uuid, boolean forceUnloadedChunks) {
		return this.getNamePlateOf(uuid, forceUnloadedChunks) != null;
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
