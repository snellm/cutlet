// Copyright (c) 2015 Michael Snell - see https://github.com/snellm/cutlet

package com.snell.michael.cutlet.converters;

public class DoubleConverter extends NumberConverter<Double> {
    @Override
    protected Double readString(String string) {
        return Double.valueOf(string);
    }

    @Override
    protected Double readDouble(Double dbl) {
        return dbl;
    }

    @Override
    protected Double readInteger(Integer integer) {
        return integer.doubleValue();
    }
}