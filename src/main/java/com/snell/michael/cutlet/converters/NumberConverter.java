// Copyright (c) 2015 Michael Snell - see https://github.com/snellm/cutlet

package com.snell.michael.cutlet.converters;

import java.math.BigDecimal;

public abstract class NumberConverter<T> extends NullConverter<T> {
    @Override
    public final T readNotNull(Object object) {
        if (object instanceof String) {
            String s = (String) object;
            try {
                if (s.toLowerCase().contains("e")) {
                    return readString(new BigDecimal(s).toPlainString());
                } else {
                    return readString(s);
                }
            } catch (NumberFormatException e) {
                throw new RuntimeException("Cannot parse [" + object + "] into a number", e);
            }
        } else if (object instanceof Double) {
            Double d = (Double) object;
            if (d.toString().toLowerCase().contains("e")) {
                return readString(BigDecimal.valueOf(d).toPlainString());
            } else {
                return readDouble(d);
            }
        } else if (object instanceof Integer) {
            return readInteger((Integer) object);
        } else {
            throw new RuntimeException("Cannot convert class [" + object.getClass() + "] value [" + object + "] to a number");
        }
    }

    protected abstract T readString(String string);

    protected abstract T readDouble(Double dbl);

    protected abstract T readInteger(Integer integer);

    @Override
    public final Object writeNotNull(T t) {
        return t;
    }
}