package com.snell.michael.cutlet.converters;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class ValueConverters {
    private static final Map<Class<?>, ValueConverter<?>> CONVERTERS = new HashMap<>();

    private ValueConverters() {}

    static {
        CONVERTERS.put(BigDecimal.class, new BigDecimalConverter());
        CONVERTERS.put(BigInteger.class, new BigIntegerConverter());
    }

    public static <T> T read(Class<T> clazz, Object object) {
        return getValueConverter(clazz).read(object);
    }

    public static <T> Object write(Class<T> clazz, T t) {
        return getValueConverter(clazz).write(t);
    }

    @SuppressWarnings("unchecked")
    private static <T> ValueConverter<T> getValueConverter(Class<T> clazz) {
        ValueConverter<T> valueConverter = (ValueConverter<T>) CONVERTERS.get(clazz);
        if (valueConverter == null) {
            throw new RuntimeException("No converter for [" + clazz.getCanonicalName() + "]");
        }
        return valueConverter;
    }
}
