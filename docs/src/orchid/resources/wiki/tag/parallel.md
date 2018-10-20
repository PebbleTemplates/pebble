---
---

# `parallel`
The `parallel` tag allows you to designate a chunk of content to be rendered using a new thread.
This tag is only available if you provide an `ExecutorService` to the main `PebbleEngine`.

```twig
{% verbatim %}
{{ upperContent }}

{% parallel %}
    {{ calculation.slowCalculation }}
{% endparallel %}

{{ lowerContent }}
{% endverbatim %}
```
In the above example, the slow calculation will not block the `lowerContent` from being evaluated concurrently.

See the {{ anchor('high performance guide', 'High Performance Techniques') }} for more tips on how to improve performance.
