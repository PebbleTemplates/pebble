package com.mitchellbosecke.pebble;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.mitchellbosecke.pebble.error.ClassAccessException;
import com.mitchellbosecke.pebble.loader.StringLoader;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Constructor;
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
      PebbleEngine pebble = pebbleEngine();
      assertThrows(ClassAccessException.class, templateEvaluation(pebble));
    }

    @Test
    void testIfAccessIsAllowedWhenAllowUnsafeMethodsIsTrue() throws Throwable {
      PebbleEngine pebble = unsafePebbleEngine();
      templateEvaluation(pebble).execute();
    }

    private Executable templateEvaluation(PebbleEngine pebble) {
      return () -> {
        String source = "{{clazz.getPackage()}}";
        PebbleTemplate template = pebble.getTemplate(source);
        Map<String, Object> context = new HashMap<>();
        context.put("clazz", Object.class);
        evaluateTemplate(template, context);
      };
    }
  }

  @Nested
  class RuntimeTest {

    @Test
    void testIfAccessIsForbiddenWhenAllowUnsafeMethodsIsFalse() {
      PebbleEngine pebble = pebbleEngine();
      assertThrows(ClassAccessException.class, templateEvaluation(pebble));
    }

    @Test
    void testIfAccessIsAllowedWhenAllowUnsafeMethodsIsTrue() throws Throwable {
      PebbleEngine pebble = unsafePebbleEngine();
      templateEvaluation(pebble).execute();
    }

    private Executable templateEvaluation(PebbleEngine pebble) {
      return () -> {
        String source = "{{runtime.availableProcessors()}}";
        PebbleTemplate template = pebble.getTemplate(source);
        Map<String, Object> context = new HashMap<>();
        context.put("runtime", Runtime.getRuntime());
        evaluateTemplate(template, context);
      };
    }
  }

  @Nested
  class ThreadTest {

    @Test
    void testIfAccessIsForbiddenWhenAllowUnsafeMethodsIsFalse() {
      PebbleEngine pebble = pebbleEngine();
      assertThrows(ClassAccessException.class, templateEvaluation(pebble));
    }

    @Test
    void testIfAccessIsAllowedWhenAllowUnsafeMethodsIsTrue() throws Throwable {
      PebbleEngine pebble = unsafePebbleEngine();
      templateEvaluation(pebble).execute();
    }

    private Executable templateEvaluation(PebbleEngine pebble) {
      return () -> {
        String source = "{{thread.sleep(500)}}";
        PebbleTemplate template = pebble.getTemplate(source);
        Map<String, Object> context = new HashMap<>();
        context.put("thread", new Thread());
        evaluateTemplate(template, context);
      };
    }
  }

  @Nested
  class SystemTest {

    @Test
    void testIfAccessIsForbiddenWhenAllowUnsafeMethodsIsFalse() {
      PebbleEngine pebble = pebbleEngine();
      assertThrows(ClassAccessException.class, templateEvaluation(pebble));
    }

    @Test
    void testIfAccessIsAllowedWhenAllowUnsafeMethodsIsTrue() throws Throwable {
      PebbleEngine pebble = unsafePebbleEngine();
      templateEvaluation(pebble).execute();
    }

    private Executable templateEvaluation(PebbleEngine pebble) {
      return () -> {
        Class<?> systemClass = Class.forName("java.lang.System");
        systemClass.getMethod("gc").setAccessible(true);

        Constructor<?> constructor = systemClass.getDeclaredConstructor();
        constructor.setAccessible(true);
        System system = (System) constructor.newInstance();

        String source = "{{system.gc()}}";
        PebbleTemplate template = pebble.getTemplate(source);
        Map<String, Object> context = new HashMap<>();
        context.put("system", system);
        evaluateTemplate(template, context);
      };
    }
  }

  @Nested
  class MethodTest {

    @Test
    void testIfAccessIsForbiddenWhenAllowUnsafeMethodsIsFalse() {
      PebbleEngine pebble = pebbleEngine();
      assertThrows(ClassAccessException.class, templateEvaluation(pebble));
    }

    @Test
    void testIfAccessIsAllowedWhenAllowUnsafeMethodsIsTrue() throws Throwable {
      PebbleEngine pebble = unsafePebbleEngine();
      templateEvaluation(pebble).execute();
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

        evaluateTemplate(template, context);
      };
    }
  }

  private PebbleEngine unsafePebbleEngine() {
    return new PebbleEngine.Builder().loader(new StringLoader())
        .allowUnsafeMethods(true).build();
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
