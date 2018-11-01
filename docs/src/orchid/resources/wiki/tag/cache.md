# `cache`

Cache the rendering portion of a page. Cache name can be an expression or a static string. It uses the cache name and the locale as a key in the cache.

In the following example we create a cache with the name 'menu':
```twig
{% cache 'menu' %}
    {% for item in items %}
        {{ item.text }}
        ....
    {% endfor %}
{% endcache %}
```

Cache implementation can be overriden with the PebbleEngine Builder.
```java
 return new PebbleEngine.Builder()
                .loader(this.templateLoader())
                .tagCache(CacheBuilder.newBuilder().maximumSize(200).build())
                .build();
```
