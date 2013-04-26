package com.snell.michael.cutlet;

import com.google.common.collect.Lists;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;

import static com.snell.michael.cutlet.TestUtil.assertContains;
import static java.math.BigDecimal.TEN;
import static org.junit.Assert.*;

public class XMLCutletTest {
    @Test
    public void parse() {
        Cutlet cutlet = getPersonXMLCutlet();
        assertNotNull(cutlet);
    }

    @Test
    public void errorHandling() {
        Cutlet cutlet = getPersonXMLCutlet();

        try {
            cutlet.getString("location/city");
            fail();
        } catch (CutletRuntimeException e) {
            // No op - expected for missing path
        }
    }

    @Test
    public void strings() {
        Cutlet cutlet = getPersonXMLCutlet();

        // Can get a string using XPath
        assertEquals("New York", cutlet.getString("address/city"));

        // Or via nested gets
        Cutlet addressCutlets = cutlet.get("address");
        assertEquals("New York", addressCutlets.getString("city"));
        assertEquals("NY", addressCutlets.getString("state"));

        // Can get an array of Strings
        List<String> strings = cutlet.getStringArray("favouriteColour");
        assertEquals(3, strings.size());
        for (String s : strings) {
            assertNotNull(s);
        }
    }

    @Test
    public void numbers() {
        Cutlet cutlet = getPersonXMLCutlet();

        // Can get a integer as a BigDecimal using XPath
        assertEquals(BigDecimal.valueOf(1), cutlet.getBigDecimal("favouriteNumber[1]"));

        // Can get a double as a BigDecimal using XPath
        assertEquals(BigDecimal.valueOf(3.141592), cutlet.getBigDecimal("favouriteNumber[2]"));

        // Can get a negative number as a BigDecimal using XPath
        assertEquals(BigDecimal.valueOf(-42), cutlet.getBigDecimal("favouriteNumber[3]"));

        // Can get a number expressed in scientific notation as a BigDecimal using XPath,
        // ensuring that the number is expressed in plain format
        assertEquals(BigDecimal.valueOf(10000000), cutlet.getBigDecimal("favouriteNumber[4]"));

        // Can get an array of BigDecimals
        List<BigDecimal> numbers = cutlet.getBigDecimalArray("favouriteNumber");
        assertEquals(4, numbers.size());
        for (BigDecimal b : numbers) {
            assertNotNull(b);
        }
    }

    @Test
    public void arrays() {
        Cutlet cutlet = getPersonXMLCutlet();

        // Can get a specific array entry directly using XPath
        assertEquals("home", cutlet.getString("phoneNumber[1]/type"));

        // Get a specific entry based on an XPath selector
        assertEquals("212 555-1234", cutlet.getString("phoneNumber[type = 'home']/number"));

        // Can get an array and extract values
        List<Cutlet> cutlets = cutlet.getArray("phoneNumber");
        assertEquals(2, cutlets.size());
        for (Cutlet phoneNumberCutlet : cutlets) {
            assertNotNull(phoneNumberCutlet.getString("type"));
        }
    }

    @Test
    public void creation() {
        Cutlet cutlet = XMLCutlet.create("person");
        cutlet.addString("name", "John Smith");

        cutlet.add("address")
            .addString("city", "Newcastle")
            .addString("county", "Northumberland");

        cutlet.addBigDecimal("biometrics/height", BigDecimal.valueOf(1.8));

        cutlet.addArray("colours", Lists.newArrayList(
                XMLCutlet.create("color")
                        .addString("name", "Red")
                        .addString("meaning", "Stop"),
                XMLCutlet.create("color")
                        .addString("name", "Green")
                        .addString("meaning", "Go")));

        String generatedXML = XMLCutlet.print(cutlet);
        assertNotNull(generatedXML);
        assertContains(generatedXML, "encoding=\"UTF-8\"");
        assertContains(generatedXML, "<name>John Smith</name>");
        assertContains(generatedXML, "<city>Newcastle</city>");

        cutlet = XMLCutlet.parse(generatedXML);
        assertNotNull(cutlet);
        assertEquals(BigDecimal.valueOf(1.8), cutlet.getBigDecimal("biometrics/height"));
        assertEquals("Newcastle", cutlet.get("address").getString("city"));
        assertEquals("Red", cutlet.getArray("colours/color").get(0).getString("name"));
    }

    @Test
    public void equalsAndHashCode() {
        Cutlet one = XMLCutlet.create("foo");
        one.addString("bar/baz", "nop");
        one.addBigDecimal("baz/bar", TEN);

        Cutlet two = XMLCutlet.create("foo");
        two.addString("bar/baz", "nop");
        two.addBigDecimal("baz/bar", TEN);

        assertEquals(one.hashCode(), two.hashCode());
        assertEquals(one, two);
    }

    private Cutlet getPersonXMLCutlet() {
        return XMLCutlet.parse(TestUtil.readFileResource(getClass(), "person.xml"));
    }
}