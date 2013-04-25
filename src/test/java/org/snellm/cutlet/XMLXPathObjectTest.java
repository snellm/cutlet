package org.snellm.cutlet;

import com.google.common.collect.Lists;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.*;
import static org.snellm.cutlet.TestUtil.assertContains;
import static org.snellm.cutlet.TestUtil.getFileResource;

public class XMLXPathObjectTest {
    private XPathObject getTestXPO() {
        return XMLXPathObject.parse(getFileResource(getClass(), "test.xml"), "person");
    }

    @Test
    public void testParse() {
        XPathObject xpo = getTestXPO();
        assertNotNull(xpo);
    }

    @Test
    public void testErrorHandling() {
        XPathObject xpo = getTestXPO();

        try {
            xpo.getString("location/city");
            fail();
        } catch (XPathObjectRuntimeException e) {
            // No op - expected for missing path
        }
    }

    @Test
    public void testStrings() {
        XPathObject xpo = getTestXPO();

        // Can get a string using XPath
        assertEquals("New York", xpo.getString("address/city"));

        // Or via nested gets
        XPathObject axpo = xpo.get("address");
        assertEquals("New York", axpo.getString("city"));
        assertEquals("NY", axpo.getString("state"));

        // Can get an array of Strings
        List<String> strings = xpo.getStringArray("favouriteColour");
        assertEquals(3, strings.size());
        for (String s : strings) {
            assertNotNull(s);
        }
    }

    @Test
    public void testNumbers() {
        XPathObject xpo = getTestXPO();

        // Can get a integer as a BigDecimal using XPath
        assertEquals(BigDecimal.valueOf(1), xpo.getBigDecimal("favouriteNumber[1]"));

        // Can get a double as a BigDecimal using XPath
        assertEquals(BigDecimal.valueOf(3.141592), xpo.getBigDecimal("favouriteNumber[2]"));

        // Can get a negative number as a BigDecimal using XPath
        assertEquals(BigDecimal.valueOf(-42), xpo.getBigDecimal("favouriteNumber[3]"));

        // Can get a number expressed in scientific notation as a BigDecimal using XPath,
        // ensuring that the number is expressed in plain format
        assertEquals(BigDecimal.valueOf(10000000), xpo.getBigDecimal("favouriteNumber[4]"));

        // Can get an array of BigDecimals
        List<BigDecimal> numbers = xpo.getBigDecimalArray("favouriteNumber");
        assertEquals(4, numbers.size());
        for (BigDecimal b : numbers) {
            assertNotNull(b);
        }
    }

    @Test
    public void testArrays() {
        XPathObject xpo = getTestXPO();

        // Can get a specific array entry directly using XPath
        assertEquals("home", xpo.getString("phoneNumber[1]/type"));

        // Get a specific entry based on an XPath selector
        assertEquals("212 555-1234", xpo.getString("phoneNumber[type = 'home']/number"));

        // Can get an array and extract values
        List<XPathObject> xpos = xpo.getArray("phoneNumber");
        assertEquals(2, xpos.size());
        for (XPathObject o : xpos) {
            assertNotNull(o.getString("type"));
        }
    }

    @Test
    public void testCreation() {
        XPathObject xpo = XMLXPathObject.create("person");
        xpo.addString("name", "John Smith");

        xpo.add("address")
            .addString("city", "Newcastle")
            .addString("county", "Northumberland");

        xpo.addBigDecimal("biometrics/height", BigDecimal.valueOf(1.8));

        xpo.addArray("colours", Lists.newArrayList(
                XMLXPathObject.create("color")
                        .addString("name", "Red")
                        .addString("meaning", "Stop"),
                XMLXPathObject.create("color")
                        .addString("name", "Green")
                        .addString("meaning", "Go")));

        String generatedXML = XMLXPathObject.print(xpo);
        assertNotNull(generatedXML);
        assertContains(generatedXML, "encoding=\"UTF-8\"");
        assertContains(generatedXML, "<name>John Smith</name>");
        assertContains(generatedXML, "<city>Newcastle</city>");

        xpo = XMLXPathObject.parse(generatedXML, "person");
        assertNotNull(xpo);
        assertEquals(BigDecimal.valueOf(1.8), xpo.getBigDecimal("biometrics/height"));
        assertEquals("Newcastle", xpo.get("address").getString("city"));
        assertEquals("Red", xpo.getArray("colours/color").get(0).getString("name"));
    }
}