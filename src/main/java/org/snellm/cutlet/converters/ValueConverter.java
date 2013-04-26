package org.snellm.cutlet.converters;

public interface ValueConverter<T> {
    /**
     * Parse object from Cutlet into required type
     */
    T read(Object object);

    /**
     * Write value into standard format for Cutlet
     */
    Object write(T t);
}
