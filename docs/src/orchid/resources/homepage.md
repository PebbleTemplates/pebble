---
---

Pebble is a Java templating engine inspired by <a href="https://twig.symfony.com/">Twig</a> and similar to the Python [Jinja Template Engine](https://palletsprojects.com/p/jinja/) syntax. It features templates inheritance and easy-to-read syntax, ships with built-in autoescaping for security, and includes integrated support for internationalization.

## Features

* **Rich set of built-in tags and filters**
* **Template inheritance**: extract common areas of your content in a single ‘layout’ and make
your templates inherit this layout.
* **Extensible language**: new tags, filters and functions can be added to Pebble very easily.

If you already know Twig, you can compare both engines in {{ anchor('the compatibility matrix', 'Twig Compatibility') }}.

## Basic Usage
First, add the following dependency to your pom.xml:
```xml
<dependency>
	<groupId>io.pebbletemplates</groupId>
	<artifactId>pebble</artifactId>
	<version>{{ site.version }}</version>
</dependency>
```

Then create a template in your WEB-INF folder. Let's start with a base template that all
other templates will inherit from, name it "base.html":
```twig
{% verbatim %}
<html>
<head>
	<title>{% block title %}My Website{% endblock %}</title>
</head>
<body>
	<div id="content">
		{% block content %}{% endblock %}
	</div>
	<div id="footer">
		{% block footer %}
			Copyright 2018
		{% endblock %}
	</div>
</body>
</html>
{% endverbatim %}
```
Then create a template that extends base.html, call it "home.html":
```twig
{% verbatim %}
{% extends "base.html" %}

{% block title %} Home {% endblock %}

{% block content %}
	<h1> Home </h1>
	<p> Welcome to my home page. My name is {{ name }}.</p>
{% endblock %}
{% endverbatim %}
```
Now we want to compile the template, and render it:
```java
PebbleEngine engine = new PebbleEngine.Builder().build();
PebbleTemplate compiledTemplate = engine.getTemplate("home.html");

Map<String, Object> context = new HashMap<>();
context.put("name", "Mitchell");

Writer writer = new StringWriter();
compiledTemplate.evaluate(writer, context);

String output = writer.toString();
```
The output should result in the following:
```twig
<html>
<head>
	<title> Home </title>
</head>
<body>
	<div id="content">
		<h1> Home </h1>
	    <p> Welcome to my home page. My name is Mitchell.</p>
	</div>
	<div id="footer">
		Copyright 2018
	</div>
</body>
</html>
```

For more information on installation and configuration, see {{ anchor('the installation guide', 'Installation and Configuration') }}.<br/>
For more information on basic usage, see {{ anchor('the basic usage guide', 'Basic Usage') }}.<br/>
For Spring Boot integration, see {{ anchor('the Spring Boot integration guide', 'Spring Boot Integration') }}.