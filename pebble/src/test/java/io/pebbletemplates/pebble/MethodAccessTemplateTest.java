package io.pebbletemplates.pebble;

import static org.junit.jupiter.api.Assertions.assertThrows;

import io.pebbletemplates.pebble.error.ClassAccessException;
import io.pebbletemplates.pebble.loader.StringLoader;
import io.pebbletemplates.pebble.attributes.methodaccess.NoOpMethodAccessValidator;
import io.pebbletemplates.pebble.template.PebbleTemplate;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Method;
import java.util.HashMap;
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

  private PebbleEngine unsafePebbleEngine() {
    return new PebbleEngine.Builder().loader(new StringLoader())
        .methodAccessValidator(new NoOpMethodAccessValidator())
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