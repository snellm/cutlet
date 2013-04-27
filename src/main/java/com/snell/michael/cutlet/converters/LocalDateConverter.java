package com.snell.michael.cutlet.converters;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public class LocalDateConverter implements ValueConverter<LocalDate> {
    private static final DateTimeFormatter LOCAL_DATE_PARSER = ISODateTimeFormat.localDateParser();
    private static final DateTimeFormatter DATE_FORMATTER = ISODateTimeFormat.date();

    @Override
    public LocalDate read(Object object) {
        return LOCAL_DATE_PARSER.parseLocalDate(object.toString());
    }

    @Override
    public Object write(LocalDate localDate) {
        return DATE_FORMATTER.print(localDate);
    }
}
