package com.mitchellbosecke.pebble;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.loader.DefaultLoader;
import com.mitchellbosecke.pebble.loader.Loader;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

public class CacheTest extends AbstractTest {

	/**
	 * There was once an issue where the cache was unable to differentiate
	 * between templates of the same name but under different directories.
	 * 
	 * @throws PebbleException
	 */
	@Test
	public void templatesWithSameNameOverridingCache() throws PebbleException, IOException {
		Loader loader = new DefaultLoader();
		PebbleEngine engine = new PebbleEngine(loader);

		PebbleTemplate cache1 = engine.compile("templates/cache/cache1/template.cache.peb");
		PebbleTemplate cache2 = engine.compile("templates/cache/cache2/template.cache.peb");

		Writer writer1 = new StringWriter();
		Writer writer2 = new StringWriter();

		cache1.evaluate(writer1);
		cache2.evaluate(writer2);

		String cache1Output = writer1.toString();
		String cache2Output = writer2.toString();

		assertFalse(cache1Output.equals(cache2Output));

	}
	
	/**
	 * There was an issue where each template was storing a reference to it's
	 * child and this was being cached. This is an issue because a template
	 * can have many different children.
	 * 
	 * @throws PebbleException
	 */
	@Test
	public void ensureChildTemplateNotCached() throws PebbleException, IOException {
		Loader loader = new DefaultLoader();
		PebbleEngine engine = new PebbleEngine(loader);

		PebbleTemplate cache1 = engine.compile("templates/cache/template.cacheChild.peb");
		PebbleTemplate cache2 = engine.compile("templates/cache/template.cacheParent.peb");

		Writer writer1 = new StringWriter();
		Writer writer2 = new StringWriter();

		cache1.evaluate(writer1);
		cache2.evaluate(writer2);

		String cache1Output = writer1.toString();
		String cache2Output = writer2.toString();

		assertEquals("child", cache1Output);
		assertEquals("parent", cache2Output);

	}

	/**
	 * An issue occurred where the engine would mistake the existence of the
	 * template in it's cache with the existence of the templates bytecode in
	 * the file managers cache. This lead to compilation issues.
	 * 
	 * It occurred when rendering two templates that share the same parent
	 * template.
	 * 
	 * @throws PebbleException
	 */
	@Test
	public void templateCachedButBytecodeCleared() throws PebbleException, IOException {
		PebbleTemplate template1 = pebble.compile("template.parent.peb");
		PebbleTemplate template2 = pebble.compile("template.parent2.peb");

		Writer writer1 = new StringWriter();
		Writer writer2 = new StringWriter();

		template1.evaluate(writer1);
		template2.evaluate(writer2);

		assertEquals("GRANDFATHER TEXT ABOVE HEAD\n" + "\n" + "\tPARENT HEAD\n"
				+ "\nGRANDFATHER TEXT BELOW HEAD AND ABOVE FOOT\n\n" + "\tGRANDFATHER FOOT\n\n"
				+ "GRANDFATHER TEXT BELOW FOOT", writer1.toString());
		assertEquals("GRANDFATHER TEXT ABOVE HEAD\n" + "\n" + "\tPARENT HEAD\n"
				+ "\nGRANDFATHER TEXT BELOW HEAD AND ABOVE FOOT\n\n" + "\tGRANDFATHER FOOT\n\n"
				+ "GRANDFATHER TEXT BELOW FOOT", writer2.toString());
	}

	@Test
	public void testConcurrentCacheHitting() throws InterruptedException, PebbleException {
		final PebbleEngine engine = new PebbleEngine();
		final ExecutorService es = Executors.newCachedThreadPool();
		final AtomicInteger totalFailed = new AtomicInteger();

		int numOfConcurrentThreads = 1000;
		final Semaphore semaphore = new Semaphore(numOfConcurrentThreads);

		for (int i = 0; i < 100000; i++) {
			semaphore.acquire();
			es.submit(new Runnable() {
				@Override
				public void run() {
					try {
						PebbleTemplate template = engine.compile("templates/template.concurrent1.peb");

						int a = r.nextInt();
						int b = r.nextInt();
						int c = r.nextInt();

						TestObject testObject = new TestObject(a, b, c);

						StringWriter writer = new StringWriter();
						Map<String, Object> context = new HashMap<>();
						context.put("test", testObject);
						template.evaluate(writer, context);

						String expectedResult = new StringBuilder().append(a).append(":").append(b).append(":")
								.append(c).toString();

						String actualResult = writer.toString();
						if (!expectedResult.equals(actualResult)) {
							System.out.println("Expected: " + expectedResult);
							System.out.println("Actual: " + actualResult);
							totalFailed.incrementAndGet();
						}

					} catch (IOException | PebbleException e) {
						e.printStackTrace();
						totalFailed.incrementAndGet();
					} finally {
						semaphore.release();
					}
				}
			});
			
			// quick fail
			if (totalFailed.intValue() > 0) {
				break;
			}
		}
		// Wait for them all to complete
		semaphore.acquire(numOfConcurrentThreads);
		assertEquals(0, totalFailed.intValue());
	}

	static Random r = new SecureRandom();

	public static class TestObject {
		final public int a;
		final public int b;
		final public int c;

		private TestObject(int a, int b, int c) {
			this.a = a;
			this.b = b;
			this.c = c;
		}
	}

}
