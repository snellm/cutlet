package com.snell.michael.cutlet;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.io.File;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

interface Cutlet<C extends Cutlet<C>> {
    // Write methods

    /**
     * Write to a String
     */
    String write(WriteStyle style);

    /**
     * Write to a StringBuffer
     */
    StringBuffer write(StringBuffer stringBuffer, WriteStyle style);

    /**
     * Write to an OutputStream
     */
    void write(OutputStream outputStream, WriteStyle style);

    /**
     * Write to a File
     */
    void write(File file, WriteStyle style);

    // Cutlet methods

    C get(String xpath);

    List<C> getArray(String xpath);

    boolean has(String xpath);

    void remove(String xpath);

    C add(String xpath);

    C addArray(String xpath, List<C> cutlets);

    // String methods

    String getString(String xpath);

    List<String> getStringArray(String xpath);

    C addString(String xpath, String value);

    // LocalDate methods

    LocalDate getLocalDate(String xpath);

    List<LocalDate> getLocalDateArray(String xpath);

    C addLocalDate(String xpath, LocalDate value);

    // DateTime methods

    DateTime getDateTime(String xpath);

    List<DateTime> getDateTimeArray(String xpath);

    C addDateTime(String xpath, DateTime value);

    // BigDecimal methods

    BigDecimal getBigDecimal(String xpath);

    List<BigDecimal> getBigDecimalArray(String xpath);

    C addBigDecimal(String xpath, BigDecimal value);

    // BigInteger methods

    BigInteger getBigInteger(String xpath);

    List<BigInteger> getBigIntegerArray(String xpath);

    C addBigInteger(String xpath, BigInteger value);
}
