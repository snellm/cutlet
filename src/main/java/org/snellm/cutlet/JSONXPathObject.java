package org.snellm.cutlet;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import org.apache.commons.jxpath.AbstractFactory;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.Pointer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class JSONXPathObject extends XPathObject {
    private JSONXPathObject(JXPathContext jxpathContext) {
        super(jxpathContext);
    }

    @Override
    protected XPathObject createXPathObject(JXPathContext jxpathContext) {
        return new JSONXPathObject(jxpathContext);
    }

    @Override
    public XPathObject addArray(String xpath, List<XPathObject> xpos) {
        Collection<Object> os = new ArrayList<>(xpos.size());
        for (XPathObject xpo : xpos) {
            os.add(xpo.getContextBean());
        }
        context.createPathAndSetValue(xpath, os);

        return this;
    }

    /**
     * Parse a JSON string into a XPathObject class with can be queried using XPath expressions
     */
    public static JSONXPathObject parse(String json) {
        return new JSONXPathObject(JXPathContext.newContext(JSONSerializer.toJSON(json)));
    }

    /**
     * Create an empty XPathObject than can later be serialised to JSON
     */
    public static XPathObject create() {
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
        return new JSONXPathObject(jxpathContext);
    }

    /**
     * Output a XPathObject as JSON text
     * This is pretty-printed with newlines and indentation
     */
    public static String print(XPathObject xpo) {
        if (xpo.getContextBean() instanceof JSONObject) {
            return ((JSONObject) xpo.getContextBean()).toString(2);
        } else {
            throw new RuntimeException("Cannot parse [" + xpo.getContextBean().getClass() + "] to JSON string - must be JSONObject");
        }
    }

    public String print() {
        return ((JSONObject) getContextBean()).toString(2);
    }
}