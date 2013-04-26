package com.snell.michael.cutlet;

import java.math.BigDecimal;
import java.util.List;

public interface Cutlet {
    // General methods

    String printCompact();

    String printPretty();

    void remove(String xpath);

    // Cutlet methods

    Cutlet get(String xpath);

    boolean has(String xpath);

    List<Cutlet> getArray(String xpath);

    Cutlet add(String xpath);

    Cutlet addArray(String xpath, List<Cutlet> cutlets);

    // String methods

    String getString(String xpath);

    String getOptionalString(String xpath);

    List<String> getStringArray(String xpath);

    Cutlet addString(String xpath, String value);

    // BigDecimal methods

    BigDecimal getBigDecimal(String xpath);

    List<BigDecimal> getBigDecimalArray(String xpath);

    Cutlet addBigDecimal(String xpath, BigDecimal value);
}
