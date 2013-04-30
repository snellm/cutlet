// Copyright (c) 2013 Michael Snell - see https://github.com/snellm/cutlet

package com.snell.michael.cutlet.implementation.example;

import com.snell.michael.cutlet.JSONCutlet;
import com.snell.michael.cutlet.XMLCutlet;
import org.junit.Test;

import java.io.InputStream;

import static com.snell.michael.cutlet.WriteStyle.PRETTY;
import static com.snell.michael.cutlet.implementation.TestUtil.openResourceStream;
import static com.snell.michael.cutlet.implementation.TestUtil.readFileResource;
import static org.junit.Assert.assertEquals;

/**
 * Example for Cutlet website
 */
public class ExampleTest {
    @Test
    public void example() {
        // Example starts
        XMLCutlet input = XMLCutlet.parse(readFile("input.xml"));
        JSONCutlet output = JSONCutlet.create();

        for (XMLCutlet person : input.getArray("person")) {
            output.add("mobile-" + person.getString("phonenumber[@type = 'mobile']"))
                .addString("forename", person.getString("firstname"))
                .addString("surname", person.getString("lastname"));
        }

        String json = output.write(PRETTY);
        // Example ends

        assertEquals(JSONCutlet.parse(readFileResource(getClass(), "output.json")), JSONCutlet.parse(json));
    }

    private InputStream readFile(String filename) {
        return openResourceStream(getClass(), filename);
    }
}