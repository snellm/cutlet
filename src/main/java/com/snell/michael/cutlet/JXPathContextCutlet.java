// Copyright (c) 2013 Michael Snell - see https://github.com/snellm/cutlet

package com.snell.michael.cutlet;

import org.apache.commons.io.FileUtils;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.JXPathNotFoundException;
import org.apache.commons.jxpath.Pointer;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static com.snell.michael.cutlet.WriteStyle.PRETTY;
import static org.apache.commons.lang.StringUtils.isBlank;

abstract class JXPathContextCutlet<J extends JXPathContextCutlet<J>> implements Cutlet<J> {
    protected final JXPathContext context;

    private ConverterMap converterMap;

    protected JXPathContextCutlet(JXPathContext jxpathContext) {
        this.context = jxpathContext;
        this.converterMap = ConverterMap.DEFAULT_CONVERTER_MAP;
    }

    @SuppressWarnings("unchecked")
    @Override
    public J withConverterMap(ConverterMap converterMap) {
        this.converterMap = converterMap;
        return (J) this;
    }

    // Write methods

    @Override
    public abstract String write(WriteStyle style);

    @Override
    public StringBuffer write(StringBuffer stringBuffer, WriteStyle style) {
        return stringBuffer.append(write(style));
    }

    @Override
    public void write(OutputStream outputStream, WriteStyle style) {
        try {
            outputStream.write(write(style).getBytes());
        } catch (IOException e) {
            throw new RuntimeException("Error writing to stream", e);
        }
    }

    @Override
    public void write(File file, WriteStyle style) {
        try {
            FileUtils.write(file, write(style));
        } catch (IOException e) {
            throw new RuntimeException("Error writing to file [" + file + "]", e);
        }
    }

    // Cutlet methods

    @Override
    public J get(String xpath) {
        Pointer pointer = context.getPointer(xpath);
        if (pointer != null) {
            JXPathContext relativeContext = context.getRelativeContext(pointer);
            return createCutlet(relativeContext);
        } else {
            throw new CutletRuntimeException("No node at [" + xpath + "] in [" + getContextBean(this) + "]");
        }
    }

    @Override
    public boolean exists(String xpath) {
        try {
            Object value = context.getValue(xpath);
            return value != null;
        } catch (JXPathNotFoundException e) {
            return false;
        }
    }

    @Override
    public boolean has(String xpath) {
        try {
            Object value = context.getValue(xpath);
            return value != null && !isBlank(value.toString());
        } catch (JXPathNotFoundException e) {
            return false;
        }
    }

    @Override
    public List<J> getArray(String xpath) {
        Iterator<?> i = context.iteratePointers(xpath);

        List<J> c = new ArrayList<>();
        while (i.hasNext()) {
            Pointer p = (Pointer) i.next();
            c.add(createCutlet(context.getRelativeContext(p)));
        }

        return c;
    }

    @Override
    public J add(String xpath) {
        context.createPath(xpath);
        return get(xpath);
    }

    @Override
    public abstract J withArray(String xpath, List<J> cutlets);

    @Override
    public void remove(String xpath) {
        context.removeAll(xpath);
    }

    // Value methods

    @Override
    public J withBigInteger(String xpath, BigInteger value) {
        return with(xpath, value, BigInteger.class);
    }

    @Override
    public <T> T get(String xpath, Class<T> clazz) {
        Object value = getAtPath(xpath);

        return converterMap.read(value, clazz);
    }

    public Object getAtPath(String xpath) {
        try {
            return context.getValue(xpath);
        } catch (JXPathNotFoundException e) {
            String p = "";
            for (String s : xpath.split("/")) {
                p = p + (p.length() > 0 ? "/" : "") + s;
                try {
                    context.getValue(p);
                } catch (JXPathNotFoundException f) {
                    throw new CutletRuntimeException("Path [" + p + "] not found while getting value at [" + xpath + "]", f);
                }
            }
            throw new CutletRuntimeException("Error getting value at [" + xpath + "]", e);
        }
    }

    @Override
    public <T> List<T> getArray(String xpath, Class<T> clazz) {
        Iterator<?> i = context.iterate(xpath);

        List<T> c = new ArrayList<>();
        while (i.hasNext()) {
            c.add(converterMap.read(i.next(), clazz));
        }

        return c;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> J with(String xpath, T value, Class<T> clazz) {
        context.createPathAndSetValue(xpath, converterMap.write(value, clazz));
        return (J) this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> J withArray(String xpath, Collection<T> values, Class<T> clazz) {
        List<Object> converted = new ArrayList<>(values.size());
        for (T t : values) {
            converted.add(converterMap.write(t, clazz));
        }
        context.createPathAndSetValue(xpath, converted);
        return (J) this;
    }

    // String methods

    @Override
    public String getString(String xpath) {
        return get(xpath, String.class);
    }

    @Override
    public List<String> getStringArray(String xpath) {
        return getArray(xpath, String.class);
    }

    @Override
    public J withString(String xpath, String value) {
        return with(xpath, value, String.class);
    }

    // Boolean methods

    @Override
    public Boolean getBoolean(String xpath) {
        return get(xpath, Boolean.class);
    }

    @Override
    public List<Boolean> getBooleanArray(String xpath) {
        return getArray(xpath, Boolean.class);
    }

    @Override
    public J withBoolean(String xpath, Boolean value) {
        return with(xpath, value, Boolean.class);
    }

    // Integer methods

    @Override
    public Integer getInteger(String xpath) {
        return get(xpath, Integer.class);
    }

    @Override
    public List<Integer> getIntegerArray(String xpath) {
        return getArray(xpath, Integer.class);
    }

    @Override
    public J withInteger(String xpath, Integer value) {
        return with(xpath, value, Integer.class);
    }

    // Long methods

    @Override
    public Long getLong(String xpath) {
        return get(xpath, Long.class);
    }

    @Override
    public List<Long> getLongArray(String xpath) {
        return getArray(xpath, Long.class);
    }

    @Override
    public J withLong(String xpath, Long value) {
        return with(xpath, value, Long.class);
    }

    // Double methods

    @Override
    public Double getDouble(String xpath) {
        return get(xpath, Double.class);
    }

    @Override
    public List<Double> getDoubleArray(String xpath) {
        return getArray(xpath, Double.class);
    }

    @Override
    public J withDouble(String xpath, Double value) {
        return with(xpath, value, Double.class);
    }

    // Float methods

    @Override
    public Float getFloat(String xpath) {
        return get(xpath, Float.class);
    }

    @Override
    public List<Float> getFloatArray(String xpath) {
        return getArray(xpath, Float.class);
    }

    @Override
    public J withFloat(String xpath, Float value) {
        return with(xpath, value, Float.class);
    }

    // LocalDate methods

    @Override
    public LocalDate getLocalDate(String xpath) {
        return get(xpath, LocalDate.class);
    }

    @Override
    public List<LocalDate> getLocalDateArray(String xpath) {
        return getArray(xpath, LocalDate.class);
    }

    @Override
    public J withLocalDate(String xpath, LocalDate value) {
        return with(xpath, value, LocalDate.class);
    }

    // DateTime methods

    @Override
    public DateTime getDateTime(String xpath) {
        return get(xpath, DateTime.class);
    }

    @Override
    public List<DateTime> getDateTimeArray(String xpath) {
        return getArray(xpath, DateTime.class);
    }

    @Override
    public J withDateTime(String xpath, DateTime value) {
        return with(xpath, value, DateTime.class);
    }

    // BigDecimal methods

    @Override
    public BigDecimal getBigDecimal(String xpath) {
        return get(xpath, BigDecimal.class);
    }

    @Override
    public List<BigDecimal> getBigDecimalArray(String xpath) {
        return getArray(xpath, BigDecimal.class);
    }

    @Override
    public J withBigDecimal(String xpath, BigDecimal value) {
        return with(xpath, value, BigDecimal.class);
    }

    // BigInteger methods

    @Override
    public BigInteger getBigInteger(String xpath) {
        return get(xpath, BigInteger.class);
    }

    @Override
    public List<BigInteger> getBigIntegerArray(String xpath) {
        return getArray(xpath, BigInteger.class);
    }

    // Other

    static Object getContextBean(Cutlet cutlet) {
        return ((JXPathContextCutlet) cutlet).context.getContextBean();
    }

    protected abstract J createCutlet(JXPathContext jxpathContext);

    @Override
    public String toString() {
        return write(PRETTY);
    }
}