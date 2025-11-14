---
---

# Pebble Spring Boot Starter
Spring Boot starter for autoconfiguring Pebble.

## Basic Usage
Add the starter dependency to your pom.xml:

### spring-boot v4
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

### spring-boot v3
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

* a Loader that will pick template files ending in ``.peb`` from ``/templates/`` dir on the classpath
* a PebbleEngine with default settings, configured with the previous loader
* a Spring extension which offers some functionality described below
* a ViewResolver that will output ``text/html`` in ``UTF-8``

PLEASE NOTE: the starter depends on ``spring-boot-starter-webmvc`` or ``spring-boot-starter-webmvcflux`` but is marked
as optional, you'll need to add the dependency yourself or configure Spring MVC appropriately.

## Boot externalized configuration

A number of properties can be defined in Spring Boot externalized configuration, eg. ``application.properties``,
starting with the prefix ``spring.pebble``. See the
corresponding [PebbleProperties.java](https://github.com/PebbleTemplates/pebble/blob/master/pebble-spring/pebble-spring-boot-starter/src/main/java/io/pebbletemplates/boot/autoconfigure/PebbleProperties.java)
for your starter version. Notable properties are:

* ``spring.pebble.prefix``: defines the prefix that will be prepended to the mvc view name. Defaults to ``/templates/``
* ``spring.pebble.suffix``: defines the suffix that will be appended to the mvc view name. Defaults to ``.peb``
* ``spring.pebble.charset``: defines the text encoding that will be used to configure the ViewResolver. Defaults to
  ``UTF-8``
* ``spring.pebble.defaultLocale``: defines the default locale that will be used to configure the PebbleEngine. Defaults
  to ``null``
* ``spring.pebble.strictVariables``: enable or disable the strict variable checking in the PebbleEngine. Defaults to
  ``false``
* ``spring.pebble.greedyMatchMethod``: enable or disable the greedy matching mode for finding java method in the
  PebbleEngine. Defaults to ``false``
* ``spring.pebble.servlet.cache``: enables or disables PebbleEngine caches. Defaults to ``true``
* ``spring.pebble.servlet.contentType``: defines the content type that will be used to configure the ViewResolver. Defaults to
  ``text/html``
* ``spring.pebble.servlet.exposeRequestAttributes``: defines whether all request attributes should be added to the model prior
  to merging with the template for the ViewResolver. Defaults to ``false``
* ``spring.pebble.servlet.exposeSessionAttributes``: defines whether all session attributes should be added to the model prior
  to merging with the template for the ViewResolver. Defaults to ``false``
* ``spring.pebble.reactive.mediaTypes``: Configure the supported media types for Pebble views when used in a reactive Spring WebFlux application. This property allows you to specify a list of MediaType objects that the Pebble view resolver should consider when rendering templates. Defaults to ``null``

## Examples
There is the spring petclinic example which has been migrated to [pebble](https://github.com/PebbleTemplates/spring-petclinic) 

There is also a fully working example project located on [github](https://github.com/PebbleTemplates/pebble-example-spring)
which can be used as a reference. It is a very simple and bare-bones project designed to only portray the basics.
To build the project, simply run `mvn install` and then deploy the resulting war file to a an application container.

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
**PLEASE NOTE**: this loader's prefix and suffix will be both overwritten when the ViewResolver is configured. You should use the externalized configuration for changing these properties.

### Customizing the PebbleEngine
Likewise, you can build a custom engine and make it the default by using the bean name ``pebbleEngine``:
```java
@Bean
public PebbleEngine pebbleEngine() {
   return new PebbleEngine.Builder().build();
}
```

### Customizing the MethodAccessValidator
You can provide your own MethodAccessValidator or switch to NoOpMethodAccessValidator by providing a MethodAccessValidator Bean
```java
@Bean
public MethodAccessValidator methodAccessValidator() {
  return new NoOpMethodAccessValidator();
}
```

### Customizing the ViewResolver
And the same goes for the ViewResolver
```java
@Bean
public PebbleViewResolver pebbleViewResolver() {
   return new PebbleViewResolver();
}
```

For reactive app
```java
@Bean
public PebbleReactiveViewResolver pebbleReactiveViewResolver() {
   return new PebbleReactiveViewResolver(...)
}
```

PLEASE NOTE: you need to change the Loader's prefix and suffix to match the custom ViewResolver's values.

## Features
### Access to Spring beans
Spring beans are available to the template.
```twig
{% verbatim %}{{ beans.beanName }}{% endverbatim %}
```

### Access to http request
HttpServletRequest object is available to the template.
```twig
{% verbatim %}{{ request.contextPath }}{% endverbatim %}
```

### Access to http response
HttpServletResponse is available to the template.
```twig
{% verbatim %}{{ response.contentType }}{% endverbatim %}
```

### Access to http session
HttpSession is available to the template.
```twig
{% verbatim %}{{ session.maxInactiveInterval }}{% endverbatim %}
```

## Spring extension

This extension has many functions for spring validation and the use of message bundle.

### Href function
Function to automatically add the context path to a given url

```twig
{% verbatim %}<a href="{{ href('/foobar') }}">Example</a>{% endverbatim %}
```

### Message function
It achieves the same thing as the i18n function, but instead, it uses the configured spring messageSource, typically the ResourceBundleMessageSource.

```twig
{% verbatim %}
Label = {{ message('label.test') }}
Label with params = {{ message('label.test.params', 'params1', 'params2') }}
{%- endverbatim %}
```

### Spring validations and error messages
6 validations methods and error messages are exposed using spring BindingResult. It needs as a parameter the form name and for a particular field, the field name.

To check if there's any error:
```twig
{% verbatim %}
{{ hasErrors('formName' }}

{{ hasGlobalErrors('formName' }}

{{ hasFieldErrors('formName', 'fieldName' }}
{%- endverbatim %}
```

To output any error:
```twig
{% verbatim %}
{% for err in getAllErrors('formName') %}
    <p>{{ err }}</p>
{% endfor %}

{% for err in getGlobalErrors('formName') %}
    <p>{{ err }}</p>
{% endfor %}

{% for err in getFieldErrors('formName', 'fieldName') %}
    <p>{{ err }}</p>
{% endfor %}
{%- endverbatim %}
```

### Using Pebble for other tasks
The main role of this starter is to configure Pebble for generating MVC View results (the typical HTML). You may define more PebbleEngine/Loader beans for other usage patterns (like generating email bodies). Bear in mind that you should not reuse the default Loader for other Engine instances.

