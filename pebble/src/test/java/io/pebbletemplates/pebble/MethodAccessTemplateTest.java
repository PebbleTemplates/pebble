package io.pebbletemplates.pebble;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.pebbletemplates.pebble.attributes.methodaccess.AllowlistMethodAccessValidator;
import io.pebbletemplates.pebble.error.ClassAccessException;
import io.pebbletemplates.pebble.loader.StringLoader;
import io.pebbletemplates.pebble.attributes.methodaccess.NoOpMethodAccessValidator;
import io.pebbletemplates.pebble.template.PebbleTemplate;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

class MethodAccessTemplateTest {

  @Nested
  class ClassTest {

    @Test
    void testIfAccessIsForbiddenWhenAllowUnsafeMethodsIsFalse() {
      PebbleEngine pebble = MethodAccessTemplateTest.this.pebbleEngine();
      assertThrows(ClassAccessException.class, this.templateEvaluation(pebble));
    }

    @Test
    void testIfAccessIsAllowedWhenAllowUnsafeMethodsIsTrue() throws Throwable {
      PebbleEngine pebble = MethodAccessTemplateTest.this.unsafePebbleEngine();
      this.templateEvaluation(pebble).execute();
    }

    private Executable templateEvaluation(PebbleEngine pebble) {
      return () -> {
        String source = "{{clazz.getPackage()}}";
        PebbleTemplate template = pebble.getTemplate(source);
        Map<String, Object> context = new HashMap<>();
        context.put("clazz", Object.class);
        MethodAccessTemplateTest.this.evaluateTemplate(template, context);
      };
    }
  }

  @Nested
  class RuntimeTest {

    @Test
    void testIfAccessIsForbiddenWhenAllowUnsafeMethodsIsFalse() {
      PebbleEngine pebble = MethodAccessTemplateTest.this.pebbleEngine();
      assertThrows(ClassAccessException.class, this.templateEvaluation(pebble));
    }

    @Test
    void testIfAccessIsAllowedWhenAllowUnsafeMethodsIsTrue() throws Throwable {
      PebbleEngine pebble = MethodAccessTemplateTest.this.unsafePebbleEngine();
      this.templateEvaluation(pebble).execute();
    }

    private Executable templateEvaluation(PebbleEngine pebble) {
      return () -> {
        String source = "{{runtime.availableProcessors()}}";
        PebbleTemplate template = pebble.getTemplate(source);
        Map<String, Object> context = new HashMap<>();
        context.put("runtime", Runtime.getRuntime());
        MethodAccessTemplateTest.this.evaluateTemplate(template, context);
      };
    }
  }

  @Nested
  class ThreadTest {

    @Test
    void testIfAccessIsForbiddenWhenAllowUnsafeMethodsIsFalse() {
      PebbleEngine pebble = MethodAccessTemplateTest.this.pebbleEngine();
      assertThrows(ClassAccessException.class, this.templateEvaluation(pebble));
    }

    @Test
    void testIfAccessIsAllowedWhenAllowUnsafeMethodsIsTrue() throws Throwable {
      PebbleEngine pebble = MethodAccessTemplateTest.this.unsafePebbleEngine();
      this.templateEvaluation(pebble).execute();
    }

    private Executable templateEvaluation(PebbleEngine pebble) {
      return () -> {
        String source = "{{thread.sleep(500)}}";
        PebbleTemplate template = pebble.getTemplate(source);
        Map<String, Object> context = new HashMap<>();
        context.put("thread", new Thread());
        MethodAccessTemplateTest.this.evaluateTemplate(template, context);
      };
    }
  }

  @Nested
  class MethodTest {

    @Test
    void testIfAccessIsForbiddenWhenAllowUnsafeMethodsIsFalse() {
      PebbleEngine pebble = MethodAccessTemplateTest.this.pebbleEngine();
      assertThrows(ClassAccessException.class, this.templateEvaluation(pebble));
    }

    @Test
    void testIfAccessIsAllowedWhenAllowUnsafeMethodsIsTrue() throws Throwable {
      PebbleEngine pebble = MethodAccessTemplateTest.this.unsafePebbleEngine();
      this.templateEvaluation(pebble).execute();
    }

    private Executable templateEvaluation(PebbleEngine pebble) {
      return () -> {
        Class<?> systemClass = Class.forName("java.lang.System");
        Method gcMethod = systemClass.getMethod("gc");
        gcMethod.setAccessible(true);
        gcMethod.invoke(null);

        String source = "{{gc.invoke(null, null)}}";
        PebbleTemplate template = pebble.getTemplate(source);
        Map<String, Object> context = new HashMap<>();
        context.put("gc", gcMethod);

        MethodAccessTemplateTest.this.evaluateTemplate(template, context);
      };
    }
  }

  @Nested
  class AllowlistTest {

    private final PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .methodAccessValidator(AllowlistMethodAccessValidator.builder()
            .allowDefaults()
            .allowTypes(User.class)
            .build())
        .build();

    @Test
    void testIfProcessBuilderAccessIsForbidden() {
      Map<String, Object> context = new HashMap<>();
      context.put("pb", new ProcessBuilder("echo"));
      assertThrows(ClassAccessException.class, () -> this.render("{{ pb.start() }}", context));
    }

    @Test
    void testIfNotAllowedTypeAccessIsForbidden() {
      Map<String, Object> context = new HashMap<>();
      context.put("other", new Other());
      assertThrows(ClassAccessException.class, () -> this.render("{{ other.name }}", context));
    }

    @Test
    void testIfAllowedTypesAccessIsAllowed() throws IOException {
      Map<String, Object> context = new HashMap<>();
      context.put("name", "pebble");
      context.put("list", Arrays.asList("a", "b"));
      context.put("map", Collections.singletonMap("key", "value"));
      context.put("user", new User());

      String result = this.render(
          "{{ name.toUpperCase() }}-{{ list.size() }}-{{ map.key }}-{{ map.size() }}-{{ user.name }}",
          context);

      assertEquals("PEBBLE-2-value-1-john", result);
    }

    @Test
    void testIfDefaultEngineDeniesUnlistedTypesButAllowsBuiltins() throws IOException {
      PebbleEngine defaultPebble = new PebbleEngine.Builder().loader(new StringLoader()).build();
      Map<String, Object> context = new HashMap<>();
      context.put("user", new User());
      context.put("items", Arrays.asList("a", "b"));

      assertThrows(ClassAccessException.class,
          () -> defaultPebble.getTemplate("{{ user.name }}").evaluate(new StringWriter(), context));

      Writer writer = new StringWriter();
      defaultPebble.getTemplate(
          "{% for item in items %}{{ loop.index }}{{ item }}{% endfor %}-{{ locale.language }}")
          .evaluate(writer, context, Locale.ENGLISH);
      assertEquals("0a1b-en", writer.toString());

      writer = new StringWriter();
      defaultPebble.getTemplate("{{ template.name }}").evaluate(writer);
      assertEquals("{{ template.name }}", writer.toString());
    }

    private String render(String source, Map<String, Object> context) throws IOException {
      Writer writer = new StringWriter();
      this.pebble.getTemplate(source).evaluate(writer, context);
      return writer.toString();
    }
  }

  public static class User {

    public String getName() {
      return "john";
    }
  }

  public static class Other {

    public String getName() {
      return "other";
    }
  }

  private PebbleEngine unsafePebbleEngine() {
    return new PebbleEngine.Builder().loader(new StringLoader())
        .methodAccessValidator(NoOpMethodAccessValidator.builder().build())
        .build();
  }

  private PebbleEngine pebbleEngine() {
    return new PebbleEngine.Builder().loader(new StringLoader()).build();
  }

  private void evaluateTemplate(PebbleTemplate template, Map<String, Object> context)
      throws IOException {
    Writer writer = new StringWriter();
    template.evaluate(writer, context);
  }
}