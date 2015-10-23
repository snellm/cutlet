// Copyright (c) 2015 Michael Snell - see https://github.com/snellm/cutlet

package com.snell.michael.cutlet;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.io.File;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Currency;
import java.util.List;
import java.util.Set;

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
    List<C> getList(String xpath);

    /**
     * Get the names of all child nodes of this Cutlet
     * @return Set of child node names
     */
    Set<String> getChildren();

    /**
     * Tests for the existence of the given xpath
     * @param xpath XPath
     * @return True if there is at least one element matching the xpath, false otherwise
     */
    boolean exists(String xpath);

    /**
     * Tests for the existence of the given xpath with a non-blank value
     * @param xpath XPath
     * @return True if there is at least one element matching the xpath with a non-blank value, false otherwise
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
    C withList(String xpath, List<C> cutlets);

    // Value methods

    /**
     * Gets the value at the given xpath, converting into the given class
     * @param xpath XPath
     * @param clazz Class to return
     * @return Value existing at the given path converted into clazz
     */
    <T> T get(String xpath, Class<T> clazz);

    /**
     * Gets the arrays of values matching the given xpath, converted into the given class, as a list
     * @param xpath XPath
     * @param clazz Class to return
     * @return List of values existing at the given path converted into clazz
     */
    <T> List<T> getList(String xpath, Class<T> clazz);

    /**
     * Gets the arrays of values matching the given xpath, converted into the given class, as a set
     * @param xpath XPath
     * @param clazz Class to return
     * @return List of values existing at the given path converted into clazz
     */
    <T> Set<T> getSet(String xpath, Class<T> clazz);


    /**
     * Adds a value at the given xpath
     * @param xpath XPath
     * @param value Value to set
     * @return Current Cutlet (to allow fluent style)
     */
    <T> C with(String xpath, T value);

    /**
     * Add an array of values at the given xpath, converting from the given class
     * @param xpath XPath
     * @param values Collection of values to set
     * @param clazz Class to convert from
     * @return Current Cutlet (to allow fluent style)
     */
    <T> C withList(String xpath, Collection<T> values, Class<T> clazz);

    // String methods

    String getString(String xpath);
    List<String> getStringList(String xpath);
    Set<String> getStringSet(String xpath);
    C withString(String xpath, String value);

    // Boolean methods

    Boolean getBoolean(String xpath);
    List<Boolean> getBooleanList(String xpath);
    C withBoolean(String xpath, Boolean value);

    // Integer methods

    Integer getInteger(String xpath);
    List<Integer> getIntegerList(String xpath);
    C withInteger(String xpath, Integer value);

    // Long methods

    Long getLong(String xpath);
    List<Long> getLongList(String xpath);
    C withLong(String xpath, Long value);

    // Double methods

    Double getDouble(String xpath);
    List<Double> getDoubleList(String xpath);
    C withDouble(String xpath, Double value);

    // Float methods

    Float getFloat(String xpath);
    List<Float> getFloatList(String xpath);
    C withFloat(String xpath, Float value);

    // BigDecimal methods

    BigDecimal getBigDecimal(String xpath);
    List<BigDecimal> getBigDecimalList(String xpath);
    C withBigDecimal(String xpath, BigDecimal value);

    // BigInteger methods

    BigInteger getBigInteger(String xpath);
    List<BigInteger> getBigIntegerList(String xpath);
    C withBigInteger(String xpath, BigInteger value);

    // LocalDate methods

    LocalDate getLocalDate(String xpath);
    List<LocalDate> getLocalDateList(String xpath);
    C withLocalDate(String xpath, LocalDate value);

    // DateTime methods

    DateTime getDateTime(String xpath);
    List<DateTime> getDateTimeList(String xpath);
    C withDateTime(String xpath, DateTime value);

    // Currency methods

    Currency getCurrency(String xpath);
    List<Currency> getCurrencyList(String xpath);
    C withCurrency(String xpath, Currency value);
}
