package org.snellm.cutlet;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.JXPathNotFoundException;
import org.apache.commons.jxpath.Pointer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class XPathObject {
    protected final JXPathContext context;

    protected XPathObject(JXPathContext jxpathContext) {
        this.context = jxpathContext;
    }

    protected abstract XPathObject createXPathObject(JXPathContext jxpathContext);

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
                    throw new XPathObjectRuntimeException("Path [" + p + "] not found while getting value at [" + xpath + "]", f);
                }
            }
            throw new XPathObjectRuntimeException("Error getting value at [" + xpath + "]", e);
        }
    }

    public XPathObject get(String xpath) {
        Pointer pointer = context.getPointer(xpath);
        if (pointer != null) {
            JXPathContext relativeContext = context.getRelativeContext(pointer);
            return createXPathObject(relativeContext);
        } else {
            throw new XPathObjectRuntimeException("No node at [" + xpath + "] in [" + getContextBean() + "]");
        }
    }

    public boolean has(String xpath) {
        try {
            getString(xpath);
        }
        catch (Exception e) {
            return false;
        }
        return true;
    }

    public List<XPathObject> getArray(String xpath) {
        Iterator<?> i = context.iteratePointers(xpath);

        List<XPathObject> c = new ArrayList<>();
        while (i.hasNext()) {
            Pointer p = (Pointer) i.next();
            c.add(createXPathObject(context.getRelativeContext(p)));
        }

        return c;
    }

    public XPathObject add(String xpath) {
        context.createPath(xpath);
        return get(xpath);
    }

    public abstract XPathObject addArray(String xpath, List<XPathObject> xpos);

    public String getString(String xpath) {
        Object o = getValue(xpath);

        if (o == null) {
            throw new XPathObjectRuntimeException("No value at [" + xpath + "] in [" + getContextBean() + "]");
        } else if (o instanceof String) {
            return (String) o;
        } else {
            return o.toString();
        }
    }

    public String getOptionalString(String xpath) {
        return (String) getValue(xpath);
    }

    public List<String> getStringArray(String xpath) {
        Iterator<?> i = context.iterate(xpath);

        List<String> c = new ArrayList<>();
        while (i.hasNext()) {
            c.add((String) i.next());
        }

        return c;
    }

    public XPathObject addString(String xpath, String value) {
        add(xpath);
        context.setValue(xpath, value);
        return this;
    }

    public BigDecimal getBigDecimal(String xpath) {
        Object o = getValue(xpath);

        try {
            return BigDecimalConverter.parse(o);
        } catch (RuntimeException e) {
            throw new XPathObjectRuntimeException("Cannot parse BigDecimal at path [" + xpath + "] in [" + getContextBean() + "]", e);
        }
    }

    public List<BigDecimal> getBigDecimalArray(String xpath) {
        Iterator<?> i = context.iterate(xpath);

        List<BigDecimal> c = new ArrayList<>();
        while (i.hasNext()) {
            c.add(BigDecimalConverter.parse(i.next()));
        }

        return c;
    }

    public XPathObject addBigDecimal(String xpath, BigDecimal value) {
        add(xpath);
        context.setValue(xpath, value);
        return this;
    }

    public void removeAll(String xpath) {
        context.removeAll(xpath);
    }
}