package com.snell.michael.cutlet;

import org.apache.commons.io.FileUtils;
import org.apache.commons.jxpath.AbstractFactory;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.Pointer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class XMLCutlet extends AbstractCutlet {
    private static DOMImplementationLS DOM_IMPLEMENTATION;
    private static LSParser PARSER;

    private XMLCutlet(JXPathContext jxpathContext) {
        super(jxpathContext);
    }

    @Override
    protected AbstractCutlet createCutlet(JXPathContext jxpathContext) {
        return new XMLCutlet(jxpathContext);
    }

    @Override
    public Cutlet addArray(String xpath, List<Cutlet> cutlets) {
        Pointer p = context.createPath(xpath);
        Element e = (Element) p.getNode();
        for (Cutlet cutlet : cutlets) {
            Node n = (Node) getContextBean(cutlet);
            Node ni = ((Element) getContextBean(this)).getOwnerDocument().importNode(n, true);
            e.appendChild(ni);
        }

        return this;
    }

    @Override
    public int hashCode() {
        return print(this).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof XMLCutlet) {
            return print(this).equals(print((XMLCutlet) obj));
        } else {
            return false;
        }
    }

    private static synchronized void createParser() {
        if (DOM_IMPLEMENTATION == null) {
            try {
                DOM_IMPLEMENTATION = (DOMImplementationLS) DOMImplementationRegistry.newInstance().getDOMImplementation("XML 3.0");
                PARSER = DOM_IMPLEMENTATION.createLSParser(DOMImplementationLS.MODE_SYNCHRONOUS, null);
            } catch (Exception e) {
                throw new RuntimeException("Error instantiating XML implementation/parser", e);
            }
        }
    }

    private static Document parseToDocument(String text) {
        createParser();
        LSInput lsi = (DOM_IMPLEMENTATION).createLSInput();
        lsi.setStringData(text);
        return PARSER.parse(lsi);
    }

    private static Document parseToDocument(InputStream inputStream) {
        createParser();
        LSInput lsi = (DOM_IMPLEMENTATION).createLSInput();
        lsi.setByteStream(inputStream);
        return PARSER.parse(lsi);
    }

    /**
     * Parse a XML string into a Cutlet class with can be queried using XPath expressions
     */
    public static Cutlet parse(String xml) {
        Document document = parseToDocument(xml);
        return getCutletFromDocument(document);
    }

    /**
     * Parse a XML input stream into a Cutlet class with can be queried using XPath expressions
     */
    public static Cutlet parse(InputStream inputStream) {
        Document document = parseToDocument(inputStream);
        return getCutletFromDocument(document);
    }

    /**
     * Parse a XML file into a Cutlet class with can be queried using XPath expressions
     */
    public static Cutlet parse(File file) {
        try {
            Document document = parseToDocument(FileUtils.openInputStream(file));
            return getCutletFromDocument(document);
        } catch (IOException e) {
            throw new RuntimeException("IO exception reading from file [" + file + "]");
        }
    }

    private static Cutlet getCutletFromDocument(Document document) {
        JXPathContext context = JXPathContext.newContext(document);

        Pointer pointer = context.getPointer(document.getDocumentElement().getNodeName());

        return new XMLCutlet(context.getRelativeContext(pointer));
    }

    /**
     * Create an empty XMLCutlet than can later be serialised to XML
     * The root node of the XML document must be specified, and is not required in further queries
     */
    public static Cutlet create(String rootNode) {
        final Document document = parseToDocument("<" + rootNode + "/>");
        JXPathContext context = JXPathContext.newContext(document);

        context.setFactory(new AbstractFactory() {
            @Override
            public boolean createObject(JXPathContext context, Pointer pointer, Object parent, String name, int index) {
                if (parent instanceof Element) {
                    ((Element) parent).appendChild(document.createElement(name));
                    return true;
                } else {
                    throw new RuntimeException("Parent class [" + parent.getClass() + "] not supported");
                }
            }
        });

        Pointer pointer = context.getPointer(rootNode);
        return new XMLCutlet(context.getRelativeContext(pointer));
    }

    private static String serializeXML(Document document) {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();

        createParser();
        LSSerializer serializer = DOM_IMPLEMENTATION.createLSSerializer();
        if (serializer.getDomConfig().canSetParameter("format-pretty-print", Boolean.TRUE)) {
            serializer.getDomConfig().setParameter("format-pretty-print", Boolean.TRUE);
        }
        serializer.getDomConfig().setParameter("xml-declaration", Boolean.TRUE);

        LSOutput output = DOM_IMPLEMENTATION.createLSOutput();
        output.setEncoding("UTF-8");
        output.setByteStream(byteStream);

        serializer.write(document, output);
        return byteStream.toString();
    }

    /**
     * Output a XMLCutlet as XML text
     * This is UTF-8 encoded and pretty-printed with newlines and indentation
     */
    public static String print(Cutlet cutlet) {
        if (getContextBean(cutlet) instanceof Element) {
            return serializeXML(((Element) getContextBean(cutlet)).getOwnerDocument());
        } else {
            throw new RuntimeException("Cannot parse [" + getContextBean(cutlet).getClass() + "] to XML string - must be Element");
        }
    }
}