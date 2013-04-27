package com.snell.michael.cutlet.converters;

public class StringConverter implements ValueConverter<String> {
    @Override
    public String read(Object object) {
        if (object instanceof String) {
            return (String) object;
        } else {
            return object.toString();
        }
    }

    @Override
    public Object write(String string) {
        return string;
    }
}
