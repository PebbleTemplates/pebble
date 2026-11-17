/*
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package io.pebbletemplates.pebble;

import io.pebbletemplates.pebble.error.LoaderException;
import io.pebbletemplates.pebble.error.PebbleException;
import io.pebbletemplates.pebble.loader.*;
import io.pebbletemplates.pebble.template.PebbleTemplate;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoaderTest {

  @Test
  void testClassLoaderLoader() throws PebbleException, IOException {
    Loader<?> loader = new ClasspathLoader();
    loader.setPrefix("templates");
    loader.setSuffix(".peb");
    PebbleEngine pebble = new PebbleEngine.Builder().loader(loader).strictVariables(false).build();
    PebbleTemplate template1 = pebble.getTemplate("template.loaderTest");
    Writer writer1 = new StringWriter();
    template1.evaluate(writer1);
    assertEquals("SUCCESS", writer1.toString());

  }

  @Test
  void testClassLoaderLoaderWithNestedTemplate() throws PebbleException, IOException {
    Loader<?> loader = new ClasspathLoader();
    loader.setPrefix("templates");
    loader.setSuffix(".peb");
    PebbleEngine engine = new PebbleEngine.Builder().loader(loader).strictVariables(false).build();
    PebbleTemplate template1 = engine.getTemplate("loader/template.loaderTest");
    Writer writer1 = new StringWriter();
    template1.evaluate(writer1);
    assertEquals("SUCCESS", writer1.toString());

  }

  @Test
  void testClassLoaderLoaderWithNestedTemplateInJar() throws PebbleException, IOException {
    URL resource = this.getClass().getResource("/templateinjar.jar");
    assertNotNull(resource);
    Loader<?> loader = new ClasspathLoader(new URLClassLoader(new URL[]{resource}, null));
    loader.setPrefix("templates");
    loader.setSuffix(".peb");
    PebbleEngine engine = new PebbleEngine.Builder().loader(loader).strictVariables(false).build();
    PebbleTemplate template1 = engine.getTemplate("loader/template.loaderTest");
    Writer writer1 = new StringWriter();
    template1.evaluate(writer1);
    assertEquals("SUCCESS", writer1.toString());

  }

  @Test
  void testDelegatingLoader() throws PebbleException, IOException {
    List<Loader<?>> loaders = new ArrayList<>();
    loaders.add(new StringLoaderFailure());
    loaders.add(new StringLoaderOne());
    loaders.add(new StringLoaderTwo());
    Loader<?> loader = new DelegatingLoader(loaders);

    PebbleEngine engine = new PebbleEngine.Builder().loader(loader).strictVariables(false).build();
    PebbleTemplate template = engine.getTemplate("fake template name");
    Writer writer = new StringWriter();
    template.evaluate(writer);

    assertEquals("LOADER ONE", writer.toString());
  }

  @Test
  void testDelegatingLoaderWithFileLoaderThatThrowsInvalidPathException() throws URISyntaxException, IOException {
    String prefix = Paths.get(this.getClass().getClassLoader().getResource("templates").toURI()).toString();

    List<Loader<?>> loaders = new ArrayList<>();
    loaders.add(new FileLoader(prefix)); // Throws InvalidPathException for HTML content
    loaders.add(new StringLoader()); // Should handle HTML content as inline template

    DelegatingLoader loader = new DelegatingLoader(loaders);
    PebbleEngine engine = new PebbleEngine.Builder().loader(loader).build();

    // This simulates passing layout file content (HTML string) to Pebble
    String layoutContent = "<main id=\"content\">Hello {{ name }}</main>";

    // With the fix, DelegatingLoader catches InvalidPathException and delegates to
    // StringLoader
    PebbleTemplate template = engine.getTemplate(layoutContent);
    Writer writer = new StringWriter();

    Map<String, Object> context = new HashMap<>();
    context.put("name", "World");
    template.evaluate(writer, context);

    assertEquals("<main id=\"content\">Hello World</main>", writer.toString());
  }

  @Test
  void testDelegatingLoaderThrowsWhenNoLoaderFindsTemplate() {
    DelegatingLoader loader =
        new DelegatingLoader(Arrays.asList(new StringLoaderFailure(), new StringLoaderFailure()));

    assertThrows(LoaderException.class, () -> loader.getReader(loader.createCacheKey("missing")));
  }

  @Test
  void testDelegatingLoaderDelegatesConfigurationAndResourceQueries() {
    ConfigurableStringLoader first = new ConfigurableStringLoader(false, null);
    ConfigurableStringLoader second = new ConfigurableStringLoader(true, "resolved/path");
    ConfigurableStringLoader third = new ConfigurableStringLoader(true, "ignored/path");
    DelegatingLoader loader = new DelegatingLoader(Arrays.asList(first, second, third));

    loader.setPrefix("templates");
    loader.setSuffix(".peb");
    loader.setCharset("UTF-16");

    for (ConfigurableStringLoader child : Arrays.asList(first, second, third)) {
      assertEquals("templates", child.prefix);
      assertEquals(".peb", child.suffix);
      assertEquals("UTF-16", child.charset);
    }

    assertTrue(loader.resourceExists("present"));
    assertFalse(loader.resourceExists("missing"));
    assertEquals("resolved/path", loader.resolveRelativePath("child", "parent"));
    assertEquals(null, loader.resolveRelativePath(null, "parent"));
  }

  @Test
  void testMemoryLoader() throws PebbleException, IOException {
    MemoryLoader loader = new MemoryLoader();
    PebbleEngine pebble = new PebbleEngine.Builder().loader(loader).strictVariables(false).build();

    loader.addTemplate("home.html", "{% extends \"layout.html\" %}{% block title %} Home {% endblock %}"
            + "{% block content %}"
            + "<h1> Home </h1>"
            + "<p> Welcome to my home page. My name is {{ name }}.</p>"
            + "{% endblock %}");
    loader.addTemplate("layout.html", "<html>"
            + "<head>"
            + "<title>Hello Pebble</title>"
            + "</head>"
            + "<body>"
            + "{% block content %}{% endblock %}"
            + "</body>"
            + "</html>");

    PebbleTemplate template = pebble.getTemplate("home.html");

    Map<String, Object> context = new HashMap<>();
    context.put("name", "Bob");

    Writer writer = new StringWriter();
    template.evaluate(writer, context);

    assertEquals("<html><head><title>Hello Pebble</title></head><body><h1> Home </h1><p> Welcome to my home page. My name is Bob.</p></body></html>", writer.toString());
  }

  @Test
  void testGetLiteralTemplate() throws IOException {
    PebbleEngine engine = new PebbleEngine.Builder().build();
    PebbleTemplate template = engine.getLiteralTemplate("hello {{ object }}");

    Map<String, Object> context = new HashMap<>();
    context.put("object", "world");
    Writer writer = new StringWriter();
    template.evaluate(writer, context);

    assertEquals("hello world", writer.toString());
  }

  /**
   * Always fail to find a template
   */
  private class StringLoaderFailure extends StringLoader {

    @Override
    public Reader getReader(String templateName) throws LoaderException {
      throw new LoaderException(null, "Could not find template ");
    }
  }

  private class StringLoaderOne extends StringLoader {

    @Override
    public Reader getReader(String templateName) throws LoaderException {
      return new StringReader("LOADER ONE");
    }

  }

  private class StringLoaderTwo extends StringLoader {

    @Override
    public Reader getReader(String templateName) throws LoaderException {
      return new StringReader("LOADER TWO");
    }
  }

  private static class ConfigurableStringLoader extends StringLoader {

    private final boolean exists;
    private final String relativePath;
    private String prefix;
    private String suffix;
    private String charset;

    private ConfigurableStringLoader(boolean exists, String relativePath) {
      this.exists = exists;
      this.relativePath = relativePath;
    }

    @Override
    public void setPrefix(String prefix) {
      this.prefix = prefix;
    }

    @Override
    public void setSuffix(String suffix) {
      this.suffix = suffix;
    }

    @Override
    public void setCharset(String charset) {
      this.charset = charset;
    }

    @Override
    public String resolveRelativePath(String relativePath, String anchorPath) {
      return this.relativePath;
    }

    @Override
    public boolean resourceExists(String templateName) {
      return this.exists && "present".equals(templateName);
    }
  }
}
