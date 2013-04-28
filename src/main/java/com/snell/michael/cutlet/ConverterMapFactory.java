// Copyright (c) 2013 Michael Snell - see https://github.com/snellm/cutlet

package com.snell.michael.cutlet;

import com.snell.michael.cutlet.converters.*;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.math.BigInteger;

public class ConverterMapFactory {
    static final ConverterMap DEFAULT_CONVERTER_MAP = createDefault();

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
    public static ConverterMap createDefault() {
        ConverterMap converterMap = create();
        registerDefaultConverters(converterMap);
        return converterMap;
    }

    private static void registerDefaultConverters(ConverterMap converterMap) {
        converterMap.register(String.class, new StringConverter());
        converterMap.register(Boolean.class, new BooleanConverter());

        converterMap.register(BigDecimal.class, new BigDecimalConverter());
        converterMap.register(BigInteger.class, new BigIntegerConverter());

        converterMap.register(LocalDate.class, new LocalDateConverter());
        converterMap.register(DateTime.class, new DateTimeConverter());
    }
}
