# `empty`
The `empty` test checks if a variable is empty. A variable is empty if it is null, an empty string, an
empty collection, or an empty map.
```twig
{% if user.email is empty %}
	...
{% endif %}
```