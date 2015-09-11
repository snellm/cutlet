// Copyright (c) 2015 Michael Snell - see https://github.com/snellm/cutlet

package com.snell.michael.cutlet.converters;

import java.math.BigDecimal;

public class IntegerConverter extends NumberConverter<Integer> {
    @Override
    protected Integer readString(String string) {
        return Integer.valueOf(string);
    }

    @Override
    protected Integer readDouble(Double dbl) {
        return new BigDecimal(dbl).intValueExact();
    }

    @Override
    protected Integer readInteger(Integer integer) {
        return integer;
    }
}