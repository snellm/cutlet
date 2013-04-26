package com.snell.michael.cutlet.example;

import com.snell.michael.cutlet.Cutlet;
import com.snell.michael.cutlet.JSONCutlet;
import com.snell.michael.cutlet.XMLCutlet;
import org.junit.Test;

import static com.snell.michael.cutlet.TestUtil.openResourceStream;
import static com.snell.michael.cutlet.TestUtil.readFileResource;
import static org.junit.Assert.assertEquals;

public class ExampleTest {
    @Test
    public void example() {
        Cutlet input = XMLCutlet.parse(openResourceStream(getClass(), "input.xml"));
        Cutlet output = JSONCutlet.create();

        for (Cutlet person : input.getArray("person")) {
            // TODO Remove "mobile" and allow for spaces
            output.add("mobile-" + person.getString("phonenumber[@type = 'mobile']"))
                .addString("forename", person.getString("firstname"))
                .addString("surname", person.getString("lastname"));
        }

        assertEquals(JSONCutlet.parse(readFileResource(getClass(), "output.json")), output);
    }
}
