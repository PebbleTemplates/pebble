# `contains`
The `contains` operator can be used to determine if a collection, map, or array contains a particular item.
```twig
{% if ["apple", "pear", "banana"] contains "apple" %}
	...
{% endif %}
```
When using maps, the contains operator checks for an existing key.
```twig
{% if {"apple":"red", "banana":"yellow"} contains "banana" %}
	...
{% endif %}
```
The operator can be used to look for multiple items at once:
```twig
{% if ["apple", "pear", "banana", "peach"] contains ["apple", "peach"] %}
	...
{% endif %}
```