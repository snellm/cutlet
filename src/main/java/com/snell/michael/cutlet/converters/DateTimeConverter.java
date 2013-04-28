// Copyright (c) 2013 Michael Snell - see https://github.com/snellm/cutlet

package com.snell.michael.cutlet.converters;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public class DateTimeConverter extends NullConverter<DateTime> {
    private static final DateTimeFormatter DATE_TIME_PARSER = ISODateTimeFormat.dateTimeParser().withOffsetParsed();
    private static final DateTimeFormatter DATE_TIME_FORMATTER = ISODateTimeFormat.dateTime();

    @Override
    public DateTime readNotNull(Object object) {
        return DATE_TIME_PARSER.parseDateTime(object.toString());
    }

    @Override
    public Object writeNotNull(DateTime dateTime) {
        return DATE_TIME_FORMATTER.print(dateTime);
    }
}
