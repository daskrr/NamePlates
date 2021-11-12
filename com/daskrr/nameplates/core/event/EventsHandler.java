package com.daskrr.nameplates.core.event;

import com.daskrr.nameplates.api.event.NamePlateEventListener;
import com.daskrr.nameplates.core.NamePlateHandler;
import com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EventsHandler {

    private final NamePlateHandler namePlateHandler;

    private final Map<NamePlateEventListener, Plugin> registeredListeners = Maps.newHashMap();

    public EventsHandler(NamePlateHandler namePlateHandler) {
        this.namePlateHandler = namePlateHandler;
    }

    public void registerEvents(NamePlateEventListener listener, Plugin plugin) {
        this.registeredListeners.put(listener, plugin);
    }

    public void fireEvent(NamePlateEvent event) {
        this.registeredListeners.forEach((listener, plugin) -> {
            List<Method> methods = getHandlerMethods(listener.getClass());
            methods.forEach(method -> {
                try {
                    // check if the parameter of the method is "instanceof" the given event to fire
                    if (method.getParameterTypes()[0] == event.getClass())
                        method.invoke(listener, event);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            });
        });
    }

    public static List<Method> getHandlerMethods(final Class<?> type) {
        Class<com.daskrr.nameplates.api.event.NamePlateEventHandler> annotation = com.daskrr.nameplates.api.event.NamePlateEventHandler.class;
        final List<Method> methods = new ArrayList<>();
        Class<?> klass = type;
        while (klass != Object.class) { // need to iterated thought hierarchy in order to retrieve methods from above the current instance
            // iterate though the list of methods declared in the class represented by klass variable, and add those annotated with the specified annotation
            for (final Method method : klass.getDeclaredMethods()) {
                if (method.isAnnotationPresent(annotation)) {
                    Annotation annotInstance = method.getAnnotation(annotation);
                    // TODO process annotInstance
                    methods.add(method);
                }
            }
            // move to the upper class in the hierarchy in search for more methods
            klass = klass.getSuperclass();
        }
        return methods;
    }
}
