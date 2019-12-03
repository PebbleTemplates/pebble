---
---

# Pebble Spring Boot Starter
Spring Boot starter for autoconfiguring Pebble as an MVC ViewResolver.

## Basic Usage
Add the starter dependency to your pom.xml:

### spring-boot v2
```XML
<dependency>
	<groupId>io.pebbletemplates</groupId>
	<artifactId>pebble-spring-boot-starter</artifactId>
	<version>{{ site.version }}</version>
</dependency>
```
Or build.gradle:
```groovy
compile "io.pebbletemplates:pebble-spring-boot-starter:{{ site.version }}"
```

### spring-boot v1
```XML
<dependency>
	<groupId>io.pebbletemplates</groupId>
	<artifactId>pebble-legacy-spring-boot-starter</artifactId>
	<version>{{ site.version }}</version>
</dependency>
```
Or build.gradle:
```groovy
compile "io.pebbletemplates:pebble-legacy-spring-boot-starter:{{ site.version }}"
```

This is enough for autoconfiguration to kick in. This includes:

* a Loader that will pick template files ending in ``.pebble`` from ``/templates/`` dir on the classpath
* a PebbleEngine with default settings, configured with the previous loader
* a ViewResolver that will output ``text/html`` in ``UTF-8``

PLEASE NOTE: the starter depends on ``spring-boot-starter-web`` but is marked as optional, you'll need to add the dependency yourself or configure Spring MVC appropiately.

## Compatibility matrix
Pebble vs tested Boot versions (may work on older Boot releases).

| Pebble Boot Starter | Spring Boot |
| --- | --- |
| 2.2.0+ | 1.2.1+ |
| 2.6.0+ | 2.0.1+ |

## Boot externalized configuration
A number of properties can be defined in Spring Boot externalized configuration, eg. ``application.properties``, starting with the prefix ``pebble``. See the corresponding [PebbleProperties.java](https://github.com/PebbleTemplates/pebble/blob/master/pebble-spring/pebble-spring-boot-starter/src/main/java/com/mitchellbosecke/pebble/boot/autoconfigure/PebbleProperties.java) for your starter version. Notable properties are:

* ``pebble.prefix``: defines the prefix that will be prepended to the mvc view name. Defaults to ``/templates/``
* ``pebble.suffix``: defines the suffix that will be appended to the mvc view name. Defaults to ``.pebble``
* ``pebble.cache``: enables or disables PebbleEngine caches. Defaults to ``true``
* ``pebble.contentType``: defines the content type that will be used to configure the ViewResolver. Defaults to ``text/html``
* ``pebble.encoding``: defines the text encoding that will be used to configure the ViewResolver. Defaults to ``UTF-8``
* ``pebble.exposeRequestAttributes``: defines whether all request attributes should be added to the model prior to merging with the template for the ViewResolver. Defaults to ``false``
* ``pebble.exposeSessionAttributes``: defines whether all session attributes should be added to the model prior to merging with the template for the ViewResolver. Defaults to ``false``
* ``pebble.defaultLocale``: defines the default locale that will be used to configure the PebbleEngine. Defaults to ``null``
* ``pebble.strictVariables``: enable or disable the strict variable checking in the PebbleEngine. Defaults to ``false``

## Customizing Pebble
### Pebble extensions
Extensions defined as beans will be picked up and added to the PebbleEngine automatically:
```java
@Bean
public Extension myPebbleExtension1() {
   return new MyPebbleExtension1();
}

@Bean
public Extension myPebbleExtension2() {
   return new MyPebbleExtension2();
}
```
CAVEAT: Spring will not gather all the beans if they're scattered across multiple @Configuration classes. If you use this mechanism, bundle all Extension @Beans in a single @Configuration class.

### Customizing the Loader
The autoconfigurer looks for a bean named ``pebbleLoader`` in the context. You can define a custom loader with that name and it will be used to configure the default PebbleEngine:
```java
@Bean
public Loader<?> pebbleLoader() {
   return new MyCustomLoader();
}
```
PLEASE NOTE: this loader's prefix and suffix will be both overwritten when the ViewResolver is configured. You should use the externalized configuration for changing these properties.

### Customizing the PebbleEngine
Likewise, you can build a custom engine and make it the default by using the bean name ``pebbleEngine``:
```java
@Bean
public PebbleEngine pebbleEngine() {
   return new PebbleEngine.Builder().build();
}
```

### Customizing the ViewResolver
And the same goes for the ViewResolver, using the bean name ``pebbleViewResolver``:
```java
@Bean
public PebbleViewResolver pebbleViewResolver() {
   return new PebbleViewResolver();
}
```

For reactive app, you need to use the bean name ``pebbleReactiveViewResolver``:
```java
@Bean
public PebbleReactiveViewResolver pebbleReactiveViewResolver() {
   return new PebbleReactiveViewResolver(...);
}
```

PLEASE NOTE: you need to change the Loader's prefix and suffix to match the custom ViewResolver's values.

### Using Pebble for other tasks
The main role of this starter is to configure Pebble for generating MVC View results (the typical HTML). You may define more PebbleEngine/Loader beans for other usage patterns (like generating email bodies). Bear in mind that you should not reuse the default Loader for other Engine instances.
