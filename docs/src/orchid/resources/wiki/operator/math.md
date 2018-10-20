---
---

# Math
All the regular math operators are available for use. Order of operations applies.
```twig
{% verbatim %}
{{ 2 + 2 / ( 10 % 3 ) * (8 - 1) }}
{% endverbatim %}
```
The result can be negated using the {{ anchor('not', 'logic') }} operator.