# `last`
The `last` filter will return the last item of a collection, or the last letter of a string.
```twig
{{ users | last }}
{# will output the last item in the collection named 'users' #}

{{ 'Mitch' | last }}
{# will output 'h' #}
```