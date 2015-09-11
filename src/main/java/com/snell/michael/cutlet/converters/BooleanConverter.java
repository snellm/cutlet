// Copyright (c) 2015 Michael Snell - see https://github.com/snellm/cutlet

package com.snell.michael.cutlet.converters;

public class BooleanConverter extends NullConverter<Boolean> {
    @Override
    public Boolean readNotNull(Object object) {
        String str = object.toString().toLowerCase().trim();
        return ("true".equals(str) || "1".equals(str) || "yes".equals(str));
    }

    @Override
    public Object writeNotNull(Boolean bool) {
        return bool.toString().toLowerCase();
    }
}
