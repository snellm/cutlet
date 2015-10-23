// Copyright (c) 2015 Michael Snell - see https://github.com/snellm/cutlet

package com.snell.michael.cutlet;

import org.apache.commons.io.FileUtils;
import org.apache.commons.jxpath.AbstractFactory;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.Pointer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static com.snell.michael.cutlet.WriteStyle.COMPACT;
import static java.lang.Boolean.TRUE;

public class XMLCutlet extends JXPathContextCutlet<XMLCutlet> {
    private static DOMImplementationLS DOM_IMPLEMENTATION;
    private static LSParser PARSER;

    private final Document document;

    private XMLCutlet(JXPathContext jxpathContext, final Document document) {
        super(jxpathContext);
        this.document = document;

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
    }

    @Override
    protected XMLCutlet createCutlet(JXPathContext jxpathContext) {
        return new XMLCutlet(jxpathContext, document);
    }

    @Override
    public Set<String> getChildren() {
        Element element = (Element) getContextBean(this);
        NodeList nodeList = element.getChildNodes();
        Set<String> s = new LinkedHashSet<>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getLocalName() != null) {
                s.add(node.getLocalName());
            }
        }
        return s;
    }

    @Override
    public XMLCutlet withList(String xpath, List<XMLCutlet> cutlets) {
        Pointer p = context.createPath(xpath);
        Element e = (Element) p.getNode();
        for (XMLCutlet cutlet : cutlets) {
            Node n = (Node) getContextBean(cutlet);
            Node ni = ((Element) getContextBean(this)).getOwnerDocument().importNode(n, true);
            e.appendChild(ni);
        }

        return this;
    }

    @Override
    public int hashCode() {
        return write(COMPACT).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof XMLCutlet) {
            return write(COMPACT).equals(((XMLCutlet) obj).write(COMPACT));
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
    public static XMLCutlet parse(String xml) {
        Document document = parseToDocument(xml);
        return getCutletFromDocument(document);
    }

    /**
     * Parse a XML input stream into a Cutlet class with can be queried using XPath expressions
     */
    public static XMLCutlet parse(InputStream inputStream) {
        Document document = parseToDocument(inputStream);
        return getCutletFromDocument(document);
    }

    /**
     * Parse a XML file into a Cutlet class with can be queried using XPath expressions
     */
    public static XMLCutlet parse(File file) {
        try {
            Document document = parseToDocument(FileUtils.openInputStream(file));
            return getCutletFromDocument(document);
        } catch (IOException e) {
            throw new RuntimeException("IO exception reading from file [" + file + "]");
        }
    }

    private static XMLCutlet getCutletFromDocument(Document document) {
        JXPathContext context = JXPathContext.newContext(document);
        Pointer pointer = context.getPointer(document.getDocumentElement().getNodeName());

        return new XMLCutlet(context.getRelativeContext(pointer), document);
    }

    /**
     * Create an empty XMLCutlet than can later be serialised to XML
     * The root node of the XML document must be specified, and is not required in further queries
     */
    public static XMLCutlet create(String rootNode) {
        Document document = parseToDocument("<" + rootNode + "/>");
        JXPathContext context = JXPathContext.newContext(document);

        Pointer pointer = context.getPointer(rootNode);
        return new XMLCutlet(context.getRelativeContext(pointer), document);
    }

    private static String serializeXML(Document document, WriteStyle style) {
        boolean prettyPrint = WriteStyle.PRETTY.equals(style);
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();

        createParser();
        LSSerializer serializer = DOM_IMPLEMENTATION.createLSSerializer();
        serializer.getDomConfig().setParameter("xml-declaration", TRUE);
        if (serializer.getDomConfig().canSetParameter("format-pretty-print", prettyPrint)) {
            serializer.getDomConfig().setParameter("format-pretty-print", prettyPrint);
        }

        LSOutput output = DOM_IMPLEMENTATION.createLSOutput();
        output.setEncoding("UTF-8");
        output.setByteStream(byteStream);
        serializer.write(document, output);
        return byteStream.toString().trim();
    }

    /**
     * Output a XMLCutlet as UTF-8 encoded XML text
     */
    @Override
    public String write(WriteStyle style) {
        return serializeXML(document, style);
    }
}