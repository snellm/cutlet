// Copyright (c) 2013 Michael Snell - see https://github.com/snellm/cutlet

package com.snell.michael.cutlet.converters;

import java.math.BigDecimal;
import java.math.BigInteger;

class BigIntegerConverter implements ValueConverter<BigInteger> {
    @Override
    public BigInteger read(Object object) {
        if (object instanceof String) {
            String s = (String) object;
            try {
                if (s.toLowerCase().contains("e")) {
                    return read(new BigDecimal(s).toPlainString());
                } else {
                    return new BigInteger(s);
                }
            } catch (NumberFormatException e) {
                throw new RuntimeException("Cannot parse [" + object + "] into BigInteger", e);
            }
        } else if (object instanceof Double) {
            Double d = (Double) object;
            if (d.toString().toLowerCase().contains("e")) {
                return read(BigDecimal.valueOf(d).toPlainString());
            } else {
                return BigInteger.valueOf(new BigDecimal(d).longValueExact());
            }
        } else if (object instanceof Integer) {
            return BigInteger.valueOf((Integer) object);
        } else {
            throw new RuntimeException("Cannot convert class [" + object.getClass() + "] value [" + object + "] to BigInteger");
        }
    }

    @Override
    public Object write(BigInteger bigInteger) {
        return bigInteger;
    }
}