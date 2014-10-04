/*******************************************************************************
his file is part of Pebble.
 * 
 * Copyright (c) 2014 by Mitchell Bösecke
 *  
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.loader.Loader;
import com.mitchellbosecke.pebble.loader.StringLoader;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertEquals;

public class CoreTagsTest extends AbstractTest {

	@Test
	public void testBlock() throws PebbleException, IOException {
		PebbleTemplate template = pebble.getTemplate("template.grandfather.peb");
		Writer writer = new StringWriter();
		template.evaluate(writer);
	}

	/**
	 * This ensures that block inheritance works properly even if it skips a
	 * generation.
	 * 
	 * @throws PebbleException
	 * @throws IOException
	 */
	@Test
	public void skipGenerationBlock() throws PebbleException, IOException {
		PebbleTemplate template = pebble.getTemplate("template.skipGenerationBlock1.peb");
		Writer writer = new StringWriter();
		template.evaluate(writer);
		assertEquals("success", writer.toString());
	}

	@Test
	public void testIf() throws PebbleException, IOException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);
		pebble.setStrictVariables(false);

		String source = "{% if false or steve == true  %}yes{% else %}no{% endif %}";
		PebbleTemplate template = pebble.getTemplate(source);

		Map<String, Object> context = new HashMap<>();
		context.put("yes", true);

		Writer writer = new StringWriter();
		template.evaluate(writer, context);
		assertEquals("no", writer.toString());
	}

	/**
	 * Issue #34
	 *
	 * @throws PebbleException
	 * @throws IOException
	 */
	@Test
	public void testIfThenElse() throws PebbleException, IOException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);
		pebble.setStrictVariables(false);

		String source = "{% if alpha %}alpha{% elseif beta %}beta{% else %}gamma{% endif %}";
		PebbleTemplate template = pebble.getTemplate(source);

		Map<String, Object> context = new HashMap<>();
		context.put("alpha", true);
		context.put("beta", false);

		Writer writer = new StringWriter();
		template.evaluate(writer, context);
		assertEquals("alpha", writer.toString());
	}

	@Test
	public void testIfWithDirectProperty() throws PebbleException, IOException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);
		pebble.setStrictVariables(false);

		String source = "{% if variable %}yes{% else %}no{% endif %}";
		PebbleTemplate template = pebble.getTemplate(source);
		Map<String, Object> context = new HashMap<>();
		context.put("variable", true);

		Writer writer = new StringWriter();
		template.evaluate(writer, context);
		assertEquals("yes", writer.toString());
	}

	@Test
	public void testFlush() throws PebbleException, IOException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		String source = "start{% flush %}end";
		PebbleTemplate template = pebble.getTemplate(source);

		FlushAwareWriter writer = new FlushAwareWriter();
		template.evaluate(writer);
		List<String> flushedBuffers = writer.getFlushedBuffers();

		assertEquals("start", flushedBuffers.get(0));
		assertEquals("startend", flushedBuffers.get(1));
	}

	@Test
	public void testFor() throws PebbleException, IOException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		String source = "{% for user in users %}{% if loop.index == 0 %}[{{ loop.length }}]{% endif %}{{ loop.index }}{{ user.username }}{% endfor %}";
		PebbleTemplate template = pebble.getTemplate(source);
		Map<String, Object> context = new HashMap<>();
		List<User> users = new ArrayList<>();
		users.add(new User("Alex"));
		users.add(new User("Bob"));
		context.put("users", users);

		Writer writer = new StringWriter();
		template.evaluate(writer, context);
		assertEquals("[2]0Alex1Bob", writer.toString());
	}

	/**
	 * Issue #15
	 * 
	 * @throws PebbleException
	 * @throws IOException
	 */
	@Test
	public void testForIteratingOverProperty() throws PebbleException, IOException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		String source = "{% for user in classroom.users %}{{ user.username }}{% endfor %}";
		PebbleTemplate template = pebble.getTemplate(source);
		Map<String, Object> context = new HashMap<>();
		List<User> users = new ArrayList<>();
		users.add(new User("Alex"));
		users.add(new User("Bob"));
		Classroom classroom = new Classroom();
		classroom.setUsers(users);
		context.put("classroom", classroom);

		Writer writer = new StringWriter();
		template.evaluate(writer, context);
		assertEquals("AlexBob", writer.toString());
	}

	@Test
	public void testForWithNullIterable() throws PebbleException, IOException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		String source = "{% for user in users %}{{ loop.index }}{% endfor %}";
		PebbleTemplate template = pebble.getTemplate(source);

		Map<String, Object> context = new HashMap<>();
		context.put("users", null);

		Writer writer = new StringWriter();
		template.evaluate(writer, context);
		assertEquals("", writer.toString());
	}
	
	@Test
	public void testForWithArray() throws PebbleException, IOException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		String source = "{% for user in users %}{{ user }}{% endfor %}";
		PebbleTemplate template = pebble.getTemplate(source);

		Map<String, Object> context = new HashMap<>();
		
		String[] users = new String[3];
		users[0] = "User 1";
		users[1] = "User 2";
		users[2] = "User 3";
		context.put("users", users);

		Writer writer = new StringWriter();
		template.evaluate(writer, context);
		assertEquals("User 1User 2User 3", writer.toString());
	}

	/**
	 * There were compilation issues when having two for loops in the same
	 * template due to the same variable name being declared twice.
	 * 
	 * @throws PebbleException
	 */
	@Test
	public void multipleForLoops() throws PebbleException, IOException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		String source = "" + "{% for user in users %}{% endfor %}" + "{% for user in users %}{% endfor %}";
		PebbleTemplate template = pebble.getTemplate(source);
		Map<String, Object> context = new HashMap<>();
		List<User> users = new ArrayList<>();
		users.add(new User("Alex"));
		users.add(new User("Bob"));
		context.put("users", users);

		Writer writer = new StringWriter();
		template.evaluate(writer, context);
	}

	@Test
	public void testForElse() throws PebbleException, IOException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		String source = "{% for user in users %}{{ user.username }}{% else %}yes{% endfor %}";
		PebbleTemplate template = pebble.getTemplate(source);
		Map<String, Object> context = new HashMap<>();
		List<User> users = new ArrayList<>();
		context.put("users", users);

		Writer writer = new StringWriter();
		template.evaluate(writer, context);
		assertEquals("yes", writer.toString());
	}

	@Test
	public void testMacro() throws PebbleException, IOException {
		PebbleTemplate template = pebble.getTemplate("template.macro1.peb");

		Writer writer = new StringWriter();
		template.evaluate(writer);
		assertEquals("	<input name=\"company\" value=\"google\" type=\"text\" />\n", writer.toString());
	}
	
	/**
	 * This ensures that macro inheritance works properly even if it skips a
	 * generation.
	 * 
	 * @throws PebbleException
	 * @throws IOException
	 */
	@Test
	public void skipGenerationMacro() throws PebbleException, IOException {
		PebbleTemplate template = pebble.getTemplate("template.skipGenerationMacro1.peb");
		Writer writer = new StringWriter();
		template.evaluate(writer);
		assertEquals("success", writer.toString());
	}

	@Test(expected = RuntimeException.class)
	public void testMacrosWithSameName() throws PebbleException, IOException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);
		PebbleTemplate template = pebble
				.getTemplate("{{ test() }}{% macro test(one) %}ONE{% endmacro %}{% macro test(one,two) %}TWO{% endmacro %}");

		Writer writer = new StringWriter();
		template.evaluate(writer);
		assertEquals("	<input name=\"company\" value=\"google\" type=\"text\" />\n", writer.toString());
	}

	@Test
	public void testMacroWithDefaultArgument() throws PebbleException, IOException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);
		PebbleTemplate template = pebble
				.getTemplate("{{ input(name='country') }}{% macro input(type='text', name) %}{{ type }} {{ name }}{% endmacro %}");

		Writer writer = new StringWriter();
		template.evaluate(writer);
		assertEquals("text country", writer.toString());
	}

	/**
	 * There was an issue where the second invokation of a macro did not have
	 * access to the original arguments any more.
	 * 
	 * @throws PebbleException
	 * @throws IOException
	 */
	@Test
	public void testMacroInvokedTwice() throws PebbleException, IOException {
		PebbleTemplate template = pebble.getTemplate("template.macroDouble.peb");

		Writer writer = new StringWriter();
		template.evaluate(writer);
		assertEquals("onetwo", writer.toString());
	}

	@Test
	public void testMacroInvocationWithoutAllArguments() throws PebbleException, IOException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);
		PebbleTemplate template = pebble
				.getTemplate("{{ test('1') }}{% macro test(one,two) %}{{ one }}{{ two }}{% endmacro %}");

		Writer writer = new StringWriter();
		template.evaluate(writer);
		assertEquals("1", writer.toString());
	}

	/**
	 * I was once writing macro output directly to writer which was preventing
	 * output from being filtered. I have fixed this now.
	 * 
	 * @throws PebbleException
	 */
	@Test
	public void testMacroBeingFiltered() throws PebbleException, IOException {
		PebbleTemplate template = pebble.getTemplate("template.macro3.peb");

		Writer writer = new StringWriter();
		template.evaluate(writer);
		assertEquals("HELLO\n", writer.toString());
	}

	@Test
	public void testImportWithinBlock() throws PebbleException, IOException {
		PebbleTemplate template = pebble.getTemplate("template.importWithinBlock.peb");

		Writer writer = new StringWriter();
		template.evaluate(writer);
		assertEquals("\t<input name=\"company\" value=\"forcorp\" type=\"text\" />\n", writer.toString());
	}

	@Test
	public void testImportFile() throws PebbleException, IOException {
		PebbleTemplate template = pebble.getTemplate("template.macro2.peb");

		Writer writer = new StringWriter();
		template.evaluate(writer);
		assertEquals("	<input name=\"company\" value=\"forcorp\" type=\"text\" />\n", writer.toString());
	}

	@Test
	public void testImportInChildTemplateOutsideOfBlock() throws PebbleException, IOException {
		PebbleTemplate template = pebble.getTemplate("template.macro.child.peb");

		Writer writer = new StringWriter();
		template.evaluate(writer);
		assertEquals("	<input name=\"company\" value=\"forcorp\" type=\"text\" />\n", writer.toString());
	}

	@Test(expected = PebbleException.class)
	public void testNonExistingMacroOrFunction() throws PebbleException, IOException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);
		PebbleTemplate template = pebble.getTemplate("{{ nonExisting('test') }}");

		Writer writer = new StringWriter();
		template.evaluate(writer);
	}

	@Test
	public void testInclude() throws PebbleException, IOException {
		PebbleTemplate template = pebble.getTemplate("template.include1.peb");

		Writer writer = new StringWriter();
		template.evaluate(writer);
		assertEquals("TEMPLATE2\nTEMPLATE1\nTEMPLATE2\n", writer.toString());
	}
	
	/**
	 * There was an issue when including a template that had it's own inheritance
	 * chain.
	 * @throws PebbleException
	 * @throws IOException
	 */
	@Test
	public void testIncludeInheritance() throws PebbleException, IOException {
		PebbleTemplate template = pebble.getTemplate("template.includeInheritance1.peb");

		Writer writer = new StringWriter();
		template.evaluate(writer);
		assertEquals("success", writer.toString());
	}

	@Test
	public void testIncludeWithinBlock() throws PebbleException, IOException {
		PebbleTemplate template = pebble.getTemplate("template.includeWithinBlock.peb");

		Writer writer = new StringWriter();
		template.evaluate(writer);
		assertEquals("TEMPLATE2\nTEMPLATE1\n", writer.toString());
	}

	/**
	 * Issue #16
	 * 
	 * @throws PebbleException
	 * @throws IOException
	 */
	@Test
	public void testIncludePropagatesContext() throws PebbleException, IOException {
		PebbleTemplate template = pebble.getTemplate("template.includePropagatesContext.peb");
		Writer writer = new StringWriter();
		Map<String, Object> context = new HashMap<>();
		context.put("name", "Mitchell");
		template.evaluate(writer, context);
		assertEquals("Mitchell", writer.toString());
	}

	/**
	 * Ensures that when including a template it is safe to have conflicting
	 * block names.
	 * 
	 * @throws PebbleException
	 * @throws IOException
	 */
	@Test
	public void testIncludeOverridesBlocks() throws PebbleException, IOException {
		PebbleTemplate template = pebble.getTemplate("template.includeOverrideBlock.peb");

		Writer writer = new StringWriter();
		template.evaluate(writer);
		assertEquals("TWO\nONE\nTWO\n", writer.toString());
	}

	@Test
	public void testSet() throws PebbleException, IOException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		String source = "{% set name = 'alex'  %}{{ name }}";
		PebbleTemplate template = pebble.getTemplate(source);
		Map<String, Object> context = new HashMap<>();
		context.put("name", "steve");

		Writer writer = new StringWriter();
		template.evaluate(writer, context);
		assertEquals("alex", writer.toString());
	}

	@Test
	public void testSetInChildTemplateOutsideOfBlock() throws PebbleException, IOException {
		PebbleTemplate template = pebble.getTemplate("template.set.child.peb");

		Writer writer = new StringWriter();
		template.evaluate(writer);
		assertEquals("SUCCESS", writer.toString());
	}

	@Test(timeout = 3000)
	public void testParallel() throws PebbleException, IOException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);
		pebble.setExecutorService(Executors.newCachedThreadPool());
		String source = "beginning {% parallel %}{{ slowObject.first }}{% endparallel %} middle {% parallel %}{{ slowObject.second }}{% endparallel %} end {% parallel %}{{ slowObject.third }}{% endparallel %}";
		PebbleTemplate template = pebble.getTemplate(source);

		Writer writer = new StringWriter();
		Map<String, Object> context = new HashMap<>();
		context.put("slowObject", new SlowObject());
		template.evaluate(writer, context);

		assertEquals("beginning first middle second end third", writer.toString());

	}

	public class SlowObject {
		public String first() {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return "first";
		}

		public String second() {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return "second";
		}

		public String third() {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return "third";
		}
	}

	public class User {
		private final String username;

		public User(String username) {
			this.username = username;
		}

		public String getUsername() {
			return username;
		}
	}

	public class Classroom {
		private List<User> users = new ArrayList<>();

		public List<User> getUsers() {
			return users;
		}

		public void setUsers(List<User> users) {
			this.users = users;
		}
	}

	public class FlushAwareWriter extends StringWriter {
		private List<String> buffers = new ArrayList<>();

		@Override
		public void flush() {
			buffers.add(this.getBuffer().toString());
			super.flush();
		}

		public List<String> getFlushedBuffers() {
			return buffers;
		}
	}
}
