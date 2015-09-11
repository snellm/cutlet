// Copyright (c) 2013 Michael Snell - see https://github.com/snellm/cutlet

package com.snell.michael.cutlet;

import com.snell.michael.cutlet.converters.*;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;

public class ConverterMap {
    static ConverterMap DEFAULT_CONVERTER_MAP = createWithDefaults();

    private final Map<Class<?>, Converter<?>> classConverter = new HashMap<>();

    private ConverterMap() {}

    /**
     * Register a converter. Will override any existing converter for clazz
     * @param clazz Class for which converter is registered
     * @param converter Converter
     * @param <T> Type
     */
    public <T> ConverterMap register(Class<T> clazz, Converter<T> converter) {
        classConverter.put(clazz, converter);
        return this;
    }

    /**
     * Read object by converting it
     * @param object Object to read
     * @param clazz Class of value
     * @param <T> Type
     * @return Converted value
     */
    public <T> T read(Object object, Class<T> clazz) {
        return getConverter(clazz).read(object);
    }

    /**
     * Write value by converting it
     * @param t Value
     * @param clazz Class of value
     * @param <T> Type
     * @return Converted object
     */
    public <T> Object write(T t, Class<T> clazz) {
        return getConverter(clazz).write(t);
    }

    public boolean hasConverter(Class<?> clazz) {
        return classConverter.containsKey(clazz);
    }

    @SuppressWarnings("unchecked")
    private <T> Converter<T> getConverter(Class<T> clazz) {
        Converter<T> converter = (Converter<T>) classConverter.get(clazz);
        if (converter == null) {
            throw new RuntimeException("No converter for [" + clazz.getCanonicalName() + "]");
        }
        return converter;
    }

    /**
     * Create a new ConverterMap with no registered converters
     * @return New ConverterMap
     */
    public static ConverterMap create() {
        return new ConverterMap();
    }

    /**
     * Create a new ConverterMap with default converters registered
     * @return New ConverterMap
     */
    public static ConverterMap createWithDefaults() {
        ConverterMap converterMap = create();
        registerDefaultConverters(converterMap);
        return converterMap;
    }

    /**
     * Sets the default ConverterMap for all Cutlets created after this call
     * @param converterMap ConverterMap
     */
    public static void setDefaultConverterMap(ConverterMap converterMap) {
        DEFAULT_CONVERTER_MAP = converterMap;
    }

    private static void registerDefaultConverters(ConverterMap converterMap) {
        converterMap.register(String.class, new StringConverter());
        converterMap.register(Boolean.class, new BooleanConverter());

        converterMap.register(Integer.class, new IntegerConverter());
        converterMap.register(Long.class, new LongConverter());
        converterMap.register(Double.class, new DoubleConverter());
        converterMap.register(Float.class, new FloatConverter());
        converterMap.register(BigDecimal.class, new BigDecimalConverter());
        converterMap.register(BigInteger.class, new BigIntegerConverter());

        converterMap.register(LocalDate.class, new LocalDateConverter());
        converterMap.register(DateTime.class, new DateTimeConverter());

        converterMap.register(URL.class, new URLConverter());

        converterMap.register(Currency.class, new CurrencyConverter());
    }

}
