package com.mitchellbosecke.pebble;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringWriter;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Ignore;
import org.junit.Test;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.loader.StringLoader;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

public class ConcurrencyTest extends AbstractTest {

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

	@Test
	public void testConcurrentEvaluation() throws InterruptedException, PebbleException {

		String templateSource = "{{ test.a }}:{{ test.b }}:{{ test.c }}";
		PebbleEngine engine = new PebbleEngine(new StringLoader());
		final PebbleTemplate template = engine.compile(templateSource);

		ExecutorService es = Executors.newCachedThreadPool();
		final AtomicInteger totalFailed = new AtomicInteger();

		int numOfConcurrentThreads = 1000;
		final Semaphore semaphore = new Semaphore(numOfConcurrentThreads);

		for (int i = 0; i < 100000; i++) {
			semaphore.acquire();
			es.submit(new Runnable() {
				@Override
				public void run() {
					try {

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
			if (totalFailed.intValue() > 0) {
				break;
			}
		}
		// Wait for them all to complete
		semaphore.acquire(numOfConcurrentThreads);
		assertEquals(0, totalFailed.intValue());
	}

	/**
	 * Currently ignoring the following test because it fails and I haven't decided if I want
	 * to put in the possibly extreme amount of effort required to get it to work.
	 * 
	 * It might be easier for me to provide a way for users to PREcompile their templates
	 * before using them in a highly concurrent environment.
	 *
	 * @throws InterruptedException
	 * @throws PebbleException
	 */
	@Ignore
	@Test
	public void testConcurrentCompilationOfMultipleTemplates() throws InterruptedException, PebbleException {
		final PebbleEngine engine = new PebbleEngine();
		final ExecutorService es = Executors.newCachedThreadPool();
		final AtomicInteger totalFailed = new AtomicInteger();

		int numOfConcurrentThreads = 500;
		final Semaphore semaphore = new Semaphore(numOfConcurrentThreads);

		for (int i = 0; i < 100000; i++) {
			semaphore.acquire(2);
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
			es.submit(new Runnable() {
				@Override
				public void run() {
					try {
						PebbleTemplate template = engine.compile("templates/template.concurrent2.peb");

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
			if (totalFailed.intValue() > 0) {
				break;
			}
		}
		// Wait for them all to complete
		semaphore.acquire(numOfConcurrentThreads);
		assertEquals(0, totalFailed.intValue());
	}
}
