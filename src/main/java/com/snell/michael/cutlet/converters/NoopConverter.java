package com.snell.michael.cutlet.converters;

public class NoopConverter implements ValueConverter<Object> {
    @Override
    public Object read(Object object) {
        return object;
    }

    @Override
    public Object write(Object o) {
        return o;
    }
}
