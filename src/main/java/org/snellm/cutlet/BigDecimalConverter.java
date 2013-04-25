package org.snellm.cutlet;

import java.math.BigDecimal;

public class BigDecimalConverter {
    private BigDecimalConverter() {
    }

    static BigDecimal parse(Object o) {
        if (o instanceof String) {
            String s = (String) o;
            try {
                if (s.toLowerCase().contains("e")) {
                    return parse(new BigDecimal(s).toPlainString());
                } else {
                    return new BigDecimal(s);
                }
            } catch (NumberFormatException e) {
                throw new RuntimeException("Cannot parse [" + o + "] into BigDecimal", e);
            }
        } else if (o instanceof Double) {
            Double d = (Double) o;
            if (d.toString().toLowerCase().contains("e")) {
                return parse(BigDecimal.valueOf(d).toPlainString());
            } else {
                return BigDecimal.valueOf(d);
            }
        } else if (o instanceof Integer) {
            return BigDecimal.valueOf((Integer) o);
        } else {
            throw new RuntimeException("Cannot convert class [" + o.getClass() + "] value [" + o + "] to BigDecimal");
        }
    }
}