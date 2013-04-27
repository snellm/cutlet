package com.snell.michael.cutlet;

import com.snell.michael.cutlet.converters.ValueConverters;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.JXPathNotFoundException;
import org.apache.commons.jxpath.Pointer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

abstract class JXPathContextCutlet<JXPATHCONTEXTCUTLET extends JXPathContextCutlet<JXPATHCONTEXTCUTLET>> implements Cutlet<JXPATHCONTEXTCUTLET> {
    protected final JXPathContext context;

    protected JXPathContextCutlet(JXPathContext jxpathContext) {
        this.context = jxpathContext;
    }

    protected abstract JXPATHCONTEXTCUTLET createCutlet(JXPathContext jxpathContext);

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
    public JXPATHCONTEXTCUTLET get(String xpath) {
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
    public List<JXPATHCONTEXTCUTLET> getArray(String xpath) {
        Iterator<?> i = context.iteratePointers(xpath);

        List<JXPATHCONTEXTCUTLET> c = new ArrayList<>();
        while (i.hasNext()) {
            Pointer p = (Pointer) i.next();
            c.add(createCutlet(context.getRelativeContext(p)));
        }

        return c;
    }

    @Override
    public JXPATHCONTEXTCUTLET add(String xpath) {
        context.createPath(xpath);
        return get(xpath);
    }

    @Override
    public abstract JXPATHCONTEXTCUTLET addArray(String xpath, List<JXPATHCONTEXTCUTLET> cutlets);

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
    public JXPATHCONTEXTCUTLET addString(String xpath, String value) {
        add(xpath);
        context.setValue(xpath, value);
        return (JXPATHCONTEXTCUTLET) this;
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
    public JXPATHCONTEXTCUTLET addBigDecimal(String xpath, BigDecimal value) {
        add(xpath);
        context.setValue(xpath, ValueConverters.write(BigDecimal.class, value));
        return (JXPATHCONTEXTCUTLET) this;
    }

    @Override
    public void remove(String xpath) {
        context.removeAll(xpath);
    }
}