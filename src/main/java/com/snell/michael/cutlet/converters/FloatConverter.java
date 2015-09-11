// Copyright (c) 2015 Michael Snell - see https://github.com/snellm/cutlet

package com.snell.michael.cutlet.converters;

public class FloatConverter extends NumberConverter<Float> {
    @Override
    protected Float readString(String string) {
        return Float.valueOf(string);
    }

    @Override
    protected Float readDouble(Double dbl) {
        return dbl.floatValue();
    }

    @Override
    protected Float readInteger(Integer integer) {
        return integer.floatValue();
    }
}