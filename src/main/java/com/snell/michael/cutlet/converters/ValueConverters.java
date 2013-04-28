// Copyright (c) 2013 Michael Snell - see https://github.com/snellm/cutlet

package com.snell.michael.cutlet.converters;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class ValueConverters {
    private static final Map<Class<?>, ValueConverter<?>> CONVERTERS = new HashMap<>();

    private ValueConverters() {}

    static {
        CONVERTERS.put(String.class, new StringConverter());
        CONVERTERS.put(Boolean.class, new BooleanConverter());

        CONVERTERS.put(BigDecimal.class, new BigDecimalConverter());
        CONVERTERS.put(BigInteger.class, new BigIntegerConverter());

        CONVERTERS.put(LocalDate.class, new LocalDateConverter());
        CONVERTERS.put(DateTime.class, new DateTimeConverter());
    }

    public static <T> T read(Object object, Class<T> clazz) {
        if (object == null) {
            return null;
        } else {
            return getValueConverter(clazz).read(object);
        }
    }

    public static <T> Object write(T t, Class<T> clazz) {
        if (t == null) {
            return null;
        } else {
            return getValueConverter(clazz).write(t);
        }
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
