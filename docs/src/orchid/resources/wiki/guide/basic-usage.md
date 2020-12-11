---
---

# Basic Usage

## Introduction
Pebble templates can be used to generate any sort of textual output. It is typically used to generate HTML but it can
also be used to create CSS, XML, JS, etc. A template itself will contain whatever language you are attempting to output
alongside Pebble-specific features and syntax. Here is a simple example that will generate a trivial HTML page:

```twig
{% verbatim %}
<html>
	<head>
		<title>{{ websiteTitle }}</title>
	</head>
	<body>
		{{ content }}
	</body>
</html>
{%- endverbatim %}
```
When you evaluate the template you will provide it with a "context" which is just a map of variables.
This context should include the two variables above, `websiteTitle` and `content`.

## Set Up
You will want to begin by creating a PebbleEngine object which is responsible for compiling your templates:
```java
PebbleEngine engine = new PebbleEngine.Builder().build();
```
And now, with your new PebbleEngine instance you can start compiling templates:
```java
PebbleTemplate compiledTemplate = engine.getTemplate("templates/home.html");
```
Finally, simply provide your compiled template with a java.io.Writer object and a Map of variables (the context)
to get your output!
```java
Writer writer = new StringWriter();

Map<String, Object> context = new HashMap<>();
context.put("websiteTitle", "My First Website");
context.put("content", "My Interesting Content");

compiledTemplate.evaluate(writer, context);

String output = writer.toString();
```

## Syntax Reference
There are two primary delimiters used within a Pebble template: `{% verbatim %}{{ ... }}{% endverbatim %}` and `{% verbatim %}{% ... %}{% endverbatim %}`. The first set of delimiters
will output the result of an expression. Expressions can be very simple (ex. a variable name) or much more complex.
The second set of delimiters is used to change the control flow of the template; it can contain an if-statement,
define a parent template, define a new block, etc.

## Variables
You can print variables directly to the output; for example, if the context contains a variable called `foo` which is a
String with the value "bar" you can do the following which will output "bar".
```twig
{% verbatim %}{{ foo }}{% endverbatim %}
```
You can use the dot (.) notation to access attributes of variables. If the attribute contains any atypical
characters, you can use the subscript notation ([]) instead.
```twig
{% verbatim %}
{{ foo.bar }}
{{ foo["bar"] }}
{%- endverbatim %}
```
Behind the scenes `foo.bar` will attempt the following techniques to to access the `bar` attribute of the `foo`
variable:
- If `foo` is a Map, `foo.get("bar")`
- `foo.getBar()`
- `foo.isBar()`
- `foo.hasBar()`
- `foo.bar()`
- `foo.bar`

Additionally, if `foo` is a List, then `foo[0]` can be used instead of `foo.get(0)`.

If the value of variable (or attribute) is null it will output an empty string.

## Type Safety
Pebble templates are dynamically typed and any possible type safety issues won't occur until the actual runtime
evaluation of your templates. Pebble does however allow you to choose how to handle type safety issues with the use
of it's `strictVariables` setting. By default, `strictVariables` is set to `false` which means that the following:
```twig
{% verbatim %}{{ foo.bar }}{% endverbatim %}
```
will print an empty string even if the object `foo` does not actually have an attribute called `bar`.
If `strictVariables` is set to true, the above expression would throw an exception.

When `strictVariables` is set to false your expressions are also null safe. The following expression will print an
empty string even if foo and/or bar are null:
```twig
{% verbatim %}{{ foo.bar.baz }}{% endverbatim %}
```
The {{ anchor('default') }} filter might come in handy for the above situations.

## Filters

Output can be further modified with the use of filters. Filters are separated from the variable using a
pipe symbol (`|`) and may have optional arguments in parentheses. Multiple filters can be chained and the output
of one filter is applied to the next.
```twig
{% verbatim %}{{ "If life gives you lemons, eat lemons." | upper | abbreviate(13) }}{% endverbatim %}
```
The above example will output the following:
```twig
IF LIFE GI...
```

## Functions
Whereas filters are intended to modify existing content/variables, functions are intended to generate new content.
Similar to other programming languages, functions are invoked via their name followed by parentheses (`()`).
```twig
{% verbatim %}{{ max(user.score, highscore) }}{% endverbatim %}
```

## Control Structure
Pebble provides several tags to control the flow of your template, two of the main ones being the {{ anchor('for') }} loop,
and {{ anchor('if') }} statements.
```twig
{% verbatim %}
{% for article in articles %}
    <h3>{{ article.title }}</h3>
    <p>{{ article.content }}</p>
{% else %}
    <p> There are no articles. </p>
{% endfor %}
{%- endverbatim %}
```
```twig
{% verbatim %}
{% if category == "news" %}
    {{ news }}
{% elseif category == "sports" %}
    {{ sports }}
{% else %}
    <p>Please select a category</p>
{% endif %}
{%- endverbatim %}
```

## Including other Templates
The {{ anchor('include') }} tag is used to include the rendered output of one template into another.
```twig
{% verbatim %}
<div class="sidebar">
	{% include "advertisement.html" %}
</div>
{%- endverbatim %}
```

## Template Inheritance
Template inheritance is the most powerful feature of Pebble. It allows templates to override sections of their parent
template. In your parent template you define "blocks" which are the sections that are allowed to be overriden.

First let us look at an example of a parent template:
```twig
{% verbatim %}
<html>
<head>
	<title>{% block title %}My Website{% endblock %}</title>
</head>
<body>
	<div id="content">
		{% block content %}{% endblock %}
	</div>
	<div id="footer">
		{% block footer %}
			Copyright 2013
		{% endblock %}
	</div>
</body>
</html>
{% endverbatim %}
```
In the above example, we have used the {{ anchor('block') }} tag to define several sections that child templates are
allowed to override.

A child template might look like this:

```twig
{% verbatim %}
{% extends "parent.html" %}

{% block title %} Home {% endblock %}

{% block content %}
	<h1> Home </h1>
	<p> Welcome to my home page.</p>
{% endblock %}
{%- endverbatim %}
```

The first line uses the {{ anchor('extends') }} tag to declare the parent template. The extends tag should be the
first tag in the template and there can only be one.

Evaluating the child template will produce the following output:

```twig
<html>
<head>
	<title>Home</title>
</head>
<body>
	<div id="content">
		<h1> Home </h1>
		<p> Welcome to my home page.</p>
	</div>
	<div id="footer">
		Copyright 2013
	</div>
</body>
</html>
```

You may have noticed that in the above example, because the child template doesn't override the `footer` block,
the value from the parent is used instead.

Dynamic inheritance is possible by using an expression with the `extends` tag:
```twig
{% verbatim %}{% extends ajax ? 'ajax.html' : 'base.html' %}{% endverbatim %}
```

## Macros
Macros are lightweight and reusable template fragments. A macro is defined via the {{ anchor('macro') }} tag:
```twig
{% verbatim %}
{% macro input(type, name) %}
	<input type="{{ type }}" name="{{ name }}" />
{% endmacro %}
{% endverbatim %}
```
And the macro will be invoked just like a function:
```twig
{% verbatim %}{{ input("text", "name", "Mitchell") }}{% endverbatim %}
```
Child templates will have access to macros defined in a parent template. To use macros located in a completely
different template, you can use the {{ anchor('import') }}) tag. A macro does not have access to the main context; the
only variables it can access are it's local arguments.

## Named Arguments
Using named arguments allows you to be more explicit with the values you are passing to a filter, function, test or
macro. They also allow you to avoid specifying arguments for which you don't want to change the default value.
```twig
{% verbatim %}{{ stringDate | date(existingFormat="yyyy-MMMM-d", format="yyyy/MMMM/d") }}{% endverbatim %}
```

Positional arguments can be used in conjunction with named arguments but all positional arguments must come before
any named arguments:
```twig
{% verbatim %}{{ stringDate | date("yyyy/MMMM/d", existingFormat="yyyy-MMMM-d") }}{% endverbatim %}
```
Macros are a great use case for named arguments because they also allow you to define default values for
unused arguments:
```twig
{% verbatim %}
{% macro input(type="text", name, value) %}
	<input type="{{ type }}" name="{{ name }}" value="{{ value }}" />
{% endmacro %}

{{ input(name="country") }}

{# will output: <input type="text" name="country" value="" /> #}
{%- endverbatim %}
```

## Escaping
[XSS vulnerabilites](https://en.wikipedia.org/wiki/Cross-site_scripting) are the most common types of security
vulnerabilities in web applications and in order to avoid them you must escape potentially unsafe data before
presenting it to the end user. Pebble provides autoescaping of all such data which is enabled by default.
Autoescaping can be turned off, in which case Pebble provides an escape filter for more fine-grained manual escaping.

The following is an example of how autoescaping will escape your context variables:
```twig
{% verbatim %}
{% set danger = "<br>" %}
{{ danger }}

{# will output: &lt;br&gt; #}
{%- endverbatim %}
```
If autoescaping is disabled you can still use the {{ anchor('escape') }} filter to aid with manual escaping:
```twig
{% verbatim %}
{% set danger = "<br>" %}
{{ danger | escape }}

{# will output: &lt;br&gt; #}
{%- endverbatim %}
```
By default, the autoescaping mechanism and the escape filter assume that it is escaping within an HTML context.
You may want to use an alternate escaping strategy depending on the context:
```twig
{% verbatim %}
{% set danger = "alert(...)" %}
<script>var username="{{ danger | escape(strategy="js") }}"</script>
{%- endverbatim %}
```
See the {{ anchor('escaping guide', 'Escaping') }} for more information on how autoescaping works, how to disable it, and the various
escaping strategies that are available.

## Whitespace
The first newline after a pebble tag is automatically ignored; all other whitespace is ignored by Pebble and will be
included in the rendered output.

Pebble provides a whitespace control modifier to trim leading or trailing whitespace adjacent to any pebble tag.
```twig
{% verbatim %}
<p> 		{{- "no whitespace" -}}		</p>
{# output: "<p>no whitespace</p>" #}
{%- endverbatim %}
```
It is also possible to only use the modifier on one side of the tag:
```twig
{% verbatim %}
<p> 		{{- "no leading whitespace" }}		</p>
{# output: "<p>no whitespace		</p>" #}
{%- endverbatim %}
```

## Comments
You can comment out any part of the template using the `{# ... #}` delimiters. These comments will not appear in
the rendered output.
```twig
{% verbatim %}
{# THIS IS A COMMENT #}
{% for article in articles %}
	<h3>{{ article.title }}</h3>
	<p>{{ article.content }}</p>
{% endfor %}
{%- endverbatim %}
```

## Expressions
Expressions in a Pebble template are very similar to expressions found in Java.

### Literals
The simplest form of expressions are literals. Literals are representations for Java types such as strings and numbers.
- `"Hello World"`: Everything between two double or single quotes is a string. You can use a backslash to escape
quotation marks within the string.
- `"Hello #{who}"`: String interpolation is also possible using `#{}` inside quotes. In this example,
if the value of the variable `who` is `"world"`, then the expression will be evaluated to `"Hello world"`.
- `100 + 10l * 2.5`: Integers, longs and floating point numbers are similar to their Java counterparts.
- `true` / `false`: Boolean values equivalent to their Java counterparts.
- `null`: Represents no specific value, similar to it's Java counterpart. `none` is an alias for null.

### Collections
Both lists and maps can be created directly within the template.
- `["apple", "banana", "pear"]`: A list of strings
- `{"apple":"red", "banana":"yellow", "pear":"green"}`: A map of strings

The collections can contain expressions.

### Math
Pebble allows you to calculate values using some basic mathematical operators. The following operators are supported:
- `+`: Addition
- `-`: Subtraction
- `/`: Division
- `%`: Modulus
- `*`: Multiplication

### Logic
You can combine multiple expressions with the following operators:
- `and`: Returns true if both operands are true
- `or`: Returns true if either operand is true
- `not`: Negates an expression
- `(...)`: Groups expressions together

### Comparisons
The following comparison operators are supported in any expression: `==`, `!=`, `<`, `>`, `>=`, and `<=`.
```twig
{% verbatim %}
{% if user.age >= 18 %}
	...
{% endif %}
{%- endverbatim %}
```

### Tests
The `is` operator performs tests. Tests can be used to test an expression for certain qualities.
The right operand is the name of the test:
```twig
{% verbatim %}
{% if 3 is odd %}
	...
{% endif %}
{%- endverbatim %}
```
Tests can be negated by using the is not operator:
```twig
{% verbatim %}
{% if name is not null %}
	...
{% endif %}
{%- endverbatim %}
```

### Conditional (Ternary) Operator
The conditional operator is similar to its Java counterpart:
```twig
{% verbatim %}{{ foo ? "yes" : "no" }}{% endverbatim %}
```

### Operator Precedence
In order from highest to lowest precedence:
- `.`
- `|`
- `%`, `/`, `*`
- `-`, `+`
- `==`, `!=`, `>`, `<`, `>=`, `<=`
- `is`, `is not`
- `and`
- `or`

### Limiting the size of the rendered output

In case you’re running Pebble with templates provided by someone else, there’s an attack similar to
[zip bombs](https://en.wikipedia.org/wiki/Zip_bomb) or [XML bombs](https://en.wikipedia.org/wiki/Billion_laughs_attack)
that might cause your process to run out of memory. To protect against it, you can limit the size of the output when
evaluating a template:
```java
PebbleEngine pebble = new PebbleEngine.Builder()
                // Output should not exceed 10 MB.
                .maxRenderedSize(10 * 1024 * 1024)
                .build();
```

This will throw a `PebbleException` when a template evaluation tries to write more characters than the limit you set.

### IDE's plugin
If you want to add IDE's syntax highlighting, you can install this [plugin](https://plugins.jetbrains.com/idea/plugin/9407-pebble) for IntelliJ. Thank you to Bastien Jansen for his contribution.
