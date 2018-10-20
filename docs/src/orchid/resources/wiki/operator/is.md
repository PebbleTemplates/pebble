---
---

# `is`
The `is` operator will apply a test to a variable which will return a boolean.

```twig
{% verbatim %}
{% if 2 is even %}
	...
{% endif %}
{%- endverbatim %}
```
The result can be negated using the {{ anchor('not', 'logic') }} operator.