/*
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.mitchellbosecke.pebble.template.tests;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.template.tests.input.PebbleTestItem;
import com.mitchellbosecke.pebble.template.tests.input.PebbleTestItemType;

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
public class WhiteSpaceControlWithNewLineTrimmingTests {

	/**
	 * All tests in this class use this list of objects as the input to the template.
	 */
	private List<PebbleTestItem> listOfObjects = new ArrayList<PebbleTestItem>();
	
	/**
	 * Used by each test and initialized before each test to simplify the 
	 * test execution, which helps make the purpose of each test clear.
	 */
	private PebbleTestContext pebbleTestContext = null;
	
	@Before
	public void setup() {
		listOfObjects.add(new PebbleTestItem("Item 1", PebbleTestItemType.ITEM_TYPE1));
		listOfObjects.add(new PebbleTestItem("Item 2", PebbleTestItemType.ITEM_TYPE2));
		listOfObjects.add(new PebbleTestItem("Item 3", PebbleTestItemType.ITEM_TYPE3, true));
		listOfObjects.add(new PebbleTestItem("Item 4", PebbleTestItemType.ITEM_TYPE4));
		
		pebbleTestContext = new PebbleTestContext();
		pebbleTestContext.setNewLineTrimming(false);
		pebbleTestContext.setTemplateInput("items", listOfObjects);
	}

	/**
	 * Test the whitespace control for a template that has a <code>for</code> loop with a nested
	 * <code>if</code> statement that uses a macro and the macro also has an if statement. 
	 * 
	 * @throws PebbleException
	 * @throws IOException
	 */
	@Test
	public void testForLoopWithNestedIfStatementAndMacro() throws PebbleException, IOException {;		
		String templateOutput = pebbleTestContext.executeTemplateFromFile("ForLoopWithNestedIfStatementAndMacro.peb");
		assertThat(templateOutput).contains(pebbleTestContext.getExpectedOutput("ForLoopWithNestedIfStatementAndMacro.txt"));
	}
	
	/**
	 * Test the whitespace control for a template that has a <code>for</code> loop with a nested
	 * <code>if</code> statement where some text is output for each item in the list.
	 * 
	 * @throws PebbleException
	 * @throws IOException
	 */
	@Test
	public void testNestedIfStatementWithOneElseIfStatements() throws PebbleException, IOException {
		String templateOutput = pebbleTestContext.executeTemplateFromFile("NestedIfStatementWithOneElseIfStatements.peb");
		assertThat(templateOutput).contains(pebbleTestContext.getExpectedOutput("NestedIfStatementWithOneElseIfStatements.txt"));
	}
	
	/**
	 * Test the whitespace control for a template that has a <code>for</code> loop with a nested
	 * <code>if</code> statement where some text is output for each item in the list.
	 * 
	 * @throws PebbleException
	 * @throws IOException
	 */
	@Test
	public void testNestedIfStatementWithTwoElseIfStatements() throws PebbleException, IOException {
		String templateOutput = pebbleTestContext.executeTemplateFromFile("NestedIfStatementWithTwoElseIfStatements.peb");
		assertThat(templateOutput).contains(pebbleTestContext.getExpectedOutput("NestedIfStatementWithTwoElseIfStatements.txt"));
	}
	
	/**
	 * Test the whitespace control for a template that has a <code>for</code> loop with a nested
	 * <code>if</code> statement that skips some of the items in the for loop.
	 * 
	 * @throws PebbleException
	 * @throws IOException
	 */
	@Test
	public void testNestedIfStatementWithThreeElseIfStatements() throws IOException {
		String templateOutput = pebbleTestContext.executeTemplateFromFile("NestedIfStatementWithThreeElseIfStatements.peb");
		assertThat(templateOutput).contains(pebbleTestContext.getExpectedOutput("NestedIfStatementWithThreeElseIfStatements.txt"));
	}
}
