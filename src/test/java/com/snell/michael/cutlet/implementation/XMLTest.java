// Copyright (c) 2015 Michael Snell - see https://github.com/snellm/cutlet

package com.snell.michael.cutlet.implementation;

import com.google.common.collect.Lists;
import com.snell.michael.cutlet.CutletRuntimeException;
import com.snell.michael.cutlet.XML;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import static com.google.common.collect.Sets.newHashSet;
import static com.snell.michael.cutlet.WriteStyle.COMPACT;
import static com.snell.michael.cutlet.WriteStyle.PRETTY;
import static com.snell.michael.cutlet.implementation.TestUtil.assertContains;
import static java.math.BigDecimal.TEN;
import static org.joda.time.DateTimeZone.UTC;
import static org.junit.Assert.*;

public class XMLTest {
    @Test
    public void parseString() {
        XML xml = XML.parse(TestUtil.readFileResource(getClass(), "person.xml"));
        assertNotNull(xml);
        assertEquals("John", xml.getString("firstName"));
    }

    @Test
    public void parseInputSteam() {
        XML xml = XML.parse(TestUtil.openResourceStream(getClass(), "person.xml"));
        assertNotNull(xml);
        assertEquals("John", xml.getString("firstName"));
    }
    @Test
    public void errorHandling() {
        XML xml = getPersonXMLCutlet();

        try {
            xml.getString("location/city");
            fail();
        } catch (CutletRuntimeException e) {
            // No op - expected for missing path
        }
    }

    @Test
    public void strings() {
        XML xml = getPersonXMLCutlet();

        // Can get a string using XPath
        assertEquals("New York", xml.getString("address/city"));

        // Or via nested gets
        XML addressCutlets = xml.get("address");
        assertEquals("New York", addressCutlets.getString("city"));
        assertEquals("NY", addressCutlets.getString("state"));

        // Can get an array of Strings
        List<String> strings = xml.getStringList("favouriteColour");
        assertEquals(3, strings.size());
        for (String s : strings) {
            assertNotNull(s);
        }
    }

    @Test
    public void booleans() {
        XML xml = getPersonXMLCutlet();

        assertEquals(true, xml.getBoolean("active"));

        xml.withBoolean("active", false);
        assertEquals("false", xml.getString("active"));
    }

    @Test
    public void localDates() {
        XML xml = getPersonXMLCutlet();

        assertEquals(new LocalDate(1969, 2, 28), xml.getLocalDate("dateOfBirth"));

        xml.withLocalDate("dateOfDeath", new LocalDate(2015, 4, 29));
        assertEquals("2015-04-29", xml.getString("dateOfDeath"));
    }

    @Test
    public void dateTimes() {
        XML xml = getPersonXMLCutlet();

        assertEquals(new DateTime(2012, 8, 7, 7, 47, 46, UTC), xml.getDateTime("lastModified"));

        DateTime now = new DateTime();
        xml.withDateTime("lastModified", now);
        assertEquals(now.toString(ISODateTimeFormat.dateTime()), xml.getString("lastModified"));
    }


    @Test
    public void decimals() {
        XML xml = getPersonXMLCutlet();

        // Can get a integer as a BigDecimal using XPath
        assertEquals(BigDecimal.valueOf(1), xml.getBigDecimal("favouriteNumber[1]"));

        // Can get a double as a BigDecimal using XPath
        assertEquals(BigDecimal.valueOf(3.141592), xml.getBigDecimal("favouriteNumber[2]"));

        // Can get a negative number as a BigDecimal using XPath
        assertEquals(BigDecimal.valueOf(-42), xml.getBigDecimal("favouriteNumber[3]"));

        // Can get a number expressed in scientific notation as a BigDecimal using XPath,
        // ensuring that the number is expressed in plain format
        assertEquals(BigDecimal.valueOf(10000000), xml.getBigDecimal("favouriteNumber[4]"));

        // Can get an array of BigDecimals
        List<BigDecimal> numbers = xml.getBigDecimalList("favouriteNumber");
        assertEquals(4, numbers.size());
        for (BigDecimal b : numbers) {
            assertNotNull(b);
        }
    }

    @Test
    public void integers() {
        XML xml = getPersonXMLCutlet();

        // Can get a integer as a BigDecimal using XPath
        assertEquals(BigInteger.valueOf(1), xml.getBigInteger("favouriteNumber[1]"));

        // Attempting to get a double as a BigDecimal using XPath should fail
        try {
            xml.getBigInteger("favouriteNumber[2]");
            fail();
        } catch (RuntimeException e) {
            // Expected
        }

        // Can get a negative number as a BigInteger using XPath
        assertEquals(BigInteger.valueOf(-42), xml.getBigInteger("favouriteNumber[3]"));

        // Can get a number expressed in scientific notation as a BigDecimal using XPath,
        // ensuring that the number is expressed in plain format
        assertEquals(BigInteger.valueOf(10000000), xml.getBigInteger("favouriteNumber[4]"));
    }

    @Test
    public void arrays() {
        XML xml = getPersonXMLCutlet();

        // Can get a specific array entry directly using XPath
        assertEquals("home", xml.getString("phoneNumber[1]/type"));

        // Get a specific entry based on an XPath selector
        assertEquals("212 555-1234", xml.getString("phoneNumber[type = 'home']/number"));

        // Can get an array and extract values
        List<XML> cutlets = xml.getList("phoneNumber");
        assertEquals(2, cutlets.size());
        for (XML phoneNumberCutlet : cutlets) {
            assertNotNull(phoneNumberCutlet.getString("type"));
        }
    }

    @Test
    public void children() {
        XML xml = getPersonXMLCutlet();

        assertEquals(newHashSet("Albert", "Bethanie", "Charlie"), xml.get("children").getChildren());
    }

    @Test
    public void creation() {
        XML xml = XML.create("person");
        xml.withString("name", "John Smith");

        xml.add("address")
            .withString("city", "Newcastle")
            .withString("county", "Northumberland");

        xml.withBigDecimal("biometrics/height", BigDecimal.valueOf(1.8));

        xml.withList("colours", Lists.newArrayList(
                XML.create("color")
                        .withString("name", "Red")
                        .withString("meaning", "Stop"),
                XML.create("color")
                        .withString("name", "Green")
                        .withString("meaning", "Go")));

        String generatedXML = xml.write(PRETTY);
        assertNotNull(generatedXML);
        assertContains(generatedXML, "encoding=\"UTF-8\"");
        assertContains(generatedXML, "<name>John Smith</name>");
        assertContains(generatedXML, "<city>Newcastle</city>");

        xml = XML.parse(generatedXML);
        assertNotNull(xml);
        assertEquals(BigDecimal.valueOf(1.8), xml.getBigDecimal("biometrics/height"));
        assertEquals("Newcastle", xml.get("address").getString("city"));
        assertEquals("Red", xml.getList("colours/color").get(0).getString("name"));
    }


    @Test
    public void printing() {
        XML xml = getPersonXMLCutlet();

        String compactString = xml.write(COMPACT);
        String prettyString = xml.write(PRETTY);

        XML reparsedCompactCutlet = XML.parse(compactString);
        assertEquals(xml, reparsedCompactCutlet);

        XML reparsedPrettyCutlet = XML.parse(prettyString);
        assertEquals(xml, reparsedPrettyCutlet);
    }

    @Test
    public void equalsAndHashCode() {
        XML one = XML.create("foo");
        one.withString("bar/baz", "nop");
        one.withBigDecimal("baz/bar", TEN);

        XML two = XML.create("foo");
        two.withString("bar/baz", "nop");
        two.withBigDecimal("baz/bar", TEN);

        assertEquals(one.hashCode(), two.hashCode());
        assertEquals(one, two);
    }

    private XML getPersonXMLCutlet() {
        return XML.parse(TestUtil.readFileResource(getClass(), "person.xml"));
    }
}