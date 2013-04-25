package org.snellm.cutlet;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.snellm.cutlet.TestUtil.assertContains;
import static org.snellm.cutlet.TestUtil.getFileResource;

public class JSONCutletTest {
    private Cutlet getTestJSONCutlet() {
        Cutlet cutlet = JSONCutlet.parse(getFileResource(getClass(), "test.json"));
        return cutlet.get("person");
    }

    @Test
    public void testParse() {
        Cutlet cutlet = getTestJSONCutlet();
        assertNotNull(cutlet);
    }

    @Test
    public void testErrorHandling() {
        Cutlet cutlet = getTestJSONCutlet();

        try {
            cutlet.getString("location/city");
            fail();
        } catch (CutletRuntimeException e) {
            // No op - expected for missing path
        }
    }

    @Test
    public void testStrings() {
        Cutlet cutlet = getTestJSONCutlet();

        // Can get a string using XPath
        assertEquals("New York", cutlet.getString("address/city"));

        // Or via nested gets
        Cutlet addressCutlet = cutlet.get("address");
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
    public void testNumbers() {
        Cutlet cutlet = getTestJSONCutlet();

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
    public void testArrays() {
        Cutlet cutlet = getTestJSONCutlet();

        // Can get a specific array entry directly using XPath
        assertEquals("home", cutlet.getString("phoneNumbers[1]/type"));

        // Get a specific entry based on an XPath selector
        assertEquals("212 555-1234", cutlet.getString("phoneNumbers[type = 'home']/number"));

        // Can get an array and extract values
        List<Cutlet> cutlets = cutlet.getArray("phoneNumbers");
        assertEquals(2, cutlets.size());
        for (Cutlet o : cutlets) {
            assertNotNull(o.getString("type"));
        }
    }

    @Test
    public void testCreation() {
        AbstractCutlet cutlet = JSONCutlet.create();
        cutlet.addString("name", "John Smith");

        cutlet.add("address")
                .addString("city", "Newcastle")
                .addString("county", "Northumberland");

        cutlet.addBigDecimal("biometrics/height", BigDecimal.valueOf(1.8));

        List<Cutlet> cutlets = new ArrayList<>();
        cutlets.add(JSONCutlet.create()
                .addString("name", "Red")
                .addString("meaning", "Stop"));
        cutlets.add(JSONCutlet.create()
                .addString("name", "Green")
                .addString("meaning", "Go"));
        cutlet.addArray("colours", cutlets);

        String s = JSONCutlet.print(cutlet);
        assertNotNull(s);
        assertContains(s, "\"John Smith\"");
        assertContains(s, "\"Newcastle\"");

        cutlet = JSONCutlet.parse(s);
        assertNotNull(cutlet);
        assertEquals(BigDecimal.valueOf(1.8), cutlet.getBigDecimal("biometrics/height"));
        assertEquals("Newcastle", cutlet.get("address").getString("city"));
        assertEquals("Red", cutlet.getArray("colours").get(0).getString("name"));
    }

    @Test
    public void testCanRemoveIndividualSectionsUsingXpath() {
        AbstractCutlet cutlet = JSONCutlet.create();
        cutlet.addBigDecimal("biometrics/height", BigDecimal.valueOf(1.8));
        cutlet.addBigDecimal("biometrics/weight", BigDecimal.valueOf(91.2));

        cutlet.removeAll("/biometrics/height");

        assertEquals("{\"biometrics\": {\"weight\": 91.2}}", JSONCutlet.print(cutlet));
    }

    @Test
    public void testCanRemoveSeveralSectionsUsingXpath() {
        AbstractCutlet cutlet = JSONCutlet.create();
        cutlet.addBigDecimal("biometrics/height", BigDecimal.valueOf(1.8));
        cutlet.addBigDecimal("biometrics/weight", BigDecimal.valueOf(91.2));

        cutlet.removeAll("/biometrics");

        assertEquals("{}", JSONCutlet.print(cutlet));
    }
}