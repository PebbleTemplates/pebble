---
---

Pebble comes with a rich set of built-in tags and filters that will help you render your templates into  websites and other documents with ease. However, imagine a more specific use-case where the templates are not entirely under your control.

In these cases it might be advised to consider stripping-down Pebbles' built-in functionality that may otherwise introduce security-concers regarding the integrity and stability of your application.

### Opt-Out using ExtensionCustomizer

The `ExtensionCustomizer` base class can be used to gain access to the default functionality before it is loaded into Pebbles template engine. Overwrite methods to get hold on provided default-functionality and modify whatever should be available for the template engine.

The following example removes the `ForTokenParser`, i.e. the ability to parse `{% for %}{{ ... }}{% endfor %}` constructs:

```java
class ExampleOptOuts extends ExtensionCustomizer {

  public ExampleOptOuts(Extension ext) {
    super(ext);
  }

  @Override
  public List<TokenParser> getTokenParsers() {
    List<TokenParser> tokenParsers = Optional.ofNullable(super.getTokenParsers())
                                        .map(ArrayList::new).orElseGet(ArrayList::new);
      
    tokenParsers.removeIf(x -> x instanceof ForTokenParser);
    return tokenParsers;
  }

}
```

The `ExtensionCustomizer` will be used to wrap any Pebble-extension which is provided by default. It can be registered in your setup code to create `PebbleEngine`:

```java
PebbleEngine engine = new PebbleEngine.Builder().registerExtensionCustomizer(ExampleOptOuts::new).build();
```
