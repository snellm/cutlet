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
import java.util.Iterator;
import java.util.List;

import static com.snell.michael.cutlet.WriteStyle.PRETTY;

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
    public boolean has(String xpath) {
        try {
            getString(xpath);
        } catch (Exception e) {
            return false;
        }
        return true;
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
    public abstract J addArray(String xpath, List<J> cutlets);

    @Override
    public void remove(String xpath) {
        context.removeAll(xpath);
    }

    // Value methods

    @Override
    public J addBigInteger(String xpath, BigInteger value) {
        return addValue(xpath, value, BigInteger.class);
    }

    @Override
    public <T> T getValue(String xpath, Class<T> clazz) {
        try {
            return converterMap.read(context.getValue(xpath), clazz);
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
    public <T> List<T> getValueArray(String xpath, Class<T> clazz) {
        Iterator<?> i = context.iterate(xpath);

        List<T> c = new ArrayList<>();
        while (i.hasNext()) {
            c.add(converterMap.read(i.next(), clazz));
        }

        return c;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> J addValue(String xpath, T value, Class<T> clazz) {
        add(xpath);
        context.setValue(xpath, converterMap.write(value, clazz));
        return (J) this;
    }

    // String methods

    @Override
    public String getString(String xpath) {
        return getValue(xpath, String.class);
    }

    @Override
    public List<String> getStringArray(String xpath) {
        return getValueArray(xpath, String.class);
    }

    @Override
    public J addString(String xpath, String value) {
        return addValue(xpath, value, String.class);
    }

    // Boolean methods

    @Override
    public Boolean getBoolean(String xpath) {
        return getValue(xpath, Boolean.class);
    }

    @Override
    public List<Boolean> getBooleanArray(String xpath) {
        return getValueArray(xpath, Boolean.class);
    }

    @Override
    public J addBoolean(String xpath, Boolean value) {
        return addValue(xpath, value, Boolean.class);
    }

    // LocalDate methods

    @Override
    public LocalDate getLocalDate(String xpath) {
        return getValue(xpath, LocalDate.class);
    }

    @Override
    public List<LocalDate> getLocalDateArray(String xpath) {
        return getValueArray(xpath, LocalDate.class);
    }

    @Override
    public J addLocalDate(String xpath, LocalDate value) {
        return addValue(xpath, value, LocalDate.class);
    }

    // DateTime methods

    @Override
    public DateTime getDateTime(String xpath) {
        return getValue(xpath, DateTime.class);
    }

    @Override
    public List<DateTime> getDateTimeArray(String xpath) {
        return getValueArray(xpath, DateTime.class);
    }

    @Override
    public J addDateTime(String xpath, DateTime value) {
        return addValue(xpath, value, DateTime.class);
    }

    // BigDecimal methods

    @Override
    public BigDecimal getBigDecimal(String xpath) {
        return getValue(xpath, BigDecimal.class);
    }

    @Override
    public List<BigDecimal> getBigDecimalArray(String xpath) {
        return getValueArray(xpath, BigDecimal.class);
    }

    @Override
    public J addBigDecimal(String xpath, BigDecimal value) {
        return addValue(xpath, value, BigDecimal.class);
    }

    // BigInteger methods

    @Override
    public BigInteger getBigInteger(String xpath) {
        return getValue(xpath, BigInteger.class);
    }

    @Override
    public List<BigInteger> getBigIntegerArray(String xpath) {
        return getValueArray(xpath, BigInteger.class);
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