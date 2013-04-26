package com.snell.michael.cutlet.converters;

import java.math.BigDecimal;

public class BigDecimalConverter implements ValueConverter<BigDecimal> {
    @Override
    public BigDecimal read(Object object) {
        if (object instanceof String) {
            String s = (String) object;
            try {
                if (s.toLowerCase().contains("e")) {
                    return read(new BigDecimal(s).toPlainString());
                } else {
                    return new BigDecimal(s);
                }
            } catch (NumberFormatException e) {
                throw new RuntimeException("Cannot parse [" + object + "] into BigDecimal", e);
            }
        } else if (object instanceof Double) {
            Double d = (Double) object;
            if (d.toString().toLowerCase().contains("e")) {
                return read(BigDecimal.valueOf(d).toPlainString());
            } else {
                return BigDecimal.valueOf(d);
            }
        } else if (object instanceof Integer) {
            return BigDecimal.valueOf((Integer) object);
        } else {
            throw new RuntimeException("Cannot convert class [" + object.getClass() + "] value [" + object + "] to BigDecimal");
        }
    }

    @Override
    public Object write(BigDecimal bigDecimal) {
        return bigDecimal;
    }
}