package com.daskrr.nameplates.version.v1_8_R1.entity;

import com.daskrr.nameplates.version.v1_8_R1.entity.custom.MonitoredEntityItem_v1_8_R1;
import com.daskrr.nameplates.version.wrapped.entity.IEntityRegistry;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.server.v1_8_R1.Entity;
import net.minecraft.server.v1_8_R1.EntityTypes;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;

public class EntityRegistry_v1_8_R1 implements IEntityRegistry {

    // c, d
    private static final Map<String, Class<? extends Entity>> nameClass = Maps.newHashMap();
    // e, f
    private static final Map<Class<? extends Entity>, Integer> classId = Maps.newHashMap();

    public EntityRegistry_v1_8_R1() {  }

    @Override
    public void addEntities() {
        this.addEntity("Item", 1, MonitoredEntityItem_v1_8_R1.class);
    }

    private void addEntity(String name, int id, Class<? extends Entity> entityClass) {
        nameClass.put(name, entityClass);
        classId.put(entityClass, id);
    }

    // to be called at plugin load
    @Override
    @SuppressWarnings({"unchecked"})
    public void register() {
        try {
            List<Map<?,?>> fieldMaps = Lists.newArrayList();
            for (Field field : EntityTypes.class.getDeclaredFields()) {
                if (!field.getType().getSimpleName().equals(Map.class.getSimpleName())) continue;

                field.setAccessible(true);

                fieldMaps.add((Map<?,?>) field.get(null));
            }

            for (Map.Entry<String, Class<? extends Entity>> entry : nameClass.entrySet()) {

                String name = entry.getKey();
                Class<? extends Entity> clazz = entry.getValue();
                int numericId = classId.get(entry.getValue());

                // c, d
                ((Map<String, Class<?>>) fieldMaps.get(0)).put(name, clazz);
                ((Map<Class<?>, String>) fieldMaps.get(1)).put(clazz, name);

                // e, f
                ((Map<Integer, Class<?>>) fieldMaps.get(2)).put(numericId, clazz);
                ((Map<Class<?>, Integer>) fieldMaps.get(3)).put(clazz, numericId);

                // g
                ((Map<String, Integer>) fieldMaps.get(3)).put(name, numericId);
            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
