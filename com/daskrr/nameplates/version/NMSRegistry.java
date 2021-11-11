package com.daskrr.nameplates.version;

import java.util.function.Supplier;

import com.daskrr.nameplates.version.wrapped.network.protocol.WrappedPacketGameSpawnEntityLiving;
import org.apache.commons.lang3.tuple.Pair;

import com.daskrr.nameplates.version.v1_8_R1.network.protocol.PacketGameSpawnEntityLiving_v1_8_R1;
import com.google.common.collect.ImmutableList;

public class NMSRegistry {
	
	public static final VersionRegistry v1_8_R1 = register(ImmutableList.of(
				Pair.of(WrappedItem.PACKET_GAME_ENTITYSPAWN, PacketGameSpawnEntityLiving_v1_8_R1::new)
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
	
	public static class WrappedItem<T> {
		
		public static final WrappedItem<WrappedPacketGameSpawnEntityLiving> PACKET_GAME_ENTITYSPAWN = new WrappedItem<WrappedPacketGameSpawnEntityLiving>();
		
		private WrappedItem() {}
		
		@SuppressWarnings("unchecked")
		protected T getInstance(Supplier<Object> supplier) {
			return (T) supplier.get();
		}
	}
}
