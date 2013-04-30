// Copyright (c) 2013 Michael Snell - see https://github.com/snellm/cutlet

package com.snell.michael.cutlet.converters;

import java.math.BigDecimal;
import java.math.BigInteger;

public class BigIntegerConverter extends NumberConverter<BigInteger> {
    @Override
    protected BigInteger readString(String string) {
        return new BigInteger(string);
    }

    @Override
    protected BigInteger readDouble(Double dbl) {
        return BigInteger.valueOf(new BigDecimal(dbl).longValueExact());
    }

    @Override
    protected BigInteger readInteger(Integer integer) {
        return BigInteger.valueOf(integer);
    }
}