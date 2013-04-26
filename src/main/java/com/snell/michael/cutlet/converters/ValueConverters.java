package com.snell.michael.cutlet.converters;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class ValueConverters {
    private ValueConverters() {}

    private static final Map<Class<?>, ValueConverter<?>> CONVERTER_MAP = new HashMap<>();

    static {
        CONVERTER_MAP.put(BigDecimal.class, new BigDecimalConverter());
    }

    public static <T> T read(Class<T> clazz, Object object) {
        return gettValueConverter(clazz).read(object);
    }

    public static <T> Object write(Class<T> clazz, T t) {
        return gettValueConverter(clazz).write(t);
    }

    @SuppressWarnings("unchecked")
    private static <T> ValueConverter<T> gettValueConverter(Class<T> clazz) {
        return ((ValueConverter<T>) CONVERTER_MAP.get(clazz));
    }
}
