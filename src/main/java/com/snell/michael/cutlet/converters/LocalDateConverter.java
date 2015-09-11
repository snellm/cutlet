// Copyright (c) 2015 Michael Snell - see https://github.com/snellm/cutlet

package com.snell.michael.cutlet.converters;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public class LocalDateConverter extends NullConverter<LocalDate> {
    private static final DateTimeFormatter LOCAL_DATE_PARSER = ISODateTimeFormat.localDateParser();
    private static final DateTimeFormatter DATE_FORMATTER = ISODateTimeFormat.date();

    @Override
    public LocalDate readNotNull(Object object) {
        return LOCAL_DATE_PARSER.parseLocalDate(object.toString());
    }

    @Override
    public Object writeNotNull(LocalDate localDate) {
        return DATE_FORMATTER.print(localDate);
    }
}
