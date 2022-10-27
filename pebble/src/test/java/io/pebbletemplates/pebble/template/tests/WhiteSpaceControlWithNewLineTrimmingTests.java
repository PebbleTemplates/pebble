/*
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package io.pebbletemplates.pebble.template.tests;

import io.pebbletemplates.pebble.error.PebbleException;
import io.pebbletemplates.pebble.template.tests.input.PebbleTestItem;
import io.pebbletemplates.pebble.template.tests.input.PebbleTestItemType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests of whitespace control when New Line Trimming is enabled.
 * This mainly affects vertical spacing, i.e. the control of blank lines.
 * With New Line Trimming enabled, Pebble control statements on a line
 * by themselves products a blank line in the output unless
 * the Whitespace Control Modifier is used before and/or after the
 * control statement, i.e. the dash character, e.g.  `{% if item.itemType equals "ITEM_TYPE1" -%}`.
 *
 * However, it can be difficult to get the blank lines to come out as expected
 * in the case that nested control structures are used.
 *
 * @author nathanward
 */
class WhiteSpaceControlWithNewLineTrimmingTests {

  /**
   * All tests in this class use this list of objects as the input to the template.
   */
  private List<PebbleTestItem> listOfObjects = null;

  /**
   * Used by each test and initialized before each test to simplify the
   * test execution, which helps make the purpose of each test clear.
   */
  private PebbleTestContext pebbleTestContext = null;

  @BeforeEach
  void setup() {
    this.pebbleTestContext = new PebbleTestContext();
    this.pebbleTestContext.setNewLineTrimming(false);

    this.listOfObjects = new ArrayList<PebbleTestItem>();
    this.listOfObjects.add(new PebbleTestItem("Item 1", PebbleTestItemType.ITEM_TYPE1));
    this.listOfObjects.add(new PebbleTestItem("Item 2", PebbleTestItemType.ITEM_TYPE2));
    this.listOfObjects.add(new PebbleTestItem("Item 3", PebbleTestItemType.ITEM_TYPE3));
    this.listOfObjects.add(new PebbleTestItem("Item 4", PebbleTestItemType.ITEM_TYPE4, true));

    this.pebbleTestContext.setTemplateInput("items", this.listOfObjects);
  }

  /**
   * Test the whitespace control for a template that has a <code>for</code> loop with a nested
   * <code>if</code> statement that uses a macro and the macro also has an if statement.
   */
  @Test
  void testForLoopWithNestedIfStatementAndMacro() throws PebbleException, IOException {
    String templateOutput = this.pebbleTestContext.executeTemplateFromFile("ForLoopWithNestedIfStatementAndMacro.peb");
    assertThat(templateOutput).contains(this.pebbleTestContext.getExpectedOutput("ForLoopWithNestedIfStatementAndMacro.txt"));
  }

  /**
   * Test the whitespace control for a template that has a <code>for</code> loop with a nested
   * <code>if</code> statement that uses a macro and the macro also has an if statement.
   */
  // TODO not sure why the white space controls work the way that it does for this case.
  @Test
  void testDoubleNestedIfStatement() throws PebbleException, IOException {
    String templateOutput = this.pebbleTestContext.executeTemplateFromFile("DoubleNestedIfStatement.peb");
    assertThat(templateOutput).contains(this.pebbleTestContext.getExpectedOutput("DoubleNestedIfStatement.txt"));
  }

  /**
   * Test the whitespace control for a template that has a <code>for</code> loop with a nested
   * <code>if</code> statement where some text is output for each item in the list.
   */
  @Test
  void testNestedIfStatementWithOneElseIfStatements() throws PebbleException, IOException {
    String templateOutput = this.pebbleTestContext.executeTemplateFromFile("NestedIfStatementWithOneElseIfStatements.peb");
    assertThat(templateOutput).contains(this.pebbleTestContext.getExpectedOutput("NestedIfStatementWithOneElseIfStatements.txt"));
  }

  /**
   * Test the whitespace control for a template that has a <code>for</code> loop with a nested
   * <code>if</code> statement where some text is output for each item in the list.
   */
  @Test
  void testNestedIfStatementWithTwoElseIfStatements() throws PebbleException, IOException {
    String templateOutput = this.pebbleTestContext.executeTemplateFromFile("NestedIfStatementWithTwoElseIfStatements.peb");
    assertThat(templateOutput).contains(this.pebbleTestContext.getExpectedOutput("NestedIfStatementWithTwoElseIfStatements.txt"));
  }

  /**
   * Test the whitespace control for a template that has a <code>for</code> loop with a nested
   * <code>if</code> statement that skips some of the items in the for loop.
   */
  @Test
  void testNestedIfStatementWithThreeElseIfStatements() throws IOException {
    String templateOutput = this.pebbleTestContext.executeTemplateFromFile("NestedIfStatementWithThreeElseIfStatements.peb");
    assertThat(templateOutput).contains(this.pebbleTestContext.getExpectedOutput("NestedIfStatementWithThreeElseIfStatements.txt"));
  }
}
