# `split`
The `split` filter splits a string by the given delimiter and returns a list of strings.
```twig
{% set foo = "one,two,three" | split(',') %}
{# foo contains ['one', 'two', 'three'] #}
```

You can also pass a limit argument:
- If `limit` is positive, then the pattern will be applied at most n - 1 times, the array's length will be no greater than n, and the array's last entry will contain all input beyond the last matched delimiter;
- If `limit` is negative, then the pattern will be applied as many times as possible and the array can have any length;
- If `limit` is zero, then the pattern will be applied as many times as possible, the array can have any length, and trailing empty strings will be discarded;

```twig
{% set foo = "one,two,three,four,five" | split(',', 3) %}
{# foo contains ['one', 'two', 'three,four,five'] #}
```

## Arguments
- delimiter: The delimiter
- limit: The limit argument