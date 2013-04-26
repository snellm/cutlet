package org.snellm.cutlet;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.JXPathNotFoundException;
import org.apache.commons.jxpath.Pointer;
import org.snellm.cutlet.converters.BigDecimalConverter;
import org.snellm.cutlet.converters.ValueConverter;

import java.math.BigDecimal;
import java.util.*;

public abstract class AbstractCutlet implements Cutlet {
    protected final JXPathContext context;

    private final Map<Class<?>, ValueConverter<?>> converterMap = new HashMap<>();

    protected AbstractCutlet(JXPathContext jxpathContext) {
        this.context = jxpathContext;

        converterMap.put(BigDecimal.class, new BigDecimalConverter());
    }

    protected abstract AbstractCutlet createCutlet(JXPathContext jxpathContext);

    @Override
    public Object getContextBean() {
        return context.getContextBean();
    }

    private Object getValue(String xpath) {
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
    public Cutlet get(String xpath) {
        Pointer pointer = context.getPointer(xpath);
        if (pointer != null) {
            JXPathContext relativeContext = context.getRelativeContext(pointer);
            return createCutlet(relativeContext);
        } else {
            throw new CutletRuntimeException("No node at [" + xpath + "] in [" + getContextBean() + "]");
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
    public List<Cutlet> getArray(String xpath) {
        Iterator<?> i = context.iteratePointers(xpath);

        List<Cutlet> c = new ArrayList<>();
        while (i.hasNext()) {
            Pointer p = (Pointer) i.next();
            c.add(createCutlet(context.getRelativeContext(p)));
        }

        return c;
    }

    @Override
    public Cutlet add(String xpath) {
        context.createPath(xpath);
        return get(xpath);
    }

    @Override
    public abstract Cutlet addArray(String xpath, List<Cutlet> cutlets);

    @Override
    public String getString(String xpath) {
        Object o = getValue(xpath);

        if (o == null) {
            throw new CutletRuntimeException("No value at [" + xpath + "] in [" + getContextBean() + "]");
        } else if (o instanceof String) {
            return (String) o;
        } else {
            return o.toString();
        }
    }

    @Override
    public String getOptionalString(String xpath) {
        return (String) getValue(xpath);
    }

    @Override
    public List<String> getStringArray(String xpath) {
        Iterator<?> i = context.iterate(xpath);

        List<String> c = new ArrayList<>();
        while (i.hasNext()) {
            c.add((String) i.next());
        }

        return c;
    }

    @Override
    public AbstractCutlet addString(String xpath, String value) {
        add(xpath);
        context.setValue(xpath, value);
        return this;
    }

    @Override
    public BigDecimal getBigDecimal(String xpath) {
        Object o = getValue(xpath);

        try {
            return parse(BigDecimal.class, o);
        } catch (RuntimeException e) {
            throw new CutletRuntimeException("Cannot parse BigDecimal at path [" + xpath + "] in [" + getContextBean() + "]", e);
        }
    }

    @Override
    public List<BigDecimal> getBigDecimalArray(String xpath) {
        Iterator<?> i = context.iterate(xpath);

        List<BigDecimal> c = new ArrayList<>();
        while (i.hasNext()) {
            c.add(parse(BigDecimal.class, i.next()));
        }

        return c;
    }

    @Override
    public AbstractCutlet addBigDecimal(String xpath, BigDecimal value) {
        add(xpath);
        context.setValue(xpath, write(BigDecimal.class, value));
        return this;
    }

    @Override
    public void removeAll(String xpath) {
        context.removeAll(xpath);
    }

    @SuppressWarnings("unchecked")
    private <T> T parse(Class<T> clazz, Object object) {
        return ((ValueConverter<T>) converterMap.get(clazz)).read(object);
    }

    @SuppressWarnings("unchecked")
    private <T> Object write(Class<T> clazz, T t) {
        return ((ValueConverter<T>) converterMap.get(clazz)).write(t);
    }
}