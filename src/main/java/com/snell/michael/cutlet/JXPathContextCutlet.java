package com.snell.michael.cutlet;

import com.snell.michael.cutlet.converters.ValueConverters;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.JXPathNotFoundException;
import org.apache.commons.jxpath.Pointer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

abstract class JXPathContextCutlet<J extends JXPathContextCutlet<J>> implements Cutlet<J> {
    protected final JXPathContext context;

    protected JXPathContextCutlet(JXPathContext jxpathContext) {
        this.context = jxpathContext;
    }

    protected abstract J createCutlet(JXPathContext jxpathContext);

    static Object getContextBean(Cutlet cutlet) {
        return ((JXPathContextCutlet) cutlet).context.getContextBean();
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
        Object o = getValue(xpath);

        if (o == null) {
            throw new CutletRuntimeException("No value at [" + xpath + "] in [" + getContextBean(this) + "]");
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

    @SuppressWarnings("unchecked")
    @Override
    public J addString(String xpath, String value) {
        add(xpath);
        context.setValue(xpath, value);
        return (J) this;
    }

    @Override
    public BigDecimal getBigDecimal(String xpath) {
        Object o = getValue(xpath);

        try {
            return ValueConverters.read(BigDecimal.class, o);
        } catch (RuntimeException e) {
            throw new CutletRuntimeException("Cannot parse BigDecimal at path [" + xpath + "] in [" + getContextBean(this) + "]", e);
        }
    }

    @Override
    public List<BigDecimal> getBigDecimalArray(String xpath) {
        Iterator<?> i = context.iterate(xpath);

        List<BigDecimal> c = new ArrayList<>();
        while (i.hasNext()) {
            c.add(ValueConverters.read(BigDecimal.class, i.next()));
        }

        return c;
    }

    @SuppressWarnings("unchecked")
    @Override
    public J addBigDecimal(String xpath, BigDecimal value) {
        add(xpath);
        context.setValue(xpath, ValueConverters.write(BigDecimal.class, value));
        return (J) this;
    }

    @Override
    public void remove(String xpath) {
        context.removeAll(xpath);
    }
}