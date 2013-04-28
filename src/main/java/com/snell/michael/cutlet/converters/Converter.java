// Copyright (c) 2013 Michael Snell - see https://github.com/snellm/cutlet

package com.snell.michael.cutlet.converters;

public interface Converter<T> {
    /**
     * Parse object from Cutlet source (ie XML or JSON) into required type
     * @param object Object - will not be null
     * @return Converted value
     */
    T read(Object object);

    /**
     * Write value into standard format for Cutlet destination (ie XML or JSON)
     * @param t Value - will not be null
     * @return Converted object
     */
    Object write(T t);
}
