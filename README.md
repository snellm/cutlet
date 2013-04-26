Overview
========

Cutlet is a library to make working with XML and JSON simple, particularly where a JavaBean
JAXB-style approach is too heavyweight.

Cutlet supports:
- The same interface for XML and JSON (with minor differences to account for the differences between these)
- Both reading and writing of XML and JSON
- Using [XPath](http://en.wikipedia.org/wiki/XPath) to select nodes
- A nested approach, ie extracting sub-documents and working with them in the same manner as full documents

Example
=======

Given an XML file containing a list of people and their home and mobile phone numbers
([input.xml](src/test/resources/com/snell/michael/cutlet/example/input.xml)), output a JSON associative array of their mobile
phone numbers to names, changing the keys for first and last name
([output.json](src/test/resources/com/snell/michael/cutlet/example/output.json)):

````java
Cutlet input = XMLCutlet.parse(readFile("input.xml"));
Cutlet output = JSONCutlet.create();

for (Cutlet person : input.getArray("person")) {
    output.add("mobile-" + person.getString("phonenumber[@type = 'mobile']"))
        .addString("forename", person.getString("firstname"))
        .addString("surname", person.getString("lastname"));
}

String json = output.printPretty();
````    

See the [JSON](src/test/java/com/snell/michael/cutlet/JSONCutletTest.java) and [XML](src/test/java/com/snell/michael/cutlet/XMLCutletTest.java) tests cases for more examples.
