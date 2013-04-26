Overview
========

Cutlet is a library to make working with XML and JSON in Java faster and simpler,  particularly where a JavaBean JAXB-style approach is not warranted.

Cutlet supports:
- The same interface for XML and JSON (with minor differences to account for the different capabilities of these formats)
- Both reading and writing
- Using [XPath](http://en.wikipedia.org/wiki/XPath) to select nodes
- A nested approach, ie extracting sub-documents and working with them in the same manner as full documents

Example
=======

Given an XML file containing a list of people and their home and mobile phone numbers ([input.xml](src/test/resources/org/snellm/cutlet/example/input.xml)), output a JSON associative array of their mobile phone numbers to names, changing the keys for first and last name ([output.json](src/test/resources/org/snellm/cutlet/example/output.json)):

````java
Cutlet input = XMLCutlet.parse(readFile("input.xml"), "people");
Cutlet output = JSONCutlet.create();

for (Cutlet person : input.getArray("person")) {
    output.add("mobile-" + person.getString("phonenumber[@type = 'mobile']"))
        .addString("forename", person.getString("firstname"))
        .addString("surname", person.getString("lastname"));
}

String json = JSONCutlet.print(output);
````    

See the [JSON](src/test/java/org/snellm/cutlet/JSONCutletTest.java) and [XML](src/test/java/org/snellm/cutlet/XMLCutletTest.java) tests cases for more examples.
