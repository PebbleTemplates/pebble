# `extends`
The `extends` tag is used to declare a parent template. It should be the very first tag used in a child
template and a child template can only extend up to one parent template.

The best way to understand template inheritance is to study an example. Let us look at a parent
template called "base":
```twig
<html>
	<head>
		<title>{% block title %} {% endblock %}</title>
	</head>
	<body>
		<div id="content">
			{% block content %}
				Default content goes here.
			{% endblock %}
		</div>

		<div id="footer">
			{% block footer %}
				Default footer content
			{% endblock %}
		</div>
	</body>
</html>
```
And now let's look at a child template called "home" which extends "base":
```twig
{% extends "base" %}

{% block title %} Home {% endblock %}

{% block content %}
	Home page content.
{% endblock %}
```
And finally let's look at the resulting output after evaluating "home":
```twig
<html>
	<head>
		<title> Home </title>
	</head>
	<body>
		<div id="content">
			Home page content will override the default content.
		</div>

		<div id="footer">
			Default footer content
		</div>
	</body>
</html>
```
To summarize, parent templates define blocks and child templates will override the contents of those blocks.
If a child template does not override the content of a particular block, the content provided by the parent
template will be used.

There is no limit to how long of an inheritance chain that you can create; i.e. a child template can
itself have a child template. A lot of potential comes from this fact because you can create a hierarchy of
templates to minimize how much content you have to write on the lower levels.

## Dynamic Inheritance
The `extends` tag will accept an expression to determine the parent template at runtime. For example:
```twig
{% extends ajax ? 'ajax' : 'base' %}
```