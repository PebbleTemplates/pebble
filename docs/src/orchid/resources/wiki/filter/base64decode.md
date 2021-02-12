---
---

# `base64decode`
The `base64decode` filter takes the given input, Base64-decodes it, if possible, and returns the byte array converted to UTF-8 String.
Applying the filter on an incorrect base64-encoded string will throw an exception. 

```twig
{% verbatim %}{{ "dGVzdA==" | base64decode }}{% endverbatim %}
```
The above example will output the following:
```
test
```