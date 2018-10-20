# `date`
The `date` filter is used to format an existing `java.util.Date` object. The filter will construct a
`java.text.SimpleDateFormat` using the provided pattern and then use this newly created
`SimpleDateFormat` to format the provided `Date` or `java.lang.Number` object.

```twig
{{ user.birthday | date("yyyy-MM-dd") }}
```

The alternative way to use this	filter is to use it on a string but then provide two arguments:
first is the desired pattern for the output and the second is the existing format used to parse the
input string into a `java.util.Date` object.
```twig
{{ "July 24, 2001" | date("yyyy-MM-dd", existingFormat="MMMM dd, yyyy") }}
```
The above example will output the following:
```twig
2001-07-24
```

## Arguments
- format
- existingFormat