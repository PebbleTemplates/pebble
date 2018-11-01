---
---

# `autoescape`

The `autoescape` tag can be used to temporarily disable/re-enable the autoescaper as well as change the
escaping strategy for a portion of the template.
```twig
{% verbatim %}
{{ danger }} {# will be escaped by default #}
{% autoescape false %}
	{{ danger }} {# will not be escaped #}
{% endautoescape %}
{%- endverbatim %}
```
```twig
{% verbatim %}
{{ danger }} {# will use the "html" escaping strategy #}
{% autoescape "js" %}
	{{ danger }} {# will use the "js" escaping strategy #}
{% endautoescape %}
{%- endverbatim %}
```
Please read the {{ anchor('escaping guide', 'Escaping') }} for more information about escaping.