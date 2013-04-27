package com.snell.michael.cutlet;

import com.snell.michael.cutlet.converters.ValueConverters;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.JXPathNotFoundException;
import org.apache.commons.jxpath.Pointer;
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

    static Object getContextBean(Cutlet cutlet) {
        return ((JXPathContextCutlet) cutlet).context.getContextBean();
    }

    protected abstract J createCutlet(JXPathContext jxpathContext);

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

    @SuppressWarnings("unchecked")
    @Override
    public J addString(String xpath, String value) {
        add(xpath);
        context.setValue(xpath, value);
        return (J) this;
    }

    @Override
    public LocalDate getLocalDate(String xpath) {
        return getValue(xpath, LocalDate.class);
    }

    @Override
    public List<LocalDate> getLocalDateArray(String xpath) {
        return getValueArray(xpath, LocalDate.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public J addLocalDate(String xpath, LocalDate value) {
        add(xpath);
        context.setValue(xpath, ValueConverters.write(value, LocalDate.class));
        return (J) this;
    }

    @Override
    public BigDecimal getBigDecimal(String xpath) {
        return getValue(xpath, BigDecimal.class);
    }

    @Override
    public List<BigDecimal> getBigDecimalArray(String xpath) {
        return getValueArray(xpath, BigDecimal.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public J addBigDecimal(String xpath, BigDecimal value) {
        add(xpath);
        context.setValue(xpath, ValueConverters.write(value, BigDecimal.class));
        return (J) this;
    }

    @Override
    public BigInteger getBigInteger(String xpath) {
        return getValue(xpath, BigInteger.class);
    }

    @Override
    public List<BigInteger> getBigIntegerArray(String xpath) {
        return getValueArray(xpath, BigInteger.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public J addBigInteger(String xpath, BigInteger value) {
        add(xpath);
        context.setValue(xpath, ValueConverters.write(value, BigInteger.class));
        return (J) this;
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

    private <T> List<T> getValueArray(String xpath, Class<T> clazz) {
        Iterator<?> i = context.iterate(xpath);

        List<T> c = new ArrayList<>();
        while (i.hasNext()) {
            c.add(ValueConverters.read(i.next(), clazz));
        }

        return c;
    }
}