// Copyright (c) 2015 Michael Snell - see https://github.com/snellm/cutlet

package com.snell.michael.cutlet.converters;

public class StringConverter extends NullConverter<String> {
    @Override
    public String readNotNull(Object object) {
        if (object instanceof String) {
            return (String) object;
        } else {
            return object.toString();
        }
    }

    @Override
    public Object writeNotNull(String string) {
        return string;
    }
}
