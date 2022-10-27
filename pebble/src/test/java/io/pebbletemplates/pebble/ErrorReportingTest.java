/*
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package io.pebbletemplates.pebble;

import io.pebbletemplates.pebble.error.ParserException;
import io.pebbletemplates.pebble.error.PebbleException;
import io.pebbletemplates.pebble.error.RootAttributeNotFoundException;
import io.pebbletemplates.pebble.loader.StringLoader;
import io.pebbletemplates.pebble.template.PebbleTemplate;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ErrorReportingTest {


  @Test
  void testLineNumberErrorReportingWithUnixNewlines() throws PebbleException {
    ParserException parserException = assertThrows(ParserException.class, () -> {
      //Arrange
      PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
          .strictVariables(false).build();
      pebble.getTemplate("test\n\n\ntest\ntest\ntest\n{% error %}\ntest");
    });

    assertThat(parserException.getMessage()).endsWith(":7)");
  }

  @Test
  void testLineNumberErrorReportingWithWindowsNewlines() throws PebbleException {
    ParserException parserException = assertThrows(ParserException.class, () -> {
      PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
          .strictVariables(false).build();
      pebble.getTemplate("test\r\n\r\ntest\r\ntest\r\ntest\r\n{% error %}\r\ntest");
    });

    assertThat(parserException.getMessage()).endsWith(":6)");
  }

  @Test
  void testLineNumberErrorReportingDuringEvaluation() throws PebbleException, IOException {
    PebbleException pebbleException = assertThrows(PebbleException.class, () -> {
      PebbleEngine pebble = new PebbleEngine.Builder().strictVariables(false).build();
      PebbleTemplate template = pebble.getTemplate("templates/template.errorReporting.peb");
      template.evaluate(new StringWriter());
    });

    assertThat(pebbleException.getMessage()).endsWith(":8)");
  }

  /**
   * An error should occur when a Pebble Template Engine instance is configured with
   * Strict Variables set to true and a template is executed that contains a references
   * to an undefined property.
   */
  @Test
  void testInvalidPropertyReferenceInStrictMode() throws PebbleException, IOException {
    RootAttributeNotFoundException rootAttributeNotFoundException = assertThrows(RootAttributeNotFoundException.class, () -> {
      PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
          .strictVariables(true)
          .build();

      PebbleTemplate template = pebble.getTemplate("{{ root }}");
      template.evaluate(new StringWriter());
    });

    assertThat(rootAttributeNotFoundException.getAttributeName()).isEqualTo("root");
    assertThat(rootAttributeNotFoundException.getMessage()).contains("Root attribute [root] does not exist or can not be accessed");
  }

}
