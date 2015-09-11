// Copyright (c) 2015 Michael Snell - see https://github.com/snellm/cutlet

package com.snell.michael.cutlet.converters;

import java.util.Currency;

public class CurrencyConverter extends NullConverter<Currency> {
    @Override
    protected Currency readNotNull(Object object) {
        return Currency.getInstance(object.toString());
    }

    @Override
    protected Object writeNotNull(Currency currency) {
        return currency.getCurrencyCode();
    }
}
