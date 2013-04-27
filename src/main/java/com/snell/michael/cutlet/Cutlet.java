package com.snell.michael.cutlet;

import java.math.BigDecimal;
import java.util.List;

interface Cutlet<CUTLET extends Cutlet<CUTLET>> {
    // General methods

    String printCompact();

    String printPretty();

    void remove(String xpath);

    // Cutlet methods

    CUTLET get(String xpath);

    boolean has(String xpath);

    List<CUTLET> getArray(String xpath);

    CUTLET add(String xpath);

    CUTLET addArray(String xpath, List<CUTLET> cutlets);

    // String methods

    String getString(String xpath);

    String getOptionalString(String xpath);

    List<String> getStringArray(String xpath);

    CUTLET addString(String xpath, String value);

    // BigDecimal methods

    BigDecimal getBigDecimal(String xpath);

    List<BigDecimal> getBigDecimalArray(String xpath);

    CUTLET addBigDecimal(String xpath, BigDecimal value);
}
