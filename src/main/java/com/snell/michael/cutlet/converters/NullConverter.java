// Copyright (c) 2013 Michael Snell - see https://github.com/snellm/cutlet

package com.snell.michael.cutlet.converters;

/**
 * Reads and writes null as null, defers conversion of non-null values
 * @param <T> Type
 */
public abstract class NullConverter<T> implements Converter<T> {
    @Override
    public T read(Object object) {
        if (object == null) {
            return null;
        } else {
            return readNotNull(object);
        }
    }

    protected abstract T readNotNull(Object object);

    @Override
    public Object write(T t) {
        if (t == null) {
            return null;
        } else {
            return writeNotNull(t);
        }
    }

    protected abstract Object writeNotNull(T t);
}
