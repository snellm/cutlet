package com.snell.michael.cutlet;

import java.math.BigDecimal;
import java.util.List;

public interface Cutlet {
    Object getContextBean();

    Cutlet get(String xpath);

    boolean has(String xpath);

    List<Cutlet> getArray(String xpath);

    Cutlet add(String xpath);

    Cutlet addArray(String xpath, List<Cutlet> cutlets);

    String getString(String xpath);

    String getOptionalString(String xpath);

    List<String> getStringArray(String xpath);

    Cutlet addString(String xpath, String value);

    BigDecimal getBigDecimal(String xpath);

    List<BigDecimal> getBigDecimalArray(String xpath);

    Cutlet addBigDecimal(String xpath, BigDecimal value);

    void remove(String xpath);
}
