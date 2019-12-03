---
---

# Integration with Spring

## Example
There is the spring petclinic example which has been migrated to [pebble](https://github.com/PebbleTemplates/spring-petclinic) 

There is also a fully working example project located on [github](https://github.com/PebbleTemplates/pebble-example-spring)
which can be used as a reference. It is a very simple and bare-bones project designed to only portray the basics.
To build the project, simply run `mvn install` and then deploy the resulting war file to a an application container.

## Setup
Pebble has integration for both versions 3.x, 4.x and 5.x of the Spring Framework, provided by three separate libraries called pebble-spring3, pebble-spring4 and pebble-spring5.

First of all, make sure your project includes the `pebble-spring3`, `pebble-spring4` or `pebble-spring5` dependency.
This will provide the necessary `ViewResolver` and `View` classes.
```xml
<dependency>
	<groupId>io.pebbletemplates</groupId>
	<artifactId>pebble-spring{version}</artifactId>
	<version>{{ site.version }}</version>
</dependency>
```
Secondly, make sure your templates are on the classpath (ex. `/WEB-INF/templates/`). Now you want to define a
`PebbleEngine` bean and a `PebbleViewResolver` in your configuration.
```java
@Configuration
@ComponentScan(basePackages = { "com.example.controller", "com.example.service" })
@EnableWebMvc
public class MvcConfig extends WebMvcConfigurerAdapter {

    @Autowired
    private ServletContext servletContext;

    @Bean
    public Loader templateLoader(){
        return new ServletLoader(this.servletContext);
    }
    
    @Bean
    public SpringExtension springExtension() {
        return new SpringExtension();
    }

    @Bean 
    public PebbleEngine pebbleEngine() {
         return new PebbleEngine.Builder()
                .loader(this.templateLoader())
                .extension(this.springExtension())
                .build();
    }

    @Bean
    public ViewResolver viewResolver() {
        PebbleViewResolver viewResolver = new PebbleViewResolver();
        viewResolver.setPrefix("/WEB-INF/templates/");
        viewResolver.setSuffix(".html");
        viewResolver.setPebbleEngine(this.pebbleEngine());
        return viewResolver;
    }

}
```
Now the methods in your `@Controller` annotated classes can simply return the name of the template as you
normally would if using JSPs:
```java
@Controller
@RequestMapping(value = "/profile")
public class ProfileController {

	@Autowired
	private UserService userService;

	@RequestMapping
	public ModelAndView getUserProfile(@RequestParam("id") long id) {
		ModelAndView mav = new ModelAndView();
		mav.addObject("user", this.userService.getUser(id));
		mav.setViewName("profile");
		return mav;
	}

}
```
The above example will render `\WEB-INF\templates\profile.html` and the "user" object will be available
in the evaluation context.

## Features

### Access to Spring beans
Spring beans are now available to the template.
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

#### Href function
Function to automatically add the context path to a given url

```twig
{% verbatim %}<a href="{{ href('/foobar') }}">Example</a>{% endverbatim %}
```

#### Message function
It achieves the same thing as the i18n function, but instead, it uses the configured spring messageSource, typically the ResourceBundleMessageSource.

```twig
{% verbatim %}
Label = {{ message('label.test') }}
Label with params = {{ message('label.test.params', 'params1', 'params2') }}
{%- endverbatim %}
```

#### Spring validations and error messages
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

## Timer

A timer in PebbleView is available to output the time taken to process a template. Just add the following config to your log4j.xml

```xml
<Logger name="com.mitchellbosecke.pebble.spring.servlet.PebbleView.timer" level="DEBUG" additivity="false">
      <AppenderRef ref="STDOUT" />
</Logger> 
```
