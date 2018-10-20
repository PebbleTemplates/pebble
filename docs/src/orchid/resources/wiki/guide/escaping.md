---
---

# Escaping

## Overview
[XSS vulnerabilites](https://en.wikipedia.org/wiki/Cross-site_scripting) are the most common types of security
vulnerabilities in web applications and in order to avoid them you must escape potentially unsafe data before
presenting it to the end user. Pebble provides autoescaping of all such data which is enabled by default.
Autoescaping can be turned off, in which case Pebble provides an {{ anchor('escape') }} filter for more
fine-grained manual escaping.

## Autoescaping
Autoescaping, which is enabled by default, will automatically escape the outcome of expressions
contained within print delimiters, i.e. {% verbatim %}`{{` and `}}`{% endverbatim %}:
```twig
{% verbatim %}
{% set danger = "<br>" %}
{{ danger }}

{# will output: &lt;br&gt; #}
{%- endverbatim %}
```
The {{ anchor('raw') }} filter can be used to prevent the autoescaper from escaping a particular expression. It is
important that the raw filter is the last operation performed in the expression.
```twig
{% verbatim %}
{% set danger = "<br>" %}
{{ danger | raw }}

{# will output: <br> #}
{%- endverbatim %}
```
If the raw filter is not the last operation performed within the expression, the expression will be deemed
as possibly unsafe by the autoescaper and will be escaped. For example:
```twig
{% verbatim %}
{% set danger = "<br>" %}
{{ danger | raw | uppercase }}

{# will output: &lt;BR&gt; #}
{%- endverbatim %}
```

### Exceptions
There are a few exceptions where expressions are **not** automatically escaped:
- If the expression only contains a string literal, it is assumed to be safe. For example:
```twig
{% verbatim %}
{{ '<br>' }}

{# will output: <br> #}
{%- endverbatim %}
```
- The last operation contained within that expression is a filter or function that explicitly returns safe output. Such a filter or function would return an instance of `SafeString` instead of a regular String. The built-in filters that return safe markup include: `date`, `escape`, and `raw`. These filters must be the last operation performed within the expression in order for their output to be ignored by the autoescaper. For example:
```twig
{% verbatim %}
{% set danger = "<br>" %}
{{ danger | uppercase | raw }}

{# will output: <br> #}
{%- endverbatim %}
```

### Autoescape Tag
The {{ anchor('autoescape') }} tag can be used to temporarily disable/re-enable the autoescaper as well as
change the escaping strategy for a portion of the template.
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

### Disabling Autoescaper
```java
PebbleEngine engine = new PebbleEngine.Builder().autoEscaping(false).build();
```

## Manual Escaping
If autoescaping is disabled you can still use the {{ anchor('escape') }} filter to aid with manual escaping:
```twig
{% verbatim %}
{% set danger = "<br>" %}
{{ danger | escape }}

{# will output: &lt;br&gt; #}
{%- endverbatim %}
```

## Strategies
When escaping data it is crucial that you utilize the correct escaping strategy depending on the context of the data.
By default, the autoescaper and the `escape` filter assume that you are escaping HTML data.
I highly recommend reading the [OWASP Cheat Sheet](https://www.owasp.org/index.php/XSS_(Cross_Site_Scripting)_Prevention_Cheat_Sheet)
to understand the significance of escaping context.

Pebble provides the following escaping strategies:

- html
- js
- css
- url_param

You can use the {{ anchor('autoescape') }} tag to temporarily change the strategy used by the autoescaper otherwise you can
change the globally used default strategy:
```java
PebbleEngine engine = new PebbleEngine.Builder().defaultEscapingStrategy("js").build();
```
The escape filter will also accept a strategy as an argument:
```js
{% verbatim %}
var username ="{{ user.name | escape(strategy="js") }}";
{%- endverbatim %}
```

### Custom Strategy
You can add a custom escaping strategy by implementing `EscapingStrategy` and adding it to the `EscaperExtension`:
```java
PebbleEngine engine = new PebbleEngine.Builder().addEscapingStrategy("custom", new CustomEscapingStrategy()).build();
```