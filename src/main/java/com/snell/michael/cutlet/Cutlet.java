// Copyright (c) 2013 Michael Snell - see https://github.com/snellm/cutlet

package com.snell.michael.cutlet;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.io.File;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

interface Cutlet<C extends Cutlet<C>> {
    /**
     * Set the ConverterMap used for this Cutlet
     * @param converterMap ConverterMap to use
     * @return The current Cutlet (to allow fluent style)
     */
    C withConverterMap(ConverterMap converterMap);

    // Write methods

    /**
     * Write to a String
     * @param style Writing style
     * @return String representation of Cutlet formatted according to style
     */
    String write(WriteStyle style);

    /**
     * Write to a StringBuffer
     * @param stringBuffer The StringBuffer to be written to
     * @param style Writing style
     * @return The supplied StringBuffer parameter (to allow fluent style)
     */
    StringBuffer write(StringBuffer stringBuffer, WriteStyle style);

    /**
     * Write to a OutputStream
     * @param outputStream The OutputStream to be written to
     * @param style Writing style
     */
    void write(OutputStream outputStream, WriteStyle style);

    /**
     * Write to a File, creating it if it does not exist
     * @param file The File to be written to
     * @param style Writing style
     */
    void write(File file, WriteStyle style);

    // Cutlet methods

    /**
     * Get the Cutlet existing at the given xpath
     * @param xpath XPath
     * @return Cutlet at path
     */
    C get(String xpath);

    /**
     * Get the array of Cutlets matching the given xpath
     * @param xpath XPath
     * @return List of Cutlets matching the xpath
     */
    List<C> getArray(String xpath);

    /**
     * Tests fot the existence of the given xpath
     * @param xpath XPath
     * @return True if there is at least one element matching the xpath, false otherwise
     */
    boolean has(String xpath);

    /**
     * Removes all elements matching the given xpath
     * @param xpath XPath
     */
    void remove(String xpath);

    /**
     * Creates a new Cutlet at the given xpath
     * @param xpath XPath
     * @return Created Cutlet
     */
    C add(String xpath);

    /**
     * Adds the given array of Cutlets at the xpath
     * @param xpath XPath
     * @param cutlets List of Cutlets to be added
     * @return The current Cutlet (to allow fluent style)
     */
    C addArray(String xpath, List<C> cutlets);

    // String methods

    /**
     * Gets the String at the given xpath
     * @param xpath XPath
     * @return The String value existing at the given path
     */
    String getString(String xpath);

    /**
     * Gets the arrays of Strings matching the given xpath
     * @param xpath XPath
     * @return List of Strings matching the given xpath
     */
    List<String> getStringArray(String xpath);

    /**
     * Adds a String at the given xpath
     * @param xpath XPath
     * @param value String to add
     * @return Current Cutlet (to allow fluent style)
     */
    C addString(String xpath, String value);

    // Boolean methods

    /**
     * Gets the Boolean at the given xpath
     * @param xpath XPath
     * @return The String value existing at the given path
     */
    Boolean getBoolean(String xpath);

    /**
     * Gets the arrays of Strings matching the given xpath
     * @param xpath XPath
     * @return List of Strings matching the given xpath
     */
    List<Boolean> getBooleanArray(String xpath);

    /**
     * Adds a Boolean at the given xpath
     * @param xpath XPath
     * @param value String to add
     * @return Current Cutlet (to allow fluent style)
     */
    C addBoolean(String xpath, Boolean value);

    // LocalDate methods

    /**
     * Gets the LocalDate at the given xpath
     * @param xpath XPath
     * @return The LocalDate value existing at the given path
     */
    LocalDate getLocalDate(String xpath);

    /**
     * Gets the arrays of LocalDates matching the given xpath
     * @param xpath XPath
     * @return List of LocalDates matching the given xpath
     */
    List<LocalDate> getLocalDateArray(String xpath);

    /**
     * Adds a LocalDate at the given xpath
     * @param xpath XPath
     * @param value LocalDate to add
     * @return Current Cutlet (to allow fluent style)
     */
    C addLocalDate(String xpath, LocalDate value);

    // DateTime methods

    /**
     * Gets the DateTime at the given xpath
     * @param xpath XPath
     * @return The DateTime value existing at the given path
     */
    DateTime getDateTime(String xpath);

    /**
     * Gets the arrays of DateTimes matching the given xpath
     * @param xpath XPath
     * @return List of DateTimes matching the given xpath
     */
    List<DateTime> getDateTimeArray(String xpath);

    /**
     * Adds a DateTime at the given xpath
     * @param xpath XPath
     * @param value DateTime to add
     * @return Current Cutlet (to allow fluent style)
     */
    C addDateTime(String xpath, DateTime value);

    // BigDecimal methods

    /**
     * Gets the BigDecimal at the given xpath
     * @param xpath XPath
     * @return The BigDecimal value existing at the given path
     */
    BigDecimal getBigDecimal(String xpath);

    /**
     * Gets the arrays of BigDecimals matching the given xpath
     * @param xpath XPath
     * @return List of BigDecimals matching the given xpath
     */
    List<BigDecimal> getBigDecimalArray(String xpath);

    /**
     * Adds a BigDecimal at the given xpath
     * @param xpath XPath
     * @param value BigDecimal to add
     * @return Current Cutlet (to allow fluent style)
     */
    C addBigDecimal(String xpath, BigDecimal value);

    // BigInteger methods

    /**
     * Gets the BigInteger at the given xpath
     * @param xpath XPath
     * @return The BigInteger value existing at the given path
     */
    BigInteger getBigInteger(String xpath);

    /**
     * Gets the arrays of BigIntegers matching the given xpath
     * @param xpath XPath
     * @return List of BigIntegers matching the given xpath
     */
    List<BigInteger> getBigIntegerArray(String xpath);

    /**
     * Adds a BigInteger at the given xpath
     * @param xpath XPath
     * @param value BigInteger to add
     * @return Current Cutlet (to allow fluent style)
     */
    C addBigInteger(String xpath, BigInteger value);
}
