Overview
========

Cutlet is a "batteries included" library to make working directly with XML and JSON as data structures in Java simpler.

- Simple, concise, fluent, type-safe API  
- The same API for XML and JSON, as far as possible given their slightly different data models
- Reading/writing of XML and JSON from/to strings, streams and files
- Provides useful error messages
- Conversion of common data types (Strings, BigInteger, BigDecimal, LocalDate, DateTime, enums) supporting
most standard formats (eg ISO8601 dates) and edge cases (exponential notation for numbers) as well as microtypes
- Pluggable data type converters
- Uses [XPath](http://en.wikipedia.org/wiki/XPath) to select nodes in both XML and JSON
- Nested approach: Extract sub-documents and working with them in the same manner as full documents

Non goals:

- Mapping between classes and JSON/XML
- Performance and memory efficiency are secondary goals

Example
=======

Hello world in JSON:

````java
JSON.create().add("message").with("hello", "world")
````

creates: 

````json
{
  "message": {
    "hello": "world"
  }
}
````

Hello world in XML:

````java
XML.create("message").with("hello", "world")
````

creates: 

````xml
<message>
    <hello>world</hello>
</message>
````

More complex: Given an XML file containing a list of people and their home and mobile phone numbers
([input.xml](https://github.com/snellm/cutlet/blob/master/src/test/resources/com/snell/michael/cutlet/implementation/example/input.xml)), output a JSON associative 
array of their mobile phone numbers to names, changing the keys for "firstname" and "lastname" to "forename" and 
"surname" ([output.json](https://github.com/snellm/cutlet/blob/master/src/test/resources/com/snell/michael/cutlet/implementation/example/output.json)):

````java
JSON output = JSON.create();
XML.parseFile("input.xml").getList("person").forEach(person -> 
    output.add("mobile-" + person.getString("phonenumber[@type = 'mobile']"))
        .withString("forename", person.getString("firstname"))
        .withString("surname", person.getString("lastname")));
String json = output.write(PRETTY);
````

See the [JSON](https://github.com/snellm/cutlet/blob/master/src/test/java/com/snell/michael/cutlet/implementation/JSONTest.java) and 
[XML](https://github.com/snellm/cutlet/blob/master/src/test/java/com/snell/michael/cutlet/implementation/XMLTest.java) tests cases for more examples.

Downloading
===========

Current version is 0.4 - alpha quality code (API is subject to change).

Maven:

````xml
<dependency>
  <groupId>com.snell.michael.cutlet</groupId>
  <artifactId>cutlet</artifactId>
  <version>0.4</version>
</dependency>
````

Direct download: http://repo1.maven.org/maven2/com/snell/michael/cutlet/cutlet

Build status
============
[![Build Status](https://travis-ci.org/snellm/cutlet.svg?branch=master)](https://travis-ci.org/snellm/cutlet)

Fine print
==========
- Copyright 2015 Michael Snell
- Licensed under the MIT license - see [LICENSE](https://github.com/snellm/cutlet/blob/master/LICENSE)
- Things may break. Performance may suffer. Giant creatures may arise from the oceans and destroy your civilization.
