Cutlet
======

Cutlet is a library to make working with XML and JSON in Java faster and simpler,  particularly where a JavaBean JAXB-style approach is not warranted.

Example
-------

Combining first name, last name and mobile number into a string from a XML string with a root element "people" and multiple "person" subelements with multiple
"phonenumber" subelements with different types - see [people.xml](src/test/java/org/snellm/cutlet/people.xml):

    List<String> list = Lists.transform(XMLCutlet.parse(xmlString, "people").getArray("person"), new Function<Cutlet, String>() {
        public String apply(Cutlet input) {
                return input.getString("firstname") + " " + input.getString("lastname") + " " + input.getString("phonenumber[@type = 'mobile']");
        }
    });

See the [JSON](src/test/java/org/snellm/cutlet/JSONCutletTest.java) and [XML](src/test/java/org/snellm/cutlet/XMLCutletTest.java) tests cases for more examples.
