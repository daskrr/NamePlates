package com.daskrr.nameplates.version;

import java.util.function.Supplier;

import com.daskrr.nameplates.version.v1_8_R1.Utils_v1_8_R1;
import com.daskrr.nameplates.version.v1_8_R1.entity.DataWatcher_v1_8_R1;
import com.daskrr.nameplates.version.v1_8_R1.entity.EntityArmorStand_v1_8_R1;
import com.daskrr.nameplates.version.v1_8_R1.entity.EntityLiving_v1_8_R1;
import com.daskrr.nameplates.version.v1_8_R1.entity.Entity_v1_8_R1;
import com.daskrr.nameplates.version.v1_8_R1.network.NetworkManager_v1_8_R1;
import com.daskrr.nameplates.version.v1_8_R1.network.protocol.PacketGameEntityDestroy_v1_8_R1;
import com.daskrr.nameplates.version.v1_8_R1.network.protocol.PacketGameEntityMetadata_v1_8_R1;
import com.daskrr.nameplates.version.v1_8_R1.network.protocol.PacketGameEntityTeleport_v1_8_R1;
import com.daskrr.nameplates.version.v1_8_R1.world.RayTraceResult_v1_8_R1;
import com.daskrr.nameplates.version.v1_8_R1.world.RayTrace_v1_8_R1;
import com.daskrr.nameplates.version.wrapped.WrappedItem;
import org.apache.commons.lang3.tuple.Pair;

import com.daskrr.nameplates.version.v1_8_R1.network.protocol.PacketGameSpawnEntityLiving_v1_8_R1;
import com.google.common.collect.ImmutableList;

public class NMSRegistry {
	
	public static final VersionRegistry v1_8_R1 = register(ImmutableList.of(
				Pair.of(WrappedItem.NETWORK_MANAGER, NetworkManager_v1_8_R1::new),
				Pair.of(WrappedItem.PACKET_GAME_SPAWN_ENTITY_LIVING, PacketGameSpawnEntityLiving_v1_8_R1::new),
				Pair.of(WrappedItem.PACKET_GAME_DESTROY_ENTITY, PacketGameEntityDestroy_v1_8_R1::new),
				Pair.of(WrappedItem.PACKET_GAME_ENTITY_METADATA, PacketGameEntityMetadata_v1_8_R1::new),
				Pair.of(WrappedItem.PACKET_GAME_ENTITY_TELEPORT, PacketGameEntityTeleport_v1_8_R1::new),

				Pair.of(WrappedItem.ENTITY, Entity_v1_8_R1::new),
				Pair.of(WrappedItem.ENTITY_LIVING, EntityLiving_v1_8_R1::new),
				Pair.of(WrappedItem.ENTITY_ARMOR_STAND, EntityArmorStand_v1_8_R1::new),

				Pair.of(WrappedItem.DATA_WATCHER, DataWatcher_v1_8_R1::new),
				Pair.of(WrappedItem.UTILS, Utils_v1_8_R1::new),

				Pair.of(WrappedItem.RAY_TRACE, RayTrace_v1_8_R1::new),
				Pair.of(WrappedItem.RAY_TRACE_RESULT, RayTraceResult_v1_8_R1::new)
			));
	
	private static VersionRegistry register(ImmutableList<Pair<WrappedItem<?>, Supplier<Object>>> classes) {
		return new VersionRegistry(classes);
	}
	
	public static class VersionRegistry {
		
		private final ImmutableList<Pair<WrappedItem<?>, Supplier<Object>>> items;
		
		protected VersionRegistry(ImmutableList<Pair<WrappedItem<?>, Supplier<Object>>> items) {
			this.items = items;
		}
		
		@SuppressWarnings("unchecked")
		public <T> T get(WrappedItem<T> item) {
			for (Pair<WrappedItem<?>, Supplier<Object>> pair : this.items) {
				if (pair.getLeft() == item)
					return (T) pair.getLeft().getInstance(pair.getRight());
			}
			
			return null;
		}
	}
}
