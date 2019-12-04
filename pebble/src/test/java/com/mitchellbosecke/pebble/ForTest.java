package com.mitchellbosecke.pebble;

import com.mitchellbosecke.pebble.error.ParserException;
import com.mitchellbosecke.pebble.loader.StringLoader;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class ForTest {
  @Test
  void testForLengthWithOperation() throws IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader()).strictVariables(false).build();

    String source = "{% for user in users %}{% if loop.index < ( loop.length - 1) %}{{user.username}}{% endif %}{% endfor %}";
    PebbleTemplate template = pebble.getTemplate(source);
    Map<String, Object> context = new HashMap<>();
    List<User> users = new ArrayList<>();
    users.add(new User("Alex"));
    users.add(new User("Bob"));
    users.add(new User("John"));
    context.put("users", users);

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("AlexBob", writer.toString());
  }

  @Test
  void testForRevIndexWithOperation() throws IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader()).strictVariables(false).build();

    String source = "{% for user in users %}{% if (loop.revindex - 1) >= 0 %}{{user.username}}{% endif %}{% endfor %}";
    PebbleTemplate template = pebble.getTemplate(source);
    Map<String, Object> context = new HashMap<>();
    List<User> users = new ArrayList<>();
    users.add(new User("Alex"));
    users.add(new User("Bob"));
    users.add(new User("John"));
    context.put("users", users);

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("AlexBob", writer.toString());
  }

  @Test
  void testInvalidIdentifierName() {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader()).strictVariables(false).build();

    try {
      String source = "{% for <= in users %}{% endfor %}";
      pebble.getTemplate(source);
      fail("Exception not thrown");
    } catch (ParserException e) {
      assertEquals("Unexpected token of value \"<=\" and type OPERATOR, expected token of type NAME ({% for <= in users %}{% endfor %}:1)", e.getMessage());
    }
  }

  public static class User {
    public final String username;

    public User(String username) {
      this.username = username;
    }
  }
}
