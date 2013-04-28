// Copyright (c) 2013 Michael Snell - see https://github.com/snellm/cutlet

package com.snell.michael.cutlet.implementation;

import com.snell.michael.cutlet.CutletRuntimeException;
import com.snell.michael.cutlet.JSONCutlet;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static com.snell.michael.cutlet.WriteStyle.COMPACT;
import static com.snell.michael.cutlet.WriteStyle.PRETTY;
import static java.math.BigDecimal.TEN;
import static org.joda.time.DateTimeZone.UTC;
import static org.junit.Assert.*;

public class JSONCutletTest {
    @Test
    public void parseString() {
        JSONCutlet cutlet = getPersonJSONCutlet();
        assertNotNull(cutlet);
        assertEquals("John", cutlet.getString("person/firstName"));
    }

    @Test
    public void parseInputSteam() {
        JSONCutlet cutlet = JSONCutlet.parse(TestUtil.openResourceStream(getClass(), "person.json"));
        assertNotNull(cutlet);
        assertEquals("John", cutlet.getString("person/firstName"));
    }

    @Test
    public void errorHandling() {
        JSONCutlet cutlet = getPersonInPersonJSONCutlet();
        try {
            cutlet.getString("location/city");
            fail();
        } catch (CutletRuntimeException e) {
            // No op - expected for missing path
        }
    }

    @Test
    public void strings() {
        JSONCutlet cutlet = getPersonInPersonJSONCutlet();

        // Can get a string using XPath
        assertEquals("New York", cutlet.getString("address/city"));

        // Or via nested gets
        JSONCutlet addressCutlet = cutlet.get("address");
        assertEquals("New York", addressCutlet.getString("city"));
        assertEquals("NY", addressCutlet.getString("state"));

        // Can get an array of Strings
        List<String> strings = cutlet.getStringArray("favouriteColours");
        assertEquals(3, strings.size());
        for (String s: strings) {
            assertNotNull(s);
        }
    }

    @Test
    public void booleans() {
        JSONCutlet cutlet = getPersonInPersonJSONCutlet();

        assertEquals(true, cutlet.getBoolean("active"));

        cutlet.addBoolean("active", false);
        assertEquals("false", cutlet.getString("active"));
    }

    @Test
    public void localDates() {
        JSONCutlet cutlet = getPersonInPersonJSONCutlet();

        assertEquals(new LocalDate(1969, 2, 28), cutlet.getLocalDate("dateOfBirth"));

        cutlet.addLocalDate("dateOfDeath", new LocalDate(2013, 4, 29));
        assertEquals("2013-04-29", cutlet.getString("dateOfDeath"));
    }

    @Test
    public void dateTimes() {
        JSONCutlet cutlet = getPersonInPersonJSONCutlet();

        assertEquals(new DateTime(2012, 8, 7, 7, 47, 46, UTC), cutlet.getDateTime("lastModified"));

        DateTime now = new DateTime();
        cutlet.addDateTime("lastModified", now);
        assertEquals(now.toString(ISODateTimeFormat.dateTime()), cutlet.getString("lastModified"));
    }

    @Test
    public void decimals() {
        JSONCutlet cutlet = getPersonInPersonJSONCutlet();

        // Can get a integer as a BigDecimal using XPath
        assertEquals(BigDecimal.valueOf(1), cutlet.getBigDecimal("favouriteNumbers[1]"));

        // Can get a double as a BigDecimal using XPath
        assertEquals(BigDecimal.valueOf(3.141592), cutlet.getBigDecimal("favouriteNumbers[2]"));

        // Can get a negative number as a BigDecimal using XPath
        assertEquals(BigDecimal.valueOf(-42), cutlet.getBigDecimal("favouriteNumbers[3]"));

        // Can get a number expressed in scientific notation as a BigDecimal using XPath,
        // ensuring that the number is expressed in plain format
        assertEquals(BigDecimal.valueOf(10000000), cutlet.getBigDecimal("favouriteNumbers[4]"));

        // Can get an array of BigDecimals
        List<BigDecimal> numbers = cutlet.getBigDecimalArray("favouriteNumbers");
        assertEquals(4, numbers.size());
        for (BigDecimal b: numbers) {
            assertNotNull(b);
        }
    }


    @Test
    public void integers() {
        JSONCutlet cutlet = getPersonInPersonJSONCutlet();

        // Can get a integer as a BigDecimal using XPath
        assertEquals(BigInteger.valueOf(1), cutlet.getBigInteger("favouriteNumbers[1]"));

        // Attempting to get a double as a BigDecimal using XPath should fail
        try {
            cutlet.getBigInteger("favouriteNumbers[2]");
            fail();
        } catch (RuntimeException e) {
            // Expected
        }

        // Can get a negative number as a BigInteger using XPath
        assertEquals(BigInteger.valueOf(-42), cutlet.getBigInteger("favouriteNumbers[3]"));

        // Can get a number expressed in scientific notation as a BigDecimal using XPath,
        // ensuring that the number is expressed in plain format
        assertEquals(BigInteger.valueOf(10000000), cutlet.getBigInteger("favouriteNumbers[4]"));
    }

    @Test
    public void arrays() {
        JSONCutlet cutlet = getPersonInPersonJSONCutlet();

        // Can get a specific array entry directly using XPath
        assertEquals("home", cutlet.getString("phoneNumbers[1]/type"));

        // Get a specific entry based on an XPath selector
        assertEquals("212 555-1234", cutlet.getString("phoneNumbers[type = 'home']/number"));

        // Can get an array and extract values
        List<JSONCutlet> cutlets = cutlet.getArray("phoneNumbers");
        assertEquals(2, cutlets.size());
        for (JSONCutlet o : cutlets) {
            assertNotNull(o.getString("type"));
        }
    }

    @Test
    public void creation() {
        JSONCutlet cutlet = JSONCutlet.create();
        cutlet.addString("name", "John Smith");

        cutlet.add("address")
                .addString("city", "Newcastle")
                .addString("county", "Northumberland");

        cutlet.addBigDecimal("biometrics/height", BigDecimal.valueOf(1.8));

        List<JSONCutlet> cutlets = new ArrayList<>();
        cutlets.add(JSONCutlet.create()
                .addString("name", "Red")
                .addString("meaning", "Stop"));
        cutlets.add(JSONCutlet.create()
                .addString("name", "Green")
                .addString("meaning", "Go"));
        cutlet.addArray("colours", cutlets);

        String s = cutlet.write(PRETTY);
        assertNotNull(s);
        TestUtil.assertContains(s, "\"John Smith\"");
        TestUtil.assertContains(s, "\"Newcastle\"");

        cutlet = JSONCutlet.parse(s);
        assertNotNull(cutlet);
        assertEquals(BigDecimal.valueOf(1.8), cutlet.getBigDecimal("biometrics/height"));
        assertEquals("Newcastle", cutlet.get("address").getString("city"));
        assertEquals("Red", cutlet.getArray("colours").get(0).getString("name"));
    }

    @Test
    public void printing() {
        JSONCutlet cutlet = getPersonInPersonJSONCutlet();

        String compactString = cutlet.write(COMPACT);
        String prettyString = cutlet.write(PRETTY);

        JSONCutlet reparsedCompactCutlet = JSONCutlet.parse(compactString);
        assertEquals(cutlet, reparsedCompactCutlet);

        JSONCutlet reparsedPrettyCutlet = JSONCutlet.parse(prettyString);
        assertEquals(cutlet, reparsedPrettyCutlet);
    }

    @Test
    public void equalsAndHashCode() {
        JSONCutlet one = JSONCutlet.create();
        one.addString("bar/baz", "nop");
        one.addBigDecimal("baz/bar", TEN);

        JSONCutlet two = JSONCutlet.create();
        two.addBigDecimal("baz/bar", TEN);
        two.addString("bar/baz", "nop");

        assertEquals(one.hashCode(), two.hashCode());
        assertEquals(one, two);
    }

    @Test
    public void canRemoveIndividualSectionsUsingXpath() {
        JSONCutlet cutlet = JSONCutlet.create();
        cutlet.addBigDecimal("biometrics/height", BigDecimal.valueOf(1.8));
        cutlet.addBigDecimal("biometrics/weight", BigDecimal.valueOf(91.2));

        cutlet.remove("biometrics/height");

        assertEquals("{\"biometrics\":{\"weight\":91.2}}", cutlet.write(COMPACT));
    }

    @Test
    public void canRemoveSeveralSectionsUsingXpath() {
        JSONCutlet cutlet = JSONCutlet.create();
        cutlet.addBigDecimal("biometrics/height", BigDecimal.valueOf(1.8));
        cutlet.addBigDecimal("biometrics/weight", BigDecimal.valueOf(91.2));

        cutlet.remove("biometrics");

        assertEquals("{}", cutlet.write(COMPACT));
    }

    private JSONCutlet getPersonInPersonJSONCutlet() {
        JSONCutlet cutlet = getPersonJSONCutlet();
        return cutlet.get("person");
    }

    private JSONCutlet getPersonJSONCutlet() {
        return JSONCutlet.parse(TestUtil.readFileResource(getClass(), "person.json"));
    }
}