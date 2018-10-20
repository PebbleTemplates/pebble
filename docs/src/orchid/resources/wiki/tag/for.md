# `for`

The `for` tag is used to iterate through primitive arrays or anything that implements the `java.lang.Iterable`
interface, as well as maps.
```twig
{% for user in users %}
	{{ user.name }} lives in {{ user.city }}.
{% endfor %}
```
While inside of the loop, Pebble provides a couple of special variables to help you out:
- loop.index - a zero-based index that increments with every iteration.
- loop.length - the size of the object we are iterating over.
- loop.first - True if first iteration
- loop.last - True if last iteration
- loop.revindex - The number of iterations from the end of the loop

```twig
{% for user in users %}
	{{ loop.index }} - {{ user.id }}
{% endfor %}
```
The `for` tag also provides a convenient way to check if the iterable object is empty with the included `else` tag.
```twig
{% for user in users %}
	{{ loop.index }} - {{ user.id }}
{% else %}
	There are no users to display.
{% endfor %}
```
Iterating over maps can be done like so:
```twig
{% for entry in map %}
    {{ entry.key }} - {{ entry.value }}
{% endfor %}
```