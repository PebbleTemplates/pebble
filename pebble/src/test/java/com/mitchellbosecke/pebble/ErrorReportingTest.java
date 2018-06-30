/*
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.mitchellbosecke.pebble;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;
import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.hamcrest.core.StringEndsWith.endsWith;

import com.mitchellbosecke.pebble.error.ParserException;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.error.RootAttributeNotFoundException;
import com.mitchellbosecke.pebble.loader.StringLoader;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import java.io.IOException;
import java.io.StringWriter;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class ErrorReportingTest {

  @Rule
  public final ExpectedException thrown = ExpectedException.none();

  @Test
  public void testLineNumberErrorReportingWithUnixNewlines() throws PebbleException {
    //Arrange
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    this.thrown.expect(ParserException.class);
    this.thrown.expectMessage(endsWith(":7)"));

    //Act + Assert
    pebble.getTemplate("test\n\n\ntest\ntest\ntest\n{% error %}\ntest");
  }

  @Test
  public void testLineNumberErrorReportingWithWindowsNewlines() throws PebbleException {
    //Arrange
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    this.thrown.expect(ParserException.class);
    this.thrown.expectMessage(endsWith(":6)"));

    //Act + Assert
    pebble.getTemplate("test\r\n\r\ntest\r\ntest\r\ntest\r\n{% error %}\r\ntest");
  }

  @Test
  public void testLineNumberErrorReportingDuringEvaluation() throws PebbleException, IOException {
    //Arrange
    PebbleEngine pebble = new PebbleEngine.Builder().strictVariables(false).build();

    PebbleTemplate template = pebble.getTemplate("templates/template.errorReporting.peb");

    this.thrown.expect(PebbleException.class);
    this.thrown.expectMessage(endsWith(":8)"));

    //Act + Assert
    template.evaluate(new StringWriter());
  }

  @Test
  public void testMissingRootPropertyInStrictMode() throws PebbleException, IOException {
    //Arrange
    PebbleEngine pebble = new PebbleEngine.Builder().strictVariables(true).build();
    PebbleTemplate template = pebble
        .getTemplate("templates/template.errorReportingMissingRootProperty.peb");

    this.thrown.expect(allOf(
        instanceOf(RootAttributeNotFoundException.class),
        hasProperty("attributeName", is("root"))
        )
    );
    this.thrown.expectMessage(
        "Root attribute [root] does not exist or can not be accessed and strict variables is set to true. (templates/template.errorReportingMissingRootProperty.peb:2)");

    //Act
    template.evaluate(new StringWriter());
  }

}
