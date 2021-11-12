package com.daskrr.nameplates.version.wrapped;

import com.daskrr.nameplates.version.wrapped.entity.WrappedDataWatcher;
import com.daskrr.nameplates.version.wrapped.entity.WrappedEntity;
import com.daskrr.nameplates.version.wrapped.entity.WrappedEntityArmorStand;
import com.daskrr.nameplates.version.wrapped.entity.WrappedEntityLiving;
import com.daskrr.nameplates.version.wrapped.network.WrappedNetworkManager;
import com.daskrr.nameplates.version.wrapped.network.protocol.WrappedPacketGameEntityDestroy;
import com.daskrr.nameplates.version.wrapped.network.protocol.WrappedPacketGameEntityMetadata;
import com.daskrr.nameplates.version.wrapped.network.protocol.WrappedPacketGameSpawnEntityLiving;
import com.daskrr.nameplates.version.wrapped.world.WrappedRayTrace;
import com.daskrr.nameplates.version.wrapped.world.WrappedRayTraceResult;

import java.util.function.Supplier;

public class WrappedItem<T> {

    public static final WrappedItem<WrappedNetworkManager> NETWORK_MANAGER = new WrappedItem<WrappedNetworkManager>();
    public static final WrappedItem<WrappedPacketGameSpawnEntityLiving> PACKET_GAME_SPAWN_ENTITY_LIVING = new WrappedItem<WrappedPacketGameSpawnEntityLiving>();
    public static final WrappedItem<WrappedPacketGameEntityDestroy> PACKET_GAME_DESTROY_ENTITY = new WrappedItem<WrappedPacketGameEntityDestroy>();
    public static final WrappedItem<WrappedPacketGameEntityMetadata> PACKET_GAME_ENTITY_METADATA = new WrappedItem<WrappedPacketGameEntityMetadata>();

    public static final WrappedItem<WrappedEntity> ENTITY = new WrappedItem<WrappedEntity>();
    public static final WrappedItem<WrappedEntityLiving> ENTITY_LIVING = new WrappedItem<WrappedEntityLiving>();
    public static final WrappedItem<WrappedEntityArmorStand> ENTITY_ARMOR_STAND = new WrappedItem<WrappedEntityArmorStand>();

    public static final WrappedItem<WrappedDataWatcher> DATA_WATCHER = new WrappedItem<WrappedDataWatcher>();

    public static final WrappedItem<WrappedRayTrace> RAY_TRACE = new WrappedItem<WrappedRayTrace>();
    public static final WrappedItem<WrappedRayTraceResult> RAY_TRACE_RESULT = new WrappedItem<WrappedRayTraceResult>();

    private WrappedItem() {}

    @SuppressWarnings("unchecked")
    public T getInstance(Supplier<Object> supplier) {
        return (T) supplier.get();
    }
}
