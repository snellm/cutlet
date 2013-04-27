package com.snell.michael.cutlet.converters;

interface ValueConverter<T> {
    /**
     * Parse object from Cutlet source into required type
     */
    T read(Object object);

    /**
     * Write value into standard format for Cutlet destination
     */
    Object write(T t);
}
