// Copyright (c) 2015 Michael Snell - see https://github.com/snellm/cutlet

package com.snell.michael.cutlet.implementation;

import com.snell.michael.cutlet.ConverterMap;
import com.snell.michael.cutlet.CutletRuntimeException;
import com.snell.michael.cutlet.JSON;
import com.snell.michael.cutlet.WriteStyle;
import com.snell.michael.cutlet.converters.Converter;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static com.snell.michael.cutlet.WriteStyle.COMPACT;
import static com.snell.michael.cutlet.WriteStyle.PRETTY;
import static java.math.BigDecimal.TEN;
import static org.joda.time.DateTimeZone.UTC;
import static org.junit.Assert.*;

public class JSONTest {
    private enum StateEnum {
        Ohio, NY
    }

    public static class StateMicrotype {
        private final String value;

        public StateMicrotype(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    @Test
    public void parseString() {
        JSON json = getPersonJSONCutlet();
        assertNotNull(json);
        assertEquals("John", json.getString("person/firstName"));
    }

    @Test
    public void parseInputSteam() {
        JSON json = JSON.parse(TestUtil.openResourceStream(getClass(), "person.json"));
        assertNotNull(json);
        assertEquals("John", json.getString("person/firstName"));
    }

    @Test
    public void writeFile() throws IOException {
        Currency usd = Currency.getInstance("USD");
        JSON json = JSON.create()
                .with("hello", "world")
                .with("currency", usd);

        File file = File.createTempFile("cutlet", "json");
        json.write(file, WriteStyle.COMPACT);

        json = JSON.parse(file);
        assertEquals("world", json.getString("hello"));
        assertEquals(usd, json.getCurrency("currency"));
    }

    @Test
    public void errorHandling() {
        JSON json = getPersonInPersonJSON();
        try {
            json.getString("location/city");
            fail();
        } catch (CutletRuntimeException e) {
            // No op - expected for missing path
        }
    }

    @Test
    public void exists() {
        JSON json = getPersonInPersonJSON();

        assertTrue(json.exists("address/city"));
        assertTrue(json.exists("active"));
        assertTrue(json.exists("opinion"));

        assertFalse(json.exists("address/country"));
        assertFalse(json.exists("sex"));
        assertFalse(json.exists("favouriteNumbers[5]"));
    }

    @Test
    public void has() {
        JSON json = getPersonInPersonJSON();

        assertFalse(json.has("opinion"));
    }

    @Test
    public void strings() {
        JSON json = getPersonInPersonJSON();

        // Can get a string using XPath
        assertEquals("New York", json.getString("address/city"));

        // Or via nested gets
        JSON addressCutlet = json.get("address");
        assertEquals("New York", addressCutlet.getString("city"));
        assertEquals("NY", addressCutlet.getString("state"));

        // Can get an array of Strings
        List<String> strings = json.getStringList("favouriteColours");
        assertEquals(3, strings.size());
        for (String s: strings) {
            assertNotNull(s);
        }

        // Can get a set of Strings
        Set<String> stringSet = json.getStringSet("favouriteColours");
        assertEquals(3, stringSet.size());
        for (String s: stringSet) {
            assertNotNull(s);
        }
    }

    @Test
    public void enums() {
        JSON json = getPersonInPersonJSON();

        assertEquals(StateEnum.NY, json.get("address/state", StateEnum.class));

        json.with("address/state", StateEnum.Ohio);

        assertEquals(StateEnum.Ohio, json.get("address/state", StateEnum.class));
    }

    @Test
    public void microtypes() {
        JSON json = getPersonInPersonJSON();

        assertEquals("NY", json.get("address/state", StateMicrotype.class).getValue());

        json.with("address/state", new StateMicrotype("Ohio"));

        assertEquals("Ohio", json.getString("address/state"));
    }

    @Test
    public void booleans() {
        JSON json = getPersonInPersonJSON();

        assertEquals(true, json.getBoolean("active"));

        json.withBoolean("active", false);
        assertEquals("false", json.getString("active"));
    }

    @Test
    public void localDates() {
        JSON json = getPersonInPersonJSON();

        assertEquals(new LocalDate(1969, 2, 28), json.getLocalDate("dateOfBirth"));

        json.withLocalDate("dateOfDeath", new LocalDate(2015, 4, 29));
        assertEquals("2015-04-29", json.getString("dateOfDeath"));
    }

    @Test
    public void dateTimes() {
        JSON json = getPersonInPersonJSON();

        assertEquals(new DateTime(2012, 8, 7, 7, 47, 46, UTC), json.getDateTime("lastModified"));

        DateTime now = new DateTime();
        json.withDateTime("lastModified", now);
        assertEquals(now.toString(ISODateTimeFormat.dateTime()), json.getString("lastModified"));
    }

    @Test
    public void decimals() {
        JSON json = getPersonInPersonJSON();

        // Can get a integer as a BigDecimal using XPath
        assertEquals(BigDecimal.valueOf(1), json.getBigDecimal("favouriteNumbers[1]"));

        // Can get a double as a BigDecimal using XPath
        assertEquals(BigDecimal.valueOf(3.141592), json.getBigDecimal("favouriteNumbers[2]"));

        // Can get a negative number as a BigDecimal using XPath
        assertEquals(BigDecimal.valueOf(-42), json.getBigDecimal("favouriteNumbers[3]"));

        // Can get a number expressed in scientific notation as a BigDecimal using XPath,
        // ensuring that the number is expressed in plain format
        assertEquals(BigDecimal.valueOf(10000000), json.getBigDecimal("favouriteNumbers[4]"));

        // Can get an array of BigDecimals
        List<BigDecimal> numbers = json.getBigDecimalList("favouriteNumbers");
        assertEquals(4, numbers.size());
        for (BigDecimal b: numbers) {
            assertNotNull(b);
        }
    }

    @Test
    public void integers() {
        JSON json = getPersonInPersonJSON();

        // Can get a integer as a BigDecimal using XPath
        assertEquals(BigInteger.valueOf(1), json.getBigInteger("favouriteNumbers[1]"));

        // Attempting to get a double as a BigDecimal using XPath should fail
        try {
            json.getBigInteger("favouriteNumbers[2]");
            fail();
        } catch (RuntimeException e) {
            // Expected
        }

        // Can get a negative number as a BigInteger using XPath
        assertEquals(BigInteger.valueOf(-42), json.getBigInteger("favouriteNumbers[3]"));

        // Can get a number expressed in scientific notation as a BigDecimal using XPath,
        // ensuring that the number is expressed in plain format
        assertEquals(BigInteger.valueOf(10000000), json.getBigInteger("favouriteNumbers[4]"));
    }

    @Test
    public void arrays() {
        JSON json = getPersonInPersonJSON();

        // Can get a specific array entry directly using XPath
        assertEquals("home", json.getString("phoneNumbers[1]/type"));

        // Get a specific entry based on an XPath selector
        assertEquals("212 555-1234", json.getString("phoneNumbers[type = 'home']/number"));

        // Can get an array and extract values
        List<JSON> jsons = json.getList("phoneNumbers");
        assertEquals(2, jsons.size());
        for (JSON o : jsons) {
            assertNotNull(o.getString("type"));
        }

        // Can add an array of strings
        json = JSON.create();
        json.withList("foo", newArrayList("One", "Two", "Three"), String.class);
        assertEquals(3, json.getStringList("foo").size());
    }

    @Test
    public void children() {
        JSON json = getPersonInPersonJSON();

        assertEquals(newHashSet("Albert", "Bethanie", "Charlie"), json.get("children").getChildren());
    }

    @Test
    public void creation() {
        JSON json = JSON.create();
        json.withString("name", "John Smith");

        json.add("address")
                .withString("city", "Newcastle")
                .withString("county", "Northumberland");

        json.withBigDecimal("biometrics/height", BigDecimal.valueOf(1.8));

        List<JSON> cutlets = new ArrayList<>();
        cutlets.add(JSON.create()
                .withString("name", "Red")
                .withString("meaning", "Stop"));
        cutlets.add(JSON.create()
                .withString("name", "Green")
                .withString("meaning", "Go"));
        json.withList("colours", cutlets);

        String s = json.write(PRETTY);
        assertNotNull(s);
        TestUtil.assertContains(s, "\"John Smith\"");
        TestUtil.assertContains(s, "\"Newcastle\"");

        json = JSON.parse(s);
        assertNotNull(json);
        assertEquals(BigDecimal.valueOf(1.8), json.getBigDecimal("biometrics/height"));
        assertEquals("Newcastle", json.get("address").getString("city"));
        assertEquals("Red", json.getList("colours").get(0).getString("name"));
    }

    @Test
    public void printing() {
        JSON json = getPersonInPersonJSON();

        String compactString = json.write(COMPACT);
        String prettyString = json.write(PRETTY);

        JSON reparsedCompactCutlet = JSON.parse(compactString);
        assertEquals(json, reparsedCompactCutlet);

        JSON reparsedPrettyCutlet = JSON.parse(prettyString);
        assertEquals(json, reparsedPrettyCutlet);
    }

    @Test
    public void customConverter() {
        JSON json = getPersonInPersonJSON().withConverterMap(
                ConverterMap.create().register(String.class, new Converter<String>() {
                    @Override
                    public String read(Object object) {
                        return StringUtils.reverse(object.toString());
                    }

                    @Override
                    public Object write(String str) {
                        return StringUtils.reverse(str);
                    }
                }));

        assertEquals("nhoJ", json.getString("firstName"));

        json.withString("lastName", "htimS");
        assertEquals("Smith", json.withConverterMap(ConverterMap.createWithDefaults()).getString("lastName"));
    }

    @Test
    public void equalsAndHashCode() {
        JSON one = JSON.create();
        one.withString("bar/baz", "nop");
        one.withBigDecimal("baz/bar", TEN);

        JSON two = JSON.create();
        two.withBigDecimal("baz/bar", TEN);
        two.withString("bar/baz", "nop");

        assertEquals(one.hashCode(), two.hashCode());
        assertEquals(one, two);
    }

    @Test
    public void canRemoveIndividualSectionsUsingXpath() {
        JSON json = JSON.create();
        json.withBigDecimal("biometrics/height", BigDecimal.valueOf(1.8));
        json.withBigDecimal("biometrics/weight", BigDecimal.valueOf(91.2));

        json.remove("biometrics/height");

        assertEquals("{\"biometrics\":{\"weight\":91.2}}", json.write(COMPACT));
    }

    @Test
    public void canRemoveSeveralSectionsUsingXpath() {
        JSON json = JSON.create();
        json.withBigDecimal("biometrics/height", BigDecimal.valueOf(1.8));
        json.withBigDecimal("biometrics/weight", BigDecimal.valueOf(91.2));

        json.remove("biometrics");

        assertEquals("{}", json.write(COMPACT));
    }

    private JSON getPersonInPersonJSON() {
        JSON json = getPersonJSONCutlet();
        return json.get("person");
    }

    private JSON getPersonJSONCutlet() {
        return JSON.parse(TestUtil.readFileResource(getClass(), "person.json"));
    }
}