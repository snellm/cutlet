package com.snell.michael.cutlet;

import com.snell.michael.cutlet.converters.ValueConverters;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.JXPathNotFoundException;
import org.apache.commons.jxpath.Pointer;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

abstract class JXPathContextCutlet<J extends JXPathContextCutlet<J>> implements Cutlet<J> {
    protected final JXPathContext context;

    protected JXPathContextCutlet(JXPathContext jxpathContext) {
        this.context = jxpathContext;
    }

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

    @Override
    public BigInteger getBigInteger(String xpath) {
        return getValue(xpath, BigInteger.class);
    }

    @Override
    public List<BigInteger> getBigIntegerArray(String xpath) {
        return getValueArray(xpath, BigInteger.class);
    }

    @Override
    public J addBigInteger(String xpath, BigInteger value) {
        return addValue(xpath, value, BigInteger.class);
    }

    @Override
    public void remove(String xpath) {
        context.removeAll(xpath);
    }

    private <T> T getValue(String xpath, Class<T> clazz) {
        try {
            return ValueConverters.read(context.getValue(xpath), clazz);
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
    private <T> J addValue(String xpath, T value, Class<T> clazz) {
        add(xpath);
        context.setValue(xpath, ValueConverters.write(value, clazz));
        return (J) this;
    }

    private <T> List<T> getValueArray(String xpath, Class<T> clazz) {
        Iterator<?> i = context.iterate(xpath);

        List<T> c = new ArrayList<>();
        while (i.hasNext()) {
            c.add(ValueConverters.read(i.next(), clazz));
        }

        return c;
    }

    static Object getContextBean(Cutlet cutlet) {
        return ((JXPathContextCutlet) cutlet).context.getContextBean();
    }

    protected abstract J createCutlet(JXPathContext jxpathContext);
}