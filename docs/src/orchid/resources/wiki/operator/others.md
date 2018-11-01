# Other Operators
The `|` operator is used to apply a filter to a variable.
```twig
{{ user.name | capitalize }}
```
Pebble supports the use of the conditional operator (often named the ternary operator).
```twig
{{ foo == null ? bar : baz }}
```