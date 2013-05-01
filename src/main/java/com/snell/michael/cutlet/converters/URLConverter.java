package com.snell.michael.cutlet.converters;

import java.net.MalformedURLException;
import java.net.URL;

public class URLConverter extends NullConverter<URL> {
    @Override
    protected URL readNotNull(Object object) {
        try {
            return new URL(object.toString());
        } catch (MalformedURLException e) {
            throw new RuntimeException("Could not parse [" + object + "] into URL", e);
        }
    }

    @Override
    protected Object writeNotNull(URL url) {
        return url.toExternalForm();
    }
}
