---
---

# `escape`
The `escape` filter will turn special characters into safe character references in order to avoid XSS
vulnerabilities. This filter will typically only need to be used if you've turned off autoescaping.
```twig
{% verbatim %}
{{ "<div>" | escape }}
{# output: &lt;div&gt; #}
{% endverbatim %}
```
Please read the {{ anchor('escaping guide', 'Escaping') }} for more information about escaping.

## Arguments
- strategy