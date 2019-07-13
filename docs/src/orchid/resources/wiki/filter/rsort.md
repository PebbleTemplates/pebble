# `rsort`
The `rsort` filter will sort a list in reversed order. The items of the list must implement `Comparable`.
```twig
{% for user in users | rsort %}
	{{ user.name }}
{% endfor %}
```