# `nl2br`

The `nl2br` filter converts newline characters (`\r`, `\n`, `\r\n`) in a string to HTML line break tags (`<br />`). This
is useful when you want to preserve line breaks in text when displaying it in a web page.

```pebble
{{ "I like Pebble.\nYou will like it too."|nl2br }}
{# outputs
    I like Pebble.<br />You will like it too.
#}
```
