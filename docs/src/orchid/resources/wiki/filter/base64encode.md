---
---

# `base64encode`
The `base64encode` filter takes the given input, converts it to an UTF-8 String (`.toString()`) and Base64-encodes it.

```twig
{% verbatim %}{{ "test" | base64encode }}{% endverbatim %}
```
The above example will output the following:
```
dGVzdA==
```
