package com.snell.michael.cutlet;

import java.math.BigDecimal;
import java.util.List;

interface Cutlet<C extends Cutlet<C>> {
    // General methods

    String compactPrint();

    String prettyPrint();

    void remove(String xpath);

    // Cutlet methods

    C get(String xpath);

    boolean has(String xpath);

    List<C> getArray(String xpath);

    C add(String xpath);

    C addArray(String xpath, List<C> cutlets);

    // String methods

    String getString(String xpath);

    String getOptionalString(String xpath);

    List<String> getStringArray(String xpath);

    C addString(String xpath, String value);

    // BigDecimal methods

    BigDecimal getBigDecimal(String xpath);

    List<BigDecimal> getBigDecimalArray(String xpath);

    C addBigDecimal(String xpath, BigDecimal value);
}
