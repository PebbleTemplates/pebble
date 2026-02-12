# `format`
The `format` filter formats a string by replacing placeholders with the provided arguments (placeholders follows the `String.format` notation).
```twig
{{ 'Hello %s!' | format('World') }}
{# Hello World! #}
```