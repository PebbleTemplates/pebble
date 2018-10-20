# `include`

The `include` tag allows you to insert the rendered output of another template directly into the
current template. The included template will have access to the same variables that the current template does.

```twig
Top Content
{% include "advertisement" %}
Bottom Content
{% include "footer" %}
```

You can add additional variables to the context of the included template by passing a map after the `with` keyword. The included template will have access to the same variables that the current template does plus the additional ones defined in the map passed after the `with` keyword:

```twig
{% include "advertisement" with {"foo":"bar"} %}
```

## Dynamic Include
The `include` tag will accept an expression to determine the template to include at runtime. For example:
```twig
{% include admin ? 'adminFooter' : 'defaultFooter' %}
```
