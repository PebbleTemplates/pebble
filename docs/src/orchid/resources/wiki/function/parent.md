# `parent`

The `parent` function is used inside of a block to render the content that the parent template would
have rendered inside of the block had the current template not overriden it. It is similar to Java's `super` keyword.

Let's assume you have a template, "parent.peb" that looks something like this:
```twig
{% block "content" %}
	parent contents
{% endblock %}
```
And then you have another template, "child.peb" that extends "parent.peb":
```twig
{% extends "parent.peb" %}

{% block "content" %}
	child contents
	{{ parent() }}
{% endblock %}
```
The output will look something like the following:
```twig
parent contents
child contents
```