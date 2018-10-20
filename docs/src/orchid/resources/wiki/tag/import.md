---
---

# `import`
The `import` tag allows you to use {{ anchor('macros', 'macro') }} defined in another template.

Assuming that a macro named `input` exists in a template called `form_util` you can import it like so:

```twig
{% verbatim %}
{% import "form_util" %}

{{ input("text", "name", "Mitchell") }}
{%- endverbatim %}
```

The easiest and most flexible is importing the whole module into a variable. That way you can access the attributes:

```twig
{% verbatim %}
{% import 'forms.html' as forms %}

<dl>
    <dt>Username</dt>
    <dd>{{ forms.input('username') }}</dd>
    <dt>Password</dt>
    <dd>{{ forms.input('password', null, 'password') }}</dd>
</dl>
<p>{{ forms.textarea('comment') }}</p>
{%- endverbatim %}
```

Alternatively you can import names from the template into the current namespace:

```twig
{% verbatim %}
{% from 'forms.html' import input as input_field, textarea %}

<dl>
    <dt>Username</dt>
    <dd>{{ input_field('username') }}</dd>
    <dt>Password</dt>
    <dd>{{ input_field('password', '', 'password') }}</dd>
</dl>
<p>{{ textarea('comment') }}</p>
{%- endverbatim %}
```

## Dynamic Import
The `import` tag will accept an expression to determine the template to import at runtime. For example:
```twig
{% verbatim %}
{% import modern ? 'ajax_form_util' : 'simple_form_util' %}

{{ input("text", "name", "Mitchell") }}
{%- endverbatim %}
```