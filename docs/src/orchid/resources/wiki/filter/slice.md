# `slice`
The `slice` filter returns a portion of a list, array, or string.
```twig
{{ ['apple', 'peach', 'pear', 'banana'] | slice(1,3) }}
{# results in: [peach, pear] #}


{{ 'Mitchell' | slice(1,3) }}
{# results in: 'it' #}
```

## Arguments
- `fromIndex`: 0-based and inclusive
- `toIndex`: 0-based and exclusive
