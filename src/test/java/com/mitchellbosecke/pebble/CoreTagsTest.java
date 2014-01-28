/*******************************************************************************
 * Copyright (c) 2013 by Mitchell Bösecke
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

import org.junit.Test;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.loader.Loader;
import com.mitchellbosecke.pebble.loader.StringLoader;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

public class CoreTagsTest extends AbstractTest {

	@Test
	public void testBlock() throws PebbleException, IOException {
		PebbleTemplate template = pebble.compile("template.grandfather.peb");
		Writer writer = new StringWriter();
		template.evaluate(writer);
	}

	@Test
	public void testIf() throws PebbleException, IOException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);
		pebble.setStrictVariables(false);

		String source = "{% if false or steve == true  %}yes{% else %}no{% endif %}";
		PebbleTemplate template = pebble.compile(source);
		Map<String, Object> context = new HashMap<>();
		context.put("yes", true);

		Writer writer = new StringWriter();
		template.evaluate(writer, context);
		assertEquals("no", writer.toString());
	}

	@Test
	public void testFlush() throws PebbleException, IOException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		String source = "start{% flush %}end";
		PebbleTemplate template = pebble.compile(source);

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
		PebbleTemplate template = pebble.compile(source);
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
		PebbleTemplate template = pebble.compile(source);
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
		PebbleTemplate template = pebble.compile(source);
		Map<String, Object> context = new HashMap<>();
		List<User> users = new ArrayList<>();
		context.put("users", users);

		Writer writer = new StringWriter();
		template.evaluate(writer, context);
		assertEquals("yes", writer.toString());
	}

	@Test
	public void testMacro() throws PebbleException, IOException {
		PebbleTemplate template = pebble.compile("template.macro1.peb");

		Writer writer = new StringWriter();
		template.evaluate(writer);
		assertEquals("	<input name=\"company\" value=\"google\" type=\"text\" />\n", writer.toString());
	}
	
	/**
	 * There was an issue where the second invokation of a macro
	 * did not have access to the original arguments any more.
	 * 
	 * @throws PebbleException
	 * @throws IOException
	 */
	@Test
	public void testMacroInvokedTwice() throws PebbleException, IOException {
		PebbleTemplate template = pebble.compile("template.macroDouble.peb");

		Writer writer = new StringWriter();
		template.evaluate(writer);
		assertEquals("onetwo", writer.toString());
	}

	/**
	 * I was once writing macro output directly to writer which was preventing
	 * output from being filtered. I have fixed this now.
	 * 
	 * @throws PebbleException
	 */
	@Test
	public void testMacroBeingFiltered() throws PebbleException, IOException {
		PebbleTemplate template = pebble.compile("template.macro3.peb");

		Writer writer = new StringWriter();
		template.evaluate(writer);
		assertEquals("HELLO\n", writer.toString());
	}

	@Test
	public void testMacroFromAnotherFile() throws PebbleException, IOException {
		PebbleTemplate template = pebble.compile("template.macro2.peb");

		Writer writer = new StringWriter();
		template.evaluate(writer);
		assertEquals("	<input name=\"company\" value=\"forcorp\" type=\"text\" />\n", writer.toString());
	}

	@Test
	public void testInclude() throws PebbleException, IOException {
		PebbleTemplate template = pebble.compile("template.include1.peb");

		Writer writer = new StringWriter();
		template.evaluate(writer);
		assertEquals("TEMPLATE2\nTEMPLATE1\nTEMPLATE2\n", writer.toString());
	}

	@Test
	public void testSet() throws PebbleException, IOException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);

		String source = "{% set name = 'alex'  %}{{ name }}";
		PebbleTemplate template = pebble.compile(source);
		Map<String, Object> context = new HashMap<>();
		context.put("name", "steve");

		Writer writer = new StringWriter();
		template.evaluate(writer, context);
		assertEquals("alex", writer.toString());
	}

	@Test(timeout = 4000)
	public void testParallel() throws PebbleException, IOException {
		Loader loader = new StringLoader();
		PebbleEngine pebble = new PebbleEngine(loader);
		pebble.setExecutorService(Executors.newCachedThreadPool());

		String source = "beginning {% parallel %}{{ slowObject.first }}{% endparallel %} middle {% parallel %}{{ slowObject.second }}{% endparallel %} end";
		PebbleTemplate template = pebble.compile(source);

		Writer writer = new StringWriter();
		Map<String, Object> context = new HashMap<>();
		context.put("slowObject", new SlowObject());
		template.evaluate(writer, context);

		assertEquals("beginning first middle second end", writer.toString());
	}

	public class SlowObject {
		public String first() {
			try {
				Thread.sleep(3000);
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
