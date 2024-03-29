package io.pebbletemplates.boot.autoconfigure;

import static org.assertj.core.api.Assertions.assertThat;

import io.pebbletemplates.pebble.PebbleEngine;
import io.pebbletemplates.boot.Application;
import java.io.StringWriter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = Application.class,
    properties = "spring.main.web-application-type=none")
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
