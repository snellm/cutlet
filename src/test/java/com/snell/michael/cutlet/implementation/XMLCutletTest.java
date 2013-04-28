// Copyright (c) 2013 Michael Snell - see https://github.com/snellm/cutlet

package com.snell.michael.cutlet.implementation;

import com.google.common.collect.Lists;
import com.snell.michael.cutlet.CutletRuntimeException;
import com.snell.michael.cutlet.XMLCutlet;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import static com.snell.michael.cutlet.WriteStyle.COMPACT;
import static com.snell.michael.cutlet.WriteStyle.PRETTY;
import static com.snell.michael.cutlet.implementation.TestUtil.assertContains;
import static java.math.BigDecimal.TEN;
import static org.joda.time.DateTimeZone.UTC;
import static org.junit.Assert.*;

public class XMLCutletTest {
    @Test
    public void parseString() {
        XMLCutlet cutlet = XMLCutlet.parse(TestUtil.readFileResource(getClass(), "person.xml"));
        assertNotNull(cutlet);
        assertEquals("John", cutlet.getString("firstName"));
    }

    @Test
    public void parseInputSteam() {
        XMLCutlet cutlet = XMLCutlet.parse(TestUtil.openResourceStream(getClass(), "person.xml"));
        assertNotNull(cutlet);
        assertEquals("John", cutlet.getString("firstName"));
    }
    @Test
    public void errorHandling() {
        XMLCutlet cutlet = getPersonXMLCutlet();

        try {
            cutlet.getString("location/city");
            fail();
        } catch (CutletRuntimeException e) {
            // No op - expected for missing path
        }
    }

    @Test
    public void strings() {
        XMLCutlet cutlet = getPersonXMLCutlet();

        // Can get a string using XPath
        assertEquals("New York", cutlet.getString("address/city"));

        // Or via nested gets
        XMLCutlet addressCutlets = cutlet.get("address");
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
    public void booleans() {
        XMLCutlet cutlet = getPersonXMLCutlet();

        assertEquals(true, cutlet.getBoolean("active"));

        cutlet.addBoolean("active", false);
        assertEquals("false", cutlet.getString("active"));
    }

    @Test
    public void localDates() {
        XMLCutlet cutlet = getPersonXMLCutlet();

        assertEquals(new LocalDate(1969, 2, 28), cutlet.getLocalDate("dateOfBirth"));

        cutlet.addLocalDate("dateOfDeath", new LocalDate(2013, 4, 29));
        assertEquals("2013-04-29", cutlet.getString("dateOfDeath"));
    }

    @Test
    public void dateTimes() {
        XMLCutlet cutlet = getPersonXMLCutlet();

        assertEquals(new DateTime(2012, 8, 7, 7, 47, 46, UTC), cutlet.getDateTime("lastModified"));

        DateTime now = new DateTime();
        cutlet.addDateTime("lastModified", now);
        assertEquals(now.toString(ISODateTimeFormat.dateTime()), cutlet.getString("lastModified"));
    }


    @Test
    public void decimals() {
        XMLCutlet cutlet = getPersonXMLCutlet();

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
    public void integers() {
        XMLCutlet cutlet = getPersonXMLCutlet();

        // Can get a integer as a BigDecimal using XPath
        assertEquals(BigInteger.valueOf(1), cutlet.getBigInteger("favouriteNumber[1]"));

        // Attempting to get a double as a BigDecimal using XPath should fail
        try {
            cutlet.getBigInteger("favouriteNumber[2]");
            fail();
        } catch (RuntimeException e) {
            // Expected
        }

        // Can get a negative number as a BigInteger using XPath
        assertEquals(BigInteger.valueOf(-42), cutlet.getBigInteger("favouriteNumber[3]"));

        // Can get a number expressed in scientific notation as a BigDecimal using XPath,
        // ensuring that the number is expressed in plain format
        assertEquals(BigInteger.valueOf(10000000), cutlet.getBigInteger("favouriteNumber[4]"));
    }

    @Test
    public void arrays() {
        XMLCutlet cutlet = getPersonXMLCutlet();

        // Can get a specific array entry directly using XPath
        assertEquals("home", cutlet.getString("phoneNumber[1]/type"));

        // Get a specific entry based on an XPath selector
        assertEquals("212 555-1234", cutlet.getString("phoneNumber[type = 'home']/number"));

        // Can get an array and extract values
        List<XMLCutlet> cutlets = cutlet.getArray("phoneNumber");
        assertEquals(2, cutlets.size());
        for (XMLCutlet phoneNumberCutlet : cutlets) {
            assertNotNull(phoneNumberCutlet.getString("type"));
        }
    }

    @Test
    public void creation() {
        XMLCutlet cutlet = XMLCutlet.create("person");
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

        String generatedXML = cutlet.write(PRETTY);
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
    public void printing() {
        XMLCutlet cutlet = getPersonXMLCutlet();

        String compactString = cutlet.write(COMPACT);
        String prettyString = cutlet.write(PRETTY);

        XMLCutlet reparsedCompactCutlet = XMLCutlet.parse(compactString);
        assertEquals(cutlet, reparsedCompactCutlet);

        XMLCutlet reparsedPrettyCutlet = XMLCutlet.parse(prettyString);
        assertEquals(cutlet, reparsedPrettyCutlet);
    }
    @Test
    public void equalsAndHashCode() {
        XMLCutlet one = XMLCutlet.create("foo");
        one.addString("bar/baz", "nop");
        one.addBigDecimal("baz/bar", TEN);

        XMLCutlet two = XMLCutlet.create("foo");
        two.addString("bar/baz", "nop");
        two.addBigDecimal("baz/bar", TEN);

        assertEquals(one.hashCode(), two.hashCode());
        assertEquals(one, two);
    }

    private XMLCutlet getPersonXMLCutlet() {
        return XMLCutlet.parse(TestUtil.readFileResource(getClass(), "person.xml"));
    }
}