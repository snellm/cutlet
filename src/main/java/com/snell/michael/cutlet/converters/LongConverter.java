// Copyright (c) 2015 Michael Snell - see https://github.com/snellm/cutlet

package com.snell.michael.cutlet.converters;

import java.math.BigDecimal;

public class LongConverter extends NumberConverter<Long> {
    @Override
    protected Long readString(String string) {
        return Long.valueOf(string);
    }

    @Override
    protected Long readDouble(Double dbl) {
        return new BigDecimal(dbl).longValueExact();
    }

    @Override
    protected Long readInteger(Integer integer) {
        return integer.longValue();
    }
}