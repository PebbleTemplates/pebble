---
---

# `if`
The `if` tag allows you to designate a chunk of content as conditional depending on the result of an expression
```twig
{% verbatim %}
{% if users is empty %}
	There are no users.
{% elseif users.length == 1 %}
	There is only one user.
{% else %}
	There are many users.
{% endif %}
{%- endverbatim %}
```
The expression used in the `if` statement often makes use of the {{ anchor('is') }} operator.

### Supported conditions

`If` tag currently supports the following expression

| Value  | Boolean expression |
| --- | --- |
| boolean | boolean value |
| Empty string | false |
| Non empty string | true |
| numeric zero | false |
| numeric different than zero | true |