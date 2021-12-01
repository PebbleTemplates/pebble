# Comparisons
Pebble provides the following comparison operators: `==`, `!=`, `<`, `>`, `<=`, `>=`. All of them except for `==`
are equivalent to their Java counterparts. The `==` operator uses `java.util.Objects.equals(a, b)` behind the
scenes to perform null safe value comparisons.

> `equals` is an alias for `==`

```twig
{% if user.name equals "Mitchell" %}
	...
{% endif %}
```
