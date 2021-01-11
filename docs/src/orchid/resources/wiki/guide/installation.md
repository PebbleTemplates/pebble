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

- `ClasspathLoader`: Uses a classloader to search the current classpath.
- `FileLoader`:  Finds templates using a filesystem path.
- `ServletLoader`:  Uses a servlet context to find the template. This is the recommended loader for use within an
application server but is not enabled by default.
- `Servlet5Loader`:  Same as `ServletLoader`, but for Jakarta Servlet 5.0 or newer.
- `StringLoader`: Considers the name of the template to be the contents of the template.
- `DelegatingLoader`: Delegates responsibility to a collection of children loaders.

If you do not provide a custom Loader, Pebble will use an instance of the `DelegatingLoader` by default.
This delegating loader will use a `ClasspathLoader` and a `FileLoader` behind the scenes to find your templates.

## Pebble Engine Settings

All the settings are set during the construction of the `PebbleEngine` object.

| Setting  | Description | Default |
| --- | --- | --- |
| `cacheActive` | Flag to activate/desactivate template caching | true |
| `templateCache` | An implementation of a ConcurrentMap cache that the Pebble engine will use to cache compiled templates. | Default implementation is `ConcurrentMapTemplateCache` and another implementation based on Caffeine is available (`CaffeineTemplateCache`) |
| `tagCache` | An implementation of a ConcurrentMap cache that the Pebble engine will use for {{ anchor('cache tag', 'cache') }}. | Default implementation is `ConcurrentMapTagCache` and another implementation based on Caffeine is available (`CaffeineTagCache`) |
| `defaultLocale` | The default locale which will be passed to each compiled template. The templates then use this locale for functions such as i18n, etc. A template can also be given a unique locale during evaluation.  | `Locale.getDefault()` |
| `executorService` | An `ExecutorService` that allows the usage of some advanced multithreading features, such as the `parallel` tag. | `null` |
| `loader` | An implementation of the `Loader` interface which is used to find templates. | An implementation of the `DelegatingLoader` which uses a `ClasspathLoader` and a `FileLoader` behind the scenes. |
| `strictVariables` | If set to true, Pebble will throw an exception if you try to access a variable or attribute that does not exist (or an attribute of a null variable). If set to false, your template will treat non-existing variables/attributes as null without ever skipping a beat. | `false` |
| `methodAccessValidator` | Pebble provides two implementations. NoOpMethodAccessValidator which do nothing and BlacklistMethodAccessValidator which checks that the method being called is not blacklisted. | `BlacklistMethodAccessValidator` 
| `literalDecimalTreatedAsInteger` | option for treating literal decimals as `int`. Otherwise it is `long`. | `false` |
| `literalNumbersAsBigDecimals` | option for toggling to enable/disable literal numbers treated as BigDecimals | `false` |
| `greedyMatchMethod` | option for toggling to enable/disable greedy matching mode for finding java method. Reduce the limit of the parameter type, try to find other method which has compatible parameter types. | `false` |
| `maxRenderedSize` | option for limiting the size of the rendered output | `-1 (disabled)` |
