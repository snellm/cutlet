package com.snell.michael.cutlet;

import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

interface Cutlet<C extends Cutlet<C>> {
    // General methods

    /**
     * Print the cutlet using compact style, ie minimal newlines, whitespace and indentation
     * @return Compact string representation
     */
    String compactPrint();

    /**
     * Print the cutlet using "pretty-print" style, ie newlines and indentation for human readability
     * @return "Pretty-printed" string representation
     */
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

    // LocalDate methods

    LocalDate getLocalDate(String xpath);

    List<LocalDate> getLocalDateArray(String xpath);

    C addLocalDate(String xpath, LocalDate value);

    // BigDecimal methods

    BigDecimal getBigDecimal(String xpath);

    List<BigDecimal> getBigDecimalArray(String xpath);

    C addBigDecimal(String xpath, BigDecimal value);

    // BigInteger methods

    BigInteger getBigInteger(String xpath);

    List<BigInteger> getBigIntegerArray(String xpath);

    C addBigInteger(String xpath, BigInteger value);
}
