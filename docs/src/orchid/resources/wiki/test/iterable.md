# `iterable`
The `iterable` test checks if a variable implements `java.lang.Iterable`.
```twig
{% if users is iterable %}
	{% for user in users %}
		...
	{% endfor %}
{% endif %}
```