package org.snellm.cutlet.example;

import org.junit.Test;
import org.snellm.cutlet.Cutlet;
import org.snellm.cutlet.JSONCutlet;
import org.snellm.cutlet.XMLCutlet;

import static org.junit.Assert.assertEquals;
import static org.snellm.cutlet.TestUtil.readFileResource;

public class ExampleTest {
    @Test
    public void example() {
        Cutlet input = XMLCutlet.parse(readFileResource(getClass(), "input.xml"));
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
