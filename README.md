Overview
========

Cutlet is a library to make working with XML and JSON simple, particularly where a JavaBean
JAXB-style approach is too heavyweight.

Cutlet supports:
- The same interface for XML and JSON, as far as possible given the different capabilities
- Reading and writing of both XML and JSON
- Using [XPath](http://en.wikipedia.org/wiki/XPath) to select nodes
- A nested approach, ie extracting sub-documents and working with them in the same manner as full documents

Example
=======

Given an XML file containing a list of people and their home and mobile phone numbers
([input.xml](src/test/resources/com/snell/michael/cutlet/example/input.xml)), output a JSON associative array of their mobile
phone numbers to names, changing the keys for "firstname" and "lastname" to "forename" and "surname"
([output.json](src/test/resources/com/snell/michael/cutlet/example/output.json)):

````java
XMLCutlet input = XMLCutlet.parse(new File("input.xml"));
JSONCutlet output = JSONCutlet.create();

for (XMLCutlet person : input.getArray("person")) {
    output.add("mobile-" + person.getString("phonenumber[@type = 'mobile']"))
        .addString("forename", person.getString("firstname"))
        .addString("surname", person.getString("lastname"));
}

String json = output.prettyPrint();
````

See the [JSON](src/test/java/com/snell/michael/cutlet/JSONCutletTest.java) and [XML](src/test/java/com/snell/michael/cutlet/XMLCutletTest.java) tests cases for more examples.

Downloading
===========

Cutlet has not yet been officially released, but you can download a development snapshot from https://oss.sonatype.org/content/repositories/snapshots/com/snell/michael/cutlet/cutlet,
or use Maven or similar tools:

````xml
<repositories>
  <repository>
    <id>sonatype-oss-public</id>
    <url>https://oss.sonatype.org/content/groups/public/</url>
    <releases>
      <enabled>true</enabled>
    </releases>
    <snapshots>
      <enabled>true</enabled>
    </snapshots>
  </repository>
</repositories>

<dependency>
  <groupId>com.snell.michael.cutlet</groupId>
  <artifactId>cutlet</artifactId>
  <version>0.1-SNAPSHOT</version>
</dependency>
````

Fine print
==========
- Copyright 2013 Michael Snell
- Licensed under the MIT license - see [LICENSE](LICENSE)
- Things may break. Performance may suffer. Giant creatures may arise from the oceans and destroy your civilization.
