# `join`
The `join` filter will concatenate all items of a collection into a string. An optional argument can be given
to be used as the separator between items.
```twig
{#
    List<String> names = new ArrayList<>();
    names.add("Alex");
    names.add("Joe");
    names.add("Bob");
#}
{{ names | join(',') }}
{# will output: Alex,Joe,Bob #}
```

## Arguments
- separator