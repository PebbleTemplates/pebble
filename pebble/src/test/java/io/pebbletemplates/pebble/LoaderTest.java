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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
  void testFileLoader() throws PebbleException, IOException, URISyntaxException {
    Loader<?> loader = new FileLoader();
    loader.setSuffix(".suffix");
    PebbleEngine engine = new PebbleEngine.Builder().loader(loader).strictVariables(false).build();
    URL url = this.getClass().getResource("/templates/template.loaderTest.peb");
    PebbleTemplate template1 = engine.getTemplate(new File(url.toURI()).getPath());
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
}
