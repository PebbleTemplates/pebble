# `first`
The `first` filter will return the first item of a collection, or the first letter of a string.
```twig
{{ users | first }}
{# will output the first item in the collection named 'users' #}

{{ 'Mitch' | first }}
{# will output 'M' #}
```