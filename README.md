#Pebble

Pebble is a java templating engine inspired by [Twig](http://twig.sensiolabs.org/). Its biggest feature is template inheritance which enables multiple templates to easily share common code. For more information please visit the [official website](http://www.mitchellbosecke.com/pebble).

## Basic Usage
For more information on installation and configuration, see [the installation guide](http://www.mitchellbosecke.com/pebble/documentation/guide/installation).
For more information on basic usage, see [the basic usage guide](http://www.mitchellbosecke.com/pebble/documentation/guide/basic-usage).

First, add the following dependency to your pom.xml:
```XML
<dependency>
	<groupId>com.mitchellbosecke</groupId>
	<artifactId>pebble</artifactId>
	<version>2.3.0<version>
</dependency>
```

Then create a template in your WEB-INF folder. Let's start with a base template that all
other templates will inherit from, name it "base.html":

```HTML+Django
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
			Copyright 2014
		{% endblock %}
	</div>
</body>
</html>
```
Then create a template that extends base.html, call it "home.html":
```HTML+Django
{% extends "base.html" %}

{% block title %} Home {% endblock %}

{% block content %}
	<h1> Home </h1>
	<p> Welcome to my home page. My name is {{ name }}.</p>
{% endblock %}
```
Now we want to compile the template, and render it:
```JAVA
PebbleEngine engine = new PebbleEngine.Builder().build();
PebbleTemplate compiledTemplate = engine.getTemplate("home.html");

Map<String, Object> context = new HashMap<>();
context.put("name", "Mitchell");

Writer writer = new StringWriter();
compiledTemplate.evaluate(writer, context);

String output = writer.toString();
```
The output should result in the following:
```HTML
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
		Copyright 2014
	</div>
</body>
</html>
```

## Contributing
Currently looking for contributors! In particular we need help with new feature ideas, bug reports/fixes, API feedback, performance optimization, and IDE integration. Just one little commit and you'll be forever listed on the [contributors page](http://www.mitchellbosecke.com/pebble/contributing). No contribution is too small and contributing is super easy:

* `git clone git://github.com/PebbleTemplates/pebble.git`
* Add feature or fix bug.
* Add quick unit test
* `mvn install`
* Submit pull request!


## License

    Copyright (c) 2013 by Mitchell Bösecke

    Some rights reserved.

    Redistribution and use in source and binary forms, with or without
    modification, are permitted provided that the following conditions are
    met:
    
        * Redistributions of source code must retain the above copyright
          notice, this list of conditions and the following disclaimer.
    
        * Redistributions in binary form must reproduce the above
          copyright notice, this list of conditions and the following
          disclaimer in the documentation and/or other materials provided
          with the distribution.
    
        * The names of the contributors may not be used to endorse or
          promote products derived from this software without specific
          prior written permission.
    
    THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
    "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
    LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
    A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
    OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
    SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
    LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
    DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
    THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
    (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
    OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

![Continuous Integration](https://api.travis-ci.org/PebbleTemplates/pebble.svg?branch=master)
