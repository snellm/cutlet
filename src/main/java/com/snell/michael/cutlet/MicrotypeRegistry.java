package com.snell.michael.cutlet;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MicrotypeRegistry {
    private final Map<Class<?>, Class<?>> microtypeToValueCache = new ConcurrentHashMap<>();

    public Class<?> getMicrotypeValueClass(Class<?> microtypeClass) {
        if (microtypeToValueCache.containsKey(microtypeClass)) {
            return microtypeToValueCache.get(microtypeClass);
        } else {
            Class<?> valueClass = determineValueClass(microtypeClass);
            microtypeToValueCache.put(microtypeClass, valueClass);
            return valueClass;
        }
    }

    private Class<?> determineValueClass(Class<?> microtypeClass) {
        try {
            Method method = microtypeClass.getMethod("getValue");
            return method.getReturnType();
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    public boolean isMicrotype(Class<?> clazz) {
        return getMicrotypeValueClass(clazz) != null;
    }
}
