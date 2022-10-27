package io.pebbletemplates.pebble.macro;

import io.pebbletemplates.pebble.PebbleEngine;
import io.pebbletemplates.pebble.error.PebbleException;
import io.pebbletemplates.pebble.template.PebbleTemplate;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Test calls of macros
 */
class TestMacroCalls {


  /**
   * Checks, if macros are called to often
   */
  @Test
  void testMultipleMacroCalls() throws PebbleException, IOException {
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
  void testMacroCallsWithImportAs() throws PebbleException, IOException {
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
  void testMacroCallsWithFromToken() throws PebbleException, IOException {
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
  void testInvalidMacroWithFromToken() throws IOException {
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
  void testInvalidMacro() throws IOException {
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
  void testInvalidSameAliasMacroWithFromToken() throws IOException {
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
  void testInvalidSameAliasMacroWithImportAsToken() throws IOException {
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

  @Test
  void testInvalidAliasReferencingUnknownMacro() throws IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().build();

    try {
      PebbleTemplate template = pebble
          .getTemplate("templates/macros/invalid.from.unknownMacro.peb");
      Writer writer = new StringWriter();
      template.evaluate(writer);
      fail("expected PebbleException");
    } catch (PebbleException e) {
      assertEquals(
              "Function or Macro [iDontExist] referenced by alias [macro_test] does not exist.",
              e.getPebbleMessage()
      );
    }
  }
}
