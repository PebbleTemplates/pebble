# `abbreviate`
The `abbreviate` filter will abbreviate a string using an ellipsis. It takes one argument which is the max
width of the desired output including the length of the ellipsis.
```twig
{{ "this is a long sentence." | abbreviate(7) }}
```
The above example will output the following:
```twig
this...
```

## Arguments
- length