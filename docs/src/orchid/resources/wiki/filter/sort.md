# `sort`
The `sort` filter will sort a list. The items of the list must implement `Comparable`.
```twig
{% for user in users | sort %}
	{{ user.name }}
{% endfor %}
```