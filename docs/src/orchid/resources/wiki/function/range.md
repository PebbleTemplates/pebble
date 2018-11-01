# `range`
The `range` function will return a list containing an arithmetic progression of numbers:
```twig
{% for i in range(0, 3) %}
    {{ i }},
{% endfor %}

{# outputs 0, 1, 2, 3, #}
```

When step is given (as the third parameter), it specifies the increment (or decrement):
```twig
{% for i in range(0, 6, 2) %}
    {{ i }},
{% endfor %}

{# outputs 0, 2, 4, 6, #}
```

Pebble built-in .. operator is just a shortcut for the range function with a step of 1+
```twig
{% for i in 0..3 %}
    {{ i }},
{% endfor %}

{# outputs 0, 1, 2, 3, #}
```
