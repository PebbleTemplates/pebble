package com.mitchellbosecke.pebble;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.mitchellbosecke.pebble.error.MethodAccessException;
import com.mitchellbosecke.pebble.loader.StringLoader;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class MethodAccessTest {

  @Test
  void testIfAccessToClassMethodsIsForbiddenWhenAllowUnsafeMethodsIsFalse() {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader()).build();

    String source = "{{clazz.getPackage()}}";
    PebbleTemplate template = pebble.getTemplate(source);
    Map<String, Object> context = new HashMap<>();
    context.put("clazz", Object.class);
    Writer writer = new StringWriter();

    assertThrows(MethodAccessException.class, () -> template.evaluate(writer, context));
  }

  @Test
  void testIfAccessToClassMethodsIsAllowedWhenAllowUnsafeMethodsIsTrue() throws IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .allowUnsafeMethods(true).build();

    String source = "{{clazz.getPackage()}}";
    PebbleTemplate template = pebble.getTemplate(source);
    Map<String, Object> context = new HashMap<>();
    context.put("clazz", Object.class);
    Writer writer = new StringWriter();

    template.evaluate(writer, context);
  }

  @Test
  void testIfAccessToRuntimeMethodsIsForbiddenWhenAllowUnsafeMethodsIsFalse() {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader()).build();

    String source = "{{runtime.availableProcessors()}}";
    PebbleTemplate template = pebble.getTemplate(source);
    Map<String, Object> context = new HashMap<>();
    context.put("runtime", Runtime.getRuntime());
    Writer writer = new StringWriter();

    assertThrows(MethodAccessException.class, () -> template.evaluate(writer, context));
  }

  @Test
  void testIfAccessToRuntimeMethodsIsAllowedWhenAllowUnsafeMethodsIsTrue() throws IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .allowUnsafeMethods(true).build();

    String source = "{{runtime.availableProcessors()}}";
    PebbleTemplate template = pebble.getTemplate(source);
    Map<String, Object> context = new HashMap<>();
    context.put("runtime", Runtime.getRuntime());
    Writer writer = new StringWriter();

    template.evaluate(writer, context);
  }
}
