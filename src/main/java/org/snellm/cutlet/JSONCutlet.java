package org.snellm.cutlet;

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
    protected AbstractCutlet createXPathObject(JXPathContext jxpathContext) {
        return new JSONCutlet(jxpathContext);
    }

    @Override
    public AbstractCutlet addArray(String xpath, List<Cutlet> cutlets) {
        Collection<Object> os = new ArrayList<>(cutlets.size());
        for (Cutlet cutlet : cutlets) {
            os.add(cutlet.getContextBean());
        }
        context.createPathAndSetValue(xpath, os);

        return this;
    }

    /**
     * Parse a JSON string into a XPathObject class with can be queried using XPath expressions
     */
    public static JSONCutlet parse(String json) {
        return new JSONCutlet(JXPathContext.newContext(JSONSerializer.toJSON(json)));
    }

    /**
     * Create an empty XPathObject than can later be serialised to JSON
     */
    public static AbstractCutlet create() {
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

    /**
     * Output a XPathObject as JSON text
     * This is pretty-printed with newlines and indentation
     */
    public static String print(Cutlet cutlet) {
        if (cutlet.getContextBean() instanceof JSONObject) {
            return ((JSONObject) cutlet.getContextBean()).toString(2);
        } else {
            throw new RuntimeException("Cannot parse [" + cutlet.getContextBean().getClass() + "] to JSON string - must be JSONObject");
        }
    }

    public String print() {
        return ((JSONObject) getContextBean()).toString(2);
    }
}