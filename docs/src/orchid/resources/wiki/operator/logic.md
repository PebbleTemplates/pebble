# Logic

The `and` operator and the `or` operator are available to join boolean expressions.
```twig
{% if 2 is even and 3 is odd %}
	...
{% endif %}
```
The `not` operator is available to negate a boolean expression.
```twig
{% if 3 is not even %}
	...
{% endif %}
```
Parenthesis can be used to group expressions to ensure a desired precedence.
```twig
{% if (3 is not even) and (2 is odd or 3 is even) %}
	...
{% endif %}
```