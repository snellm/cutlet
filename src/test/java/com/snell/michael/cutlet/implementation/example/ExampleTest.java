// Copyright (c) 2015 Michael Snell - see https://github.com/snellm/cutlet

package com.snell.michael.cutlet.implementation.example;

import com.snell.michael.cutlet.JSON;
import com.snell.michael.cutlet.XML;
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
    public void helloWorldJSON() {
        JSON json = JSON.create().add("message").with("hello", "world");
        //System.out.println(json);
    }

    @Test
    public void helloWorldXML() {
        XML xml = XML.create("message").with("hello", "world");
        //System.out.println(xml);
    }

    @Test
    public void example() {
        // Example starts
        JSON output = JSON.create();
        for (XML person : XML.parse(readFile("input.xml")).getList("person")) {
            output.add("mobile-" + person.getString("phonenumber[@type = 'mobile']"))
                .withString("forename", person.getString("firstname"))
                .withString("surname", person.getString("lastname"));
        }
        String json = output.write(PRETTY);
        // Example ends

        assertEquals(JSON.parse(readFileResource(getClass(), "output.json")), JSON.parse(json));
    }

    private InputStream readFile(String filename) {
        return openResourceStream(getClass(), filename);
    }
}
