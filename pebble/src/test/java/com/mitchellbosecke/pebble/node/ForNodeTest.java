package com.mitchellbosecke.pebble.node;

import static org.junit.Assert.assertEquals;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.loader.StringLoader;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;

public class ForNodeTest {

  @Test
  public void testVariableScope() throws Exception {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    StringBuilder source = new StringBuilder("{% set fooList = range(1, 1) %}");
    source.append("{% for item in fooList %}");
    source.append("{% set foo1 = 'fooValue' %}");
    source.append("Foo1 value : {{ foo1 }}");
    source.append("{% endfor %}");
    source.append("Foo1 value : {{ foo1 }}");

    source.append("{% for item in fooList %}");
    source.append("{% set foo2 = 'fooValue2' %}");
    source.append("Foo2 value : {{ foo2 }}");
    source.append("{% endfor %}");
    source.append("Foo2 value : {{ foo2 }}");

    PebbleTemplate template = pebble.getTemplate(source.toString());

    Map<String, Object> context = new HashMap<>();

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("Foo1 value : fooValueFoo1 value : Foo2 value : fooValue2Foo2 value : ",
        writer.toString());
  }

  @Test
  public void testNestedLoop() throws Exception {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    StringBuilder source = new StringBuilder("{% for i in 0..2 %}");
    source.append("{% for j in 0..2 %}");
    source.append("i={{ i }} j={{ j }} ");
    source.append("{% endfor %}");
    source.append("{% endfor %}");
    source.append("i={{ i }} j={{ j }} ");

    PebbleTemplate template = pebble.getTemplate(source.toString());

    Map<String, Object> context = new HashMap<>();

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("i=0 j=0 i=0 j=1 i=0 j=2 i=1 j=0 i=1 j=1 i=1 j=2 i=2 j=0 i=2 j=1 i=2 j=2 i= j= ",
        writer.toString());
  }

  @Test
  public void testLoopIndex() throws Exception {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    StringBuilder source = new StringBuilder("{% for i in 0..2 %}");
    source.append("{% for j in 0..2 %}");
    source.append("inner={{ loop.index }} ");
    source.append("{% endfor %}");
    source.append("outer={{ loop.index }} ");
    source.append("{% endfor %}");
    source.append("outside loop={{ loop.index }} ");

    PebbleTemplate template = pebble.getTemplate(source.toString());

    Map<String, Object> context = new HashMap<>();

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals(
        "inner=0 inner=1 inner=2 outer=0 inner=0 inner=1 inner=2 outer=1 inner=0 inner=1 inner=2 outer=2 outside loop= ",
        writer.toString());
  }

  @Test
  public void loopOverEnumeration() throws Exception {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    StringBuilder source = new StringBuilder("{% for item in fooEnumeration %}");
    source.append("{{ item }}");
    source.append("{% endfor %}");

    PebbleTemplate template = pebble.getTemplate(source.toString());

    Map<String, Object> context = new HashMap<>();
    context.put("fooEnumeration", new Enumeration<String>() {

      private int value = 0;

      @Override
      public boolean hasMoreElements() {
        return value < 10;
      }

      @Override
      public String nextElement() {
        return String.valueOf(value++);
      }
    });

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("0123456789", writer.toString());
  }

}