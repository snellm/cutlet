package com.snell.michael.cutlet;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.jxpath.AbstractFactory;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.Pointer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class JSONCutlet extends JXPathContextCutlet<JSONCutlet> {
    private JSONCutlet(JXPathContext jxpathContext) {
        super(jxpathContext);
    }

    @Override
    protected JSONCutlet createCutlet(JXPathContext jxpathContext) {
        return new JSONCutlet(jxpathContext);
    }

    @Override
    public JSONCutlet addArray(String xpath, List<JSONCutlet> cutlets) {
        Collection<Object> os = new ArrayList<>(cutlets.size());
        for (JSONCutlet cutlet : cutlets) {
            os.add(getContextBean(cutlet));
        }
        context.createPathAndSetValue(xpath, os);

        return this;
    }

    /**
     * Parse a JSON string into a JSONCutlet with can be queried using XPath expressions
     */
    public static JSONCutlet parse(String json) {
        return new JSONCutlet(JXPathContext.newContext(JSONSerializer.toJSON(json)));
    }

    /**
     * Parse a JSON input stream into a JSONCutlet with can be queried using XPath expressions
     */
    public static JSONCutlet parse(InputStream inputStream) {
        try {
            return new JSONCutlet(JXPathContext.newContext(JSONSerializer.toJSON(IOUtils.toString(inputStream))));
        } catch (IOException e) {
            throw new CutletRuntimeException("IO exception reading from input stream [" + inputStream + "]", e);
        }
    }

    /**
     * Parse a JSON file into a JSONCutlet with can be queried using XPath expressions
     */
    public static JSONCutlet parse(File file) {
        try {
            return new JSONCutlet(JXPathContext.newContext(JSONSerializer.toJSON(FileUtils.readFileToString(file))));
        } catch (IOException e) {
            throw new CutletRuntimeException("IO exception reading from file [" + file + "]", e);
        }
    }

    /**
     * Create an empty JSONCutlet
     */
    public static JSONCutlet create() {
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
            return getContextBean(this).equals((getContextBean((JSONCutlet) obj)));
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return getContextBean(this).hashCode();
    }

    private String print(boolean pretty) {
        if (getContextBean(this) instanceof JSONObject) {
            return ((JSONObject) getContextBean(this)).toString(pretty ? 2 : 0);
        } else {
            throw new RuntimeException("Cannot parse [" + getContextBean(this).getClass() + "] to JSON string - must be JSONObject");
        }
    }

    /**
     * Output a JSONCutlet as JSON text
     * This is printed compactly
     */
    @Override
    public String printCompact() {
        return print(false);
    }


    /**
     * Output a JSONCutlet as JSON text
     * This is pretty-printed with newlines and indentation
     */
    @Override
    public String printPretty() {
        return print(true);
    }
}