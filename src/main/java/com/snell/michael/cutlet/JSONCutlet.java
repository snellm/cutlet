package com.snell.michael.cutlet;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import org.apache.commons.jxpath.AbstractFactory;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.Pointer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class JSONCutlet extends AbstractCutlet {
    private JSONCutlet(JXPathContext jxpathContext) {
        super(jxpathContext);
    }

    @Override
    protected AbstractCutlet createCutlet(JXPathContext jxpathContext) {
        return new JSONCutlet(jxpathContext);
    }

    @Override
    public Cutlet addArray(String xpath, List<Cutlet> cutlets) {
        Collection<Object> os = new ArrayList<>(cutlets.size());
        for (Cutlet cutlet : cutlets) {
            os.add(cutlet.getContextBean());
        }
        context.createPathAndSetValue(xpath, os);

        return this;
    }

    /**
     * Parse a JSON string into a JSONCutlet with can be queried using XPath expressions
     */
    public static Cutlet parse(String json) {
        return new JSONCutlet(JXPathContext.newContext(JSONSerializer.toJSON(json)));
    }

    /**
     * Create an empty JSONCutlet
     */
    public static Cutlet create() {
        JXPathContext jxpathContext = JXPathContext.newContext(new JSONObject());
        jxpathContext.setFactory(new AbstractFactory() {
            @Override
            public boolean createObject(JXPathContext context, Pointer pointer, Object parent, String name, int index) {
                if (parent instanceof JSONObject) {
                    ((JSONObject) parent).put(name, new JSONObject());
                    return true;
                } else {
                    throw new RuntimeException("Parent class [" + parent.getClass() + "] not supported");
                }
            }
        });
        return new JSONCutlet(jxpathContext);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof JSONCutlet) {
            return getContextBean().equals(((JSONCutlet) obj).getContextBean());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return getContextBean().hashCode();
    }

    /**
     * Output a JSONCutlet as JSON text
     * This is pretty-printed with newlines and indentation
     */
    public static String print(Cutlet cutlet) {
        if (cutlet.getContextBean() instanceof JSONObject) {
            return ((JSONObject) cutlet.getContextBean()).toString(2);
        } else {
            throw new RuntimeException("Cannot parse [" + cutlet.getContextBean().getClass() + "] to JSON string - must be JSONObject");
        }
    }
}