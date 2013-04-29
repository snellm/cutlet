Overview
========

Cutlet is a "batteries included" library to make working with XML and JSON in Java simpler.

- The same interface for XML and JSON, as far as possible given their different capabilities
- Reading/writing of XML and JSON from/to strings, streams and files
- Fails fast and provides useful error messages
- Conversion of common data types (Strings, BigInteger, BigDecimal, LocalDate, DateTime) supporting
most standard formats (eg ISO8601 dates) and edge cases (exponential notation for numbers)
- Pluggable data type converters
- Uses [XPath](http://en.wikipedia.org/wiki/XPath) to select nodes
- A nested approach, ie extracting sub-documents and working with them in the same manner as full documents

Example
=======

Given an XML file containing a list of people and their home and mobile phone numbers
([input.xml](https://github.com/snellm/cutlet/blob/master/src/test/resources/com/snell/michael/cutlet/implementation/example/input.xml)), output a JSON associative 
array of their mobile phone numbers to names, changing the keys for "firstname" and "lastname" to "forename" and 
"surname" ([output.json](https://github.com/snellm/cutlet/blob/master/src/test/resources/com/snell/michael/cutlet/implementation/example/output.json)):

````java
XMLCutlet input = XMLCutlet.parse(new File("input.xml"));
JSONCutlet output = JSONCutlet.create();

for (XMLCutlet person : input.getArray("person")) {
    output.add("mobile-" + person.getString("phonenumber[@type = 'mobile']"))
        .addString("forename", person.getString("firstname"))
        .addString("surname", person.getString("lastname"));
}

String json = output.write(PRETTY);
````

See the [JSON](https://github.com/snellm/cutlet/blob/master/src/test/java/com/snell/michael/cutlet/implementation/JSONCutletTest.java) and 
[XML](https://github.com/snellm/cutlet/blob/master/src/test/java/com/snell/michael/cutlet/implementation/XMLCutletTest.java) tests cases for more examples.

Downloading
===========

Current version is 0.3 - alpha quality code (API is subject to change).

Maven:

````xml
<dependency>
  <groupId>com.snell.michael.cutlet</groupId>
  <artifactId>cutlet</artifactId>
  <version>0.3</version>
</dependency>
````

Direct download: http://repo1.maven.org/maven2/com/snell/michael/cutlet/cutlet

Fine print
==========
- Copyright 2013 Michael Snell
- Licensed under the MIT license - see [LICENSE](https://github.com/snellm/cutlet/blob/master/LICENSE)
- Things may break. Performance may suffer. Giant creatures may arise from the oceans and destroy your civilization.
