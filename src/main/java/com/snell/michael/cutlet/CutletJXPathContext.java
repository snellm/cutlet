// Copyright (c) 2015 Michael Snell - see https://github.com/snellm/cutlet

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
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

import static com.snell.michael.cutlet.WriteStyle.PRETTY;
import static org.apache.commons.lang.StringUtils.isBlank;

abstract class CutletJXPathContext<J extends CutletJXPathContext<J>> implements Cutlet<J> {
    protected final JXPathContext context;

    private static final MicrotypeRegistry MICROTYPE_REGISTRY = new MicrotypeRegistry();

    private ConverterMap converterMap;

    protected CutletJXPathContext(JXPathContext jxpathContext) {
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
            return create(relativeContext);
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
    public List<J> getList(String xpath) {
        Iterator<?> i = context.iteratePointers(xpath);

        List<J> c = new ArrayList<>();
        while (i.hasNext()) {
            Pointer p = (Pointer) i.next();
            c.add(create(context.getRelativeContext(p)));
        }

        return c;
    }

    @Override
    public J add(String xpath) {
        context.createPath(xpath);
        return get(xpath);
    }

    @Override
    public abstract J withList(String xpath, List<J> cutlets);

    @Override
    public void remove(String xpath) {
        context.removeAll(xpath);
    }

    // Value methods

    @Override
    public <T> T get(String xpath, Class<T> clazz) {
        Object value = getPath(xpath);

        return convertFromJSONValue(xpath, clazz, value);
    }

    private <T> T convertFromJSONValue(String xpath, Class<T> clazz, Object value) {
        if (clazz.isEnum()) {
            return convertToEnum(clazz, value);
        } else if (converterMap.hasConverter(clazz)){
            return converterMap.read(value, clazz);
        } else if (MICROTYPE_REGISTRY.isMicrotype(clazz)) {
            return convertToMicrotype(clazz, value);
        } else {
            throw new RuntimeException("Converting value [" + value + "] at [" + xpath + "] to [" + clazz + "] not supported");
        }
    }

    private Object getPath(String xpath) {
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

    @SuppressWarnings("unchecked")
    private <T> T convertToEnum(Class<T> clazz, Object value) {
        if (value == null) {
            return null;
        } else {
            return (T) Enum.valueOf((Class<? extends Enum>) clazz, value.toString());
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T convertToMicrotype(Class<T> clazz, Object value) {
        Class<?> valueClass = MICROTYPE_REGISTRY.getMicrotypeValueClass(clazz);
        Object convertedValue = converterMap.read(value, valueClass);
        try {
            return clazz.getConstructor(valueClass).newInstance(convertedValue);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Failed to instantiate microtype", e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Microtype has no accessible constructor taking wrapped value", e);
        }
    }

    @Override
    public <T> List<T> getList(String xpath, Class<T> clazz) {
        List<T> list = new ArrayList<>(0);
        populateCollection(xpath, clazz, list);
        return list;
    }

    @Override
    public <T> Set<T> getSet(String xpath, Class<T> clazz) {
        Set<T> set = new HashSet<>(0);
        populateCollection(xpath, clazz, set);
        return set;
    }

    private <T> void populateCollection(String xpath, Class<T> clazz, Collection<T> c) {
        Iterator<?> i = context.iterate(xpath);
        while (i.hasNext()) {
            c.add(converterMap.read(i.next(), clazz));
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> J with(String xpath, T value) {
        Object convertedValue = null;
        if (value != null) {
            Class<T> clazz = (Class<T>) value.getClass();
            convertedValue = convertToJSONValue(xpath, value, clazz);
        }

        context.createPathAndSetValue(xpath, convertedValue);
        return (J) this;
    }

    @SuppressWarnings("unchecked")
    private <T> J with(String xpath, T value, Class<T> clazz) {
        Object convertedValue = convertToJSONValue(xpath, value, clazz);

        context.createPathAndSetValue(xpath, convertedValue);
        return (J) this;
    }

    private <T> Object convertToJSONValue(String xpath, T value, Class<T> clazz) {
        if (clazz.isEnum()) {
            return value.toString();
        } else if (converterMap.hasConverter(clazz)) {
            return converterMap.write(value, clazz);
        } else if (MICROTYPE_REGISTRY.isMicrotype(clazz)) {
            return convertMicrotypeToJSONValue(value, clazz);
        } else {
            throw new RuntimeException("Converting value [" + value + "] at [" + xpath + "] to [" + clazz + "] not supported");
        }
    }

    @SuppressWarnings("unchecked")
    private <T> Object convertMicrotypeToJSONValue(T microtype, Class<T> clazz) {
        try {
            Object value = clazz.getMethod("getValue").invoke(microtype);
            if (value == null) {
                return null;
            } else {
                Class<Object> valueClass = (Class<Object>) value.getClass();
                return converterMap.write(value, valueClass);
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Unable to invoke getValue method on microtype");
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("No getValue method on microtype");
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> J withList(String xpath, Collection<T> values, Class<T> clazz) {
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
    public List<String> getStringList(String xpath) {
        return getList(xpath, String.class);
    }

    @Override
    public Set<String> getStringSet(String xpath) {
        return getSet(xpath, String.class);
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
    public List<Boolean> getBooleanList(String xpath) {
        return getList(xpath, Boolean.class);
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
    public List<Integer> getIntegerList(String xpath) {
        return getList(xpath, Integer.class);
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
    public List<Long> getLongList(String xpath) {
        return getList(xpath, Long.class);
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
    public List<Double> getDoubleList(String xpath) {
        return getList(xpath, Double.class);
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
    public List<Float> getFloatList(String xpath) {
        return getList(xpath, Float.class);
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
    public List<LocalDate> getLocalDateList(String xpath) {
        return getList(xpath, LocalDate.class);
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
    public List<DateTime> getDateTimeList(String xpath) {
        return getList(xpath, DateTime.class);
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
    public List<BigDecimal> getBigDecimalList(String xpath) {
        return getList(xpath, BigDecimal.class);
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
    public List<BigInteger> getBigIntegerList(String xpath) {
        return getList(xpath, BigInteger.class);
    }

    @Override
    public J withBigInteger(String xpath, BigInteger value) {
        return with(xpath, value, BigInteger.class);
    }

    // Currency

    @Override
    public Currency getCurrency(String xpath) {
        return get(xpath, Currency.class);
    }

    @Override
    public List<Currency> getCurrencyList(String xpath) {
        return getList(xpath, Currency.class);
    }

    @Override
    public J withCurrency(String xpath, Currency value) {
        return with(xpath, value, Currency.class);
    }

    // Other

    static Object getContextBean(Cutlet cutlet) {
        return ((CutletJXPathContext) cutlet).context.getContextBean();
    }

    protected abstract J create(JXPathContext jxpathContext);

    @Override
    public String toString() {
        return write(PRETTY);
    }
}