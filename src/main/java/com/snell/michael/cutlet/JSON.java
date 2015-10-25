// Copyright (c) 2015 Michael Snell - see https://github.com/snellm/cutlet

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
import java.util.Set;
import java.util.regex.Pattern;

import static com.snell.michael.cutlet.WriteStyle.PRETTY;
import static java.util.regex.Pattern.DOTALL;

public class JSON extends CutletJXPathContext<JSON> {
    private static final Pattern COMMENT_PATTERN = Pattern.compile("^/\\*.*?\\*/", DOTALL);

    private final JSON root;

    private JSON(JSON root, JXPathContext jxpathContext) {
        super(jxpathContext);

        this.root = (root == null ? this : root);

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
    }

    @Override
    protected JSON create(JXPathContext jxpathContext) {
        return new JSON(root == null? this : root, jxpathContext);
    }

    @Override
    public String write(WriteStyle style) {
        return ((JSONObject) getContextBean(root)).toString(PRETTY.equals(style) ? 2 : 0);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<String> getChildren() {
        return ((JSONObject) getContextBean(this)).keySet();
    }

    @Override
    public JSON withList(String xpath, List<JSON> jsons) {
        Collection<Object> os = new ArrayList<>(jsons.size());
        for (JSON json : jsons) {
            os.add(getContextBean(json));
        }
        context.createPathAndSetValue(xpath, os);

        return this;
    }

    /**
     * Parse a JSON string into a Cutlet
     */
    public static JSON parse(String string) {
        try {
            string = stripComments(string);
            return new JSON(null, JXPathContext.newContext(JSONSerializer.toJSON(string)));
        } catch (RuntimeException e) {
            throw new CutletRuntimeException("Could not parse [" + string + "] as JSON", e);
        }
    }

    private static String stripComments(String string) {
        return COMMENT_PATTERN.matcher(string.trim()).replaceFirst("").trim();
    }

    /**
     * Parse a JSON input stream into a Cutlet
     */
    public static JSON parse(InputStream inputStream) {
        String string;
        try {
            string = IOUtils.toString(inputStream);
        } catch (IOException e) {
            throw new CutletRuntimeException("IO exception reading from input stream [" + inputStream + "]", e);
        }

        return parse(string);
    }

    /**
     * Parse a JSON file into a Cutlet
     */
    public static JSON parse(File file) {
        String string;
        try {
            string = FileUtils.readFileToString(file);
        } catch (IOException e) {
            throw new CutletRuntimeException("IO exception reading from file [" + file + "]", e);
        }

        return parse(string);
    }

    /**
     * Parse a JSON file into a Cutlet
     */
    public static JSON parseFile(String filename) {
        return parse(new File(filename));
    }

    /**
     * Create an empty JSONCutlet
     */
    public static JSON create() {
        return new JSON(null, JXPathContext.newContext(new JSONObject()));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof JSON) {
            return getContextBean(root).equals((getContextBean(((JSON) obj).root)));
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return getContextBean(this).hashCode();
    }
}