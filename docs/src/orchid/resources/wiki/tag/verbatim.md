# `verbatim`
The `verbatim` tag allows you to write Pebble syntax that won't be parsed.
```twig
{% verbatim %}
	{% for user in users %}
		{{ user.name }}
	{% endfor %}}
{% endverbatim %}
```