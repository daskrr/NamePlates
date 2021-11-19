package com.daskrr.nameplates.version;

import java.util.Map;
import java.util.function.Supplier;

import com.daskrr.nameplates.version.v1_8_R1.Utils_v1_8_R1;
import com.daskrr.nameplates.version.v1_8_R1.bukkit.CompatibilityEntityType_v1_8_R1;
import com.daskrr.nameplates.version.v1_8_R1.bukkit.event.EventListener_v1_8_R1;
import com.daskrr.nameplates.version.v1_8_R1.entity.*;
import com.daskrr.nameplates.version.v1_8_R1.network.NetworkManager_v1_8_R1;
import com.daskrr.nameplates.version.v1_8_R1.network.protocol.*;
import com.daskrr.nameplates.version.v1_8_R1.world.RayTraceResult_v1_8_R1;
import com.daskrr.nameplates.version.v1_8_R1.world.RayTrace_v1_8_R1;
import com.daskrr.nameplates.version.wrapped.WrappedItem;
import com.daskrr.nameplates.version.wrapped.bukkit.event.ICompatibilityListener;
import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.ImmutableList;
import org.bukkit.entity.EntityType;

public class NMSRegistry {
	
	public static final VersionRegistry v1_8_R1 = register(ImmutableList.of(
				Pair.of(WrappedItem.NETWORK_MANAGER, NetworkManager_v1_8_R1::new),
				Pair.of(WrappedItem.PACKET_GAME_SPAWN_ENTITY, PacketGameSpawnEntity_v1_8_R1::new),
				Pair.of(WrappedItem.PACKET_GAME_SPAWN_ENTITY_LIVING, PacketGameSpawnEntityLiving_v1_8_R1::new),
				Pair.of(WrappedItem.PACKET_GAME_DESTROY_ENTITY, PacketGameEntityDestroy_v1_8_R1::new),
				Pair.of(WrappedItem.PACKET_GAME_ENTITY_METADATA, PacketGameEntityMetadata_v1_8_R1::new),
				Pair.of(WrappedItem.PACKET_GAME_ENTITY_TELEPORT, PacketGameEntityTeleport_v1_8_R1::new),
				Pair.of(WrappedItem.PACKET_GAME_MOUNT_ENTITY, PacketGameMountEntity_v1_8_R1::new),

				Pair.of(WrappedItem.ENTITY_REGISTRY, EntityRegistry_v1_8_R1::new),
				Pair.of(WrappedItem.ENTITY, Entity_v1_8_R1::new),
				Pair.of(WrappedItem.ENTITY_LIVING, EntityLiving_v1_8_R1::new),
				Pair.of(WrappedItem.ENTITY_ARMOR_STAND, EntityArmorStand_v1_8_R1::new),
				Pair.of(WrappedItem.ENTITY_ITEM, EntityItem_v1_8_R1::new),

				Pair.of(WrappedItem.DATA_WATCHER, DataWatcher_v1_8_R1::new),
				Pair.of(WrappedItem.UTILS, Utils_v1_8_R1::new),

				Pair.of(WrappedItem.RAY_TRACE, RayTrace_v1_8_R1::new),
				Pair.of(WrappedItem.RAY_TRACE_RESULT, RayTraceResult_v1_8_R1::new),

				Pair.of(WrappedItem.COMPATIBILITY_LISTENER, EventListener_v1_8_R1::new)
			), CompatibilityEntityType_v1_8_R1.entityTypes);
	
	private static VersionRegistry register(ImmutableList<Pair<WrappedItem<?>, Supplier<Object>>> classes, Map<String, EntityType> entityTypes) {
		return new VersionRegistry(classes, entityTypes);
	}
	
	public static class VersionRegistry {
		
		private final ImmutableList<Pair<WrappedItem<?>, Supplier<Object>>> items;
		private final Map<String, EntityType> entityTypes;
		
		protected VersionRegistry(ImmutableList<Pair<WrappedItem<?>, Supplier<Object>>> items, Map<String, EntityType> entityTypes) {
			this.items = items;
			this.entityTypes = entityTypes;
		}
		
		@SuppressWarnings("unchecked")
		public <T> T get(WrappedItem<T> item) {
			for (Pair<WrappedItem<?>, Supplier<Object>> pair : this.items) {
				if (pair.getLeft() == item)
					return (T) pair.getLeft().getInstance(pair.getRight());
			}
			
			return null;
		}

		public EntityType getEntityType(String id) {
			return entityTypes.get(id) == null ? EntityType.UNKNOWN : entityTypes.get(id);
		}
	}
}
