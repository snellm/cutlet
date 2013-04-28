package com.snell.michael.cutlet.converters;

public class BooleanConverter implements ValueConverter<Boolean> {
    @Override
    public Boolean read(Object object) {
        String str = object.toString().toLowerCase().trim();
        return ("true".equals(str) || "1".equals(str) || "yes".equals(str));
    }

    @Override
    public Object write(Boolean bool) {
        return bool.toString().toLowerCase();
    }
}
