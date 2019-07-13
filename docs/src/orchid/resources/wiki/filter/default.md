## `default`
The `default` filter will render a default value if and only if the object being filtered is empty.
A variable is empty if it is null, an empty string, an empty collection, or an empty map.
```twig
{{ user.phoneNumber | default("No phone number") }}
```
In the following example, if `foo`, `bar`, or `baz` are null the output will become an empty string which is a perfect use case for the default filter:
```twig
{{ foo.bar.baz | default("No baz") }}
```
Note that the default filter will suppress any `AttributeNotFoundException` exceptions that will usually be thrown when `strictVariables` is set to `true`.

## Arguments
- default
