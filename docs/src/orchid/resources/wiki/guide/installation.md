---
---

# Installation & Configuration
## Installation
Pebble is hosted in the Maven Central Repository. Simply add the following dependency into your `pom.xml` file:
```xml
<dependency>
	<groupId>io.pebbletemplates</groupId>
	<artifactId>pebble</artifactId>
	<version>{{ site.version }}</version>
</dependency>
```

Also, snapshots of the master branch are deployed automatically with each successful commit. Instead of Maven Central, use the Sonatype snapshots repository at:
```xml
<url>https://oss.sonatype.org/content/repositories/snapshots</url>
```
You can add the repository in your pom.xml

```xml
<repositories>
  <repository>
    <id>sonatype-public</id>
    <name>Sonatype Public</name>
    <url>https://oss.sonatype.org/content/groups/public</url>
    <snapshots>
      <enabled>true</enabled>
    </snapshots>
  </repository>
</repositories>
```

## Set Up
If you are integrating Pebble with Spring MVC, read {{ anchor('this guide', 'Spring Integration') }}.

You will want to begin by creating a `PebbleEngine` which is responsible for coordinating the retrieval and
compilation of your templates:
```java
PebbleEngine engine = new PebbleEngine.Builder().build();
```
And now, with your new `PebbleEngine` instance you can start compiling templates:
```java
PebbleTemplate compiledTemplate = engine.getTemplate("templateName");
```
Finally, simply provide your compiled template with a `Writer` object and a Map of variables to get your output!
```java
Writer writer = new StringWriter();

Map<String, Object> context = new HashMap<>();
context.put("name", "Mitchell");

compiledTemplate.evaluate(writer, context);

String output = writer.toString();
```

## Template Loader
The `PebbleEngineBuilder` will also accept a `Loader` implementation as an argument. A loader is responsible for
finding your templates.

Pebble ships with the following loader implementations:

- `DelegatingLoader`: Delegates responsibility to a collection of children loaders.
- `ClasspathLoader`: Uses a classloader to search the current classpath.
- `FileLoader`:  Finds templates using a filesystem path. Must provide a mandatory absolute base path.
- `ServletLoader`:  Uses a servlet context to find the template. This is the recommended loader for use within an
application server but is not enabled by default.
- `Servlet5Loader`:  Same as `ServletLoader`, but for Jakarta Servlet 5.0 or newer.
- `MemoryLoader`: Loader that supports inheritance and doesn't require a filesystem. This is useful for applications
- `StringLoader`: Considers the name of the template to be the contents of the template. Should not be used in a production environment. It is primarily for testing and debugging. Many tags may not work when using this loader, such as "extends", "imports", etc.
  that retrieve templates from a database for example.

If you do not provide a custom Loader, Pebble will use an instance of the `ClasspathLoader` by default.

## Pebble Engine Settings

All the settings are set during the construction of the `PebbleEngine` object.

| Setting  | Description | Default |
| --- | --- | --- |
| `cacheActive` | Flag to activate/desactivate template caching | true |
| `templateCache` | An implementation of a ConcurrentMap cache that the Pebble engine will use to cache compiled templates. | Default implementation is `ConcurrentMapTemplateCache` and another implementation based on Caffeine is available (`CaffeineTemplateCache`) |
| `tagCache` | An implementation of a ConcurrentMap cache that the Pebble engine will use for {{ anchor('cache tag', 'cache') }}. | Default implementation is `ConcurrentMapTagCache` and another implementation based on Caffeine is available (`CaffeineTagCache`) |
| `defaultLocale` | The default locale which will be passed to each compiled template. The templates then use this locale for functions such as i18n, etc. A template can also be given a unique locale during evaluation.  | `Locale.getDefault()` |
| `executorService` | An `ExecutorService` that allows the usage of some advanced multithreading features, such as the `parallel` tag. | `null` |
| `loader` | An implementation of the `Loader` interface which is used to find templates. | An implementation of the `ClasspathLoader` |
| `strictVariables` | If set to true, Pebble will throw an exception if you try to access a variable or attribute that does not exist (or an attribute of a null variable). If set to false, your template will treat non-existing variables/attributes as null without ever skipping a beat. | `false` |
| `methodAccessValidator` | Pebble provides three implementations: AllowlistMethodAccessValidator allows explicitly allowed methods (see [Allowlist mode](#allowlist-mode)); BlacklistMethodAccessValidator blocks blacklisted methods and is deprecated; NoOpMethodAccessValidator performs no checks. | `AllowlistMethodAccessValidator` with `allowDefaults()` |
| `literalDecimalTreatedAsInteger` | option for treating literal decimals as `int`. Otherwise it is `long`. | `false` |
| `literalNumbersAsBigDecimals` | option for toggling to enable/disable literal numbers treated as BigDecimals | `false` |
| `greedyMatchMethod` | option for toggling to enable/disable greedy matching mode for finding java method. Reduce the limit of the parameter type, try to find other method which has compatible parameter types. | `false` |
| `maxRenderedSize` | option for limiting the size of the rendered output | `-1 (disabled)` |

### Allowlist mode
By default, Pebble uses an `AllowlistMethodAccessValidator` that denies every method call that has not been explicitly
allowed. Out of the box, only the common, side-effect free JDK types listed below and the objects Pebble exposes itself
(`loop` variables, `template.name`, `locale`) can have their methods called from a template. Calling a method of your
own classes (e.g. `{{ user.name }}`, which resolves to `getName()`) requires you to allow them:

```java
PebbleEngine engine = new PebbleEngine.Builder()
    .methodAccessValidator(AllowlistMethodAccessValidator.builder()
        .allowDefaults()                         // String, boxed primitives, collections, maps, java.time, ...
        .allowTypes(User.class)                  // methods exposed by User, on any User instance
        .allowPackages("com.example.model")      // every public method of classes in this package and sub-packages
        .allowMethod(Order.class, "getTotal")    // only these methods of Order
        .build())
    .build();
```

- `allowDefaults()` allows `String`, `CharSequence`, `Boolean`, `Character`, `Number` and its JDK subclasses
  (`Integer`, `Long`, `BigDecimal`, ...), `Enum`, `Comparable`, `Iterable`, `Collection`, `List`, `Set`, `Map`,
  `Map.Entry`, `Optional`, `UUID`, `Date`, `Locale` and the `java.time` package.
- `allowTypes(...)` only allows the methods exposed by the given type: allowing `List` allows `size()` on an
  `ArrayList`, but not `ArrayList.trimToSize()`.
- Without any rule, every method call is denied, except on the objects Pebble exposes itself.
- Static methods, the `getClass`, `wait`, `notify` and `notifyAll` methods, and any method called on a `Class`,
  `ClassLoader`, reflection object (`Method`, `Field`, `Constructor`), `Runtime`, `Thread` or `ThreadGroup` are always
  denied, whatever the configured rules.

A denied method call throws a `ClassAccessException`. Map key access (`{{ map.key }}`), list and array index access,
public field access, filters, functions and tests do not go through the validator and keep working as usual.

To restore the behavior of Pebble 4.1.3 and earlier, where any method not blacklisted could be called, configure the
deprecated `BlacklistMethodAccessValidator` or switch to the `NoOpMethodAccessValidator` (only do this if your templates and context objects are fully trusted).
It is planned for removal in version 4.3.x; migrate to an explicitly configured allowlist:

```java
PebbleEngine engine = new PebbleEngine.Builder()
    .methodAccessValidator(BlacklistMethodAccessValidator.builder().build())
    .build();

PebbleEngine engine = new PebbleEngine.Builder()
        .methodAccessValidator(NoOpMethodAccessValidator.builder().build())
        .build();
```
