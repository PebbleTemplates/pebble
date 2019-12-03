package com.mitchellbosecke.pebble.boot.autoconfigure;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.boot.NonWebApplication;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.StringWriter;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = NonWebApplication.class)
class NonWebAppTests {

  @Autowired
  private PebbleEngine pebbleEngine;

  @Test
  void testOk() throws Exception {
    StringWriter sw = new StringWriter();
    this.pebbleEngine.getTemplate("hello").evaluate(sw);
    assertThat(sw.toString() != null && !sw.toString().isEmpty()).isTrue();
  }
}
