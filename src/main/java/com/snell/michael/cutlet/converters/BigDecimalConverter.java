// Copyright (c) 2015 Michael Snell - see https://github.com/snellm/cutlet

package com.snell.michael.cutlet.converters;

import java.math.BigDecimal;

public class BigDecimalConverter extends NumberConverter<BigDecimal> {
    @Override
    protected BigDecimal readString(String string) {
        return new BigDecimal(string);
    }

    @Override
    protected BigDecimal readDouble(Double dbl) {
        return BigDecimal.valueOf(dbl);
    }

    @Override
    protected BigDecimal readInteger(Integer integer) {
        return BigDecimal.valueOf(integer);
    }
}