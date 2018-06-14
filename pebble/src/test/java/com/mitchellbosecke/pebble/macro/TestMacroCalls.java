package com.mitchellbosecke.pebble.macro;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import org.junit.Test;

/**
 * Test calls of macros
 */
public class TestMacroCalls {


  /**
   * Checks, if macros are called to often
   */
  @Test
  public void testMultipleMacroCalls() throws PebbleException, IOException {
    // Build a pebble engine with one configured filter ("testfilter" - TestFilter.java)
    PebbleEngine pebble = new PebbleEngine.Builder()
        .extension(new PebbleExtension())
        .strictVariables(false).build();
    // Resets the counter of the filter
    TestFilter.counter = 0;
    /*
     * Runs the test scenario:
     * 	index-template with an import of the macro and a call to this macro (Call #1)
     * 	index-templates includes "include.peb"
     *
     *  include.peb with an import of the macro and a call to this macro (Call #2)
     *
     *  We track the number of macro-calls by using a small "Filter" (TestFilter) that just
     *  counts, how often it is called.
     */
    PebbleTemplate template = pebble.getTemplate("templates/macros/index.peb");
    Writer writer = new StringWriter();
    template.evaluate(writer);

    // We expect, that the TestFilter was called 2x
    assertEquals(2, TestFilter.getCounter());
  }

  @Test
  public void testMacroCallsWithImportAs() throws PebbleException, IOException {
    // Build a pebble engine with one configured filter ("testfilter" - TestFilter.java)
    PebbleEngine pebble = new PebbleEngine.Builder()
        .extension(new PebbleExtension())
        .strictVariables(false).build();
    // Resets the counter of the filter
    TestFilter.counter = 0;
    /*
     * Runs the test scenario:
     *  import.as-template with an import-as of the macro and a call to this macro (Call #1)
     *  import.as-templates includes "include.peb"
     *
     *  include.peb with an import of the macro and a call to this macro (Call #2)
     *
     *  We track the number of macro-calls by using a small "Filter" (TestFilter) that just
     *  counts, how often it is called.
     */
    PebbleTemplate template = pebble.getTemplate("templates/macros/import.as.peb");
    Writer writer = new StringWriter();
    template.evaluate(writer);

    // We expect, that the TestFilter was called 2x
    assertEquals(2, TestFilter.getCounter());
  }

  @Test
  public void testMacroCallsWithFromToken() throws PebbleException, IOException {
    // Build a pebble engine with one configured filter ("testfilter" - TestFilter.java)
    PebbleEngine pebble = new PebbleEngine.Builder()
        .extension(new PebbleExtension())
        .strictVariables(false).build();
    // Resets the counter of the filter
    TestFilter.counter = 0;
    /*
     * Runs the test scenario:
     *  from-template with an from-import-as of these macro and a call to these macro (Call #1, Call #3)
     *  from-templates includes "include.peb"
     *
     *  include.peb with an import of the macro and a call to this macro (Call #2)
     *
     *  We track the number of macro-calls by using a small "Filter" (TestFilter) that just
     *  counts, how often it is called.
     */
    PebbleTemplate template = pebble.getTemplate("templates/macros/from.peb");
    Writer writer = new StringWriter();
    template.evaluate(writer);

    // We expect, that the TestFilter was called 3x
    assertEquals(3, TestFilter.getCounter());
  }

  @Test
  public void testInvalidMacroWithFromToken() throws IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().build();

    try {
      PebbleTemplate template = pebble.getTemplate("templates/macros/invalid.from.peb");
      Writer writer = new StringWriter();
      template.evaluate(writer);
      fail("expected PebbleException");
    } catch (PebbleException e) {
      assertEquals(e.getLineNumber(), (Integer) 3);
      assertEquals(e.getFileName(), "templates/macros/invalid.from.peb");
    }
  }

  @Test
  public void testInvalidMacro() throws IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().build();

    try {
      PebbleTemplate template = pebble.getTemplate("templates/macros/invalid.macro.peb");
      Writer writer = new StringWriter();
      template.evaluate(writer);
      fail("expected PebbleException");
    } catch (PebbleException e) {
      assertEquals(e.getLineNumber(), (Integer) 2);
      assertEquals(e.getFileName(), "templates/macros/invalid.macro.peb");
    }
  }

  @Test
  public void testInvalidSameAliasMacroWithFromToken() throws IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().build();

    try {
      PebbleTemplate template = pebble.getTemplate("templates/macros/invalid.from.sameAlias.peb");
      Writer writer = new StringWriter();
      template.evaluate(writer);
      fail("expected PebbleException");
    } catch (PebbleException e) {
      assertTrue(
          e.getPebbleMessage().startsWith("More than one macro can not share the same name"));
    }
  }

  @Test
  public void testInvalidSameAliasMacroWithImportAsToken() throws IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().build();

    try {
      PebbleTemplate template = pebble
          .getTemplate("templates/macros/invalid.import.as.sameAlias.peb");
      Writer writer = new StringWriter();
      template.evaluate(writer);
      fail("expected PebbleException");
    } catch (PebbleException e) {
      assertTrue(e.getPebbleMessage()
          .startsWith("More than one named template can not share the same name"));
    }
  }
}
