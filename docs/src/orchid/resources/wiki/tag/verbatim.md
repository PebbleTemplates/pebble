# `verbatim`
The `verbatim` tag allows you to write a block of Pebble syntax that won't be parsed.
```twig
{% verbatim %}
	{% for user in users %}
		{{ user.name }}
	{% endfor %}
{% endverbatim %}
```
<br/>
## Inline Verbatim Text

For inline verbatim text, a string literal can be used. For example, if you need to include **{{** in the output of a template, you can use `{{ "{{" }}` in string literal in the Pebble template 

This would be useful if you are using Pebble to generate Angular HTML component template files:

```javascript
<td>{{ "{{" }}school.name{{ "}}" }}</td>
```

would produce the following template output:

```javascript
<td>{{school.name}}</td>
```
