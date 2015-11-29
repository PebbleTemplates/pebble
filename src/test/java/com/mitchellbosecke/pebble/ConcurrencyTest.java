/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Copyright (c) 2014 by Mitchell Bösecke
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.loader.StringLoader;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;

public class ConcurrencyTest extends AbstractTest {

    static Random r = new SecureRandom();

    public static class TestObject {

        final public int a;

        final public int b;

        final public int c;
        
        final public String d;

        private TestObject(int a, int b, int c, String d) {
            this.a = a;
            this.b = b;
            this.c = c;
            this.d = d;
        }
    }

    @Test
    public void testConcurrentEvaluation() throws InterruptedException, PebbleException {
        PebbleEngine engine = new PebbleEngine.Builder().loader(new StringLoader()).strictVariables(false).build();

        String templateSource = "{{ test.a }}:{{ test.b }}:{{ test.c }}:{{ test.d | upper }}";
        final PebbleTemplate template = engine.getTemplate(templateSource);

        ExecutorService es = Executors.newCachedThreadPool();
        final AtomicInteger totalFailed = new AtomicInteger();

        int numOfConcurrentEvaluations = Math.min(4, Runtime.getRuntime().availableProcessors());
        final Semaphore semaphore = new Semaphore(numOfConcurrentEvaluations);

        for (int i = 0; i < 10000; i++) {
            semaphore.acquire();
            es.submit(new Runnable() {

                @Override
                public void run() {
                    try {

                        int a = r.nextInt();
                        int b = r.nextInt();
                        int c = r.nextInt();
                        int d = r.nextInt();

                        TestObject testObject = new TestObject(a, b, c, "test"+d);

                        StringWriter writer = new StringWriter();
                        Map<String, Object> context = new HashMap<>();
                        context.put("test", testObject);
                        template.evaluate(writer, context);

                        String expectedResult = new StringBuilder().append(a).append(":").append(b).append(":")
                                .append(c).append(":").append("TEST").append(d).toString();

                        String actualResult = writer.toString();
                        if (!expectedResult.equals(actualResult)) {
                            System.out.println("Expected: " + expectedResult);
                            System.out.println("Actual: " + actualResult);
                            System.out.println("");
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
        semaphore.acquire(numOfConcurrentEvaluations);
        es.shutdown();
        assertEquals(0, totalFailed.intValue());
    }

    /**
     * True concurrent compilation is not currently supported. The pebble engine
     * will only compile one at a time. This test simply makes sure that if a
     * user attempts to trigger concurrent compilation everything still succeeds
     * (despite it being executed one at a time behind the scenes).
     * 
     * @throws InterruptedException
     * @throws PebbleException
     */
    @Test
    public void testThreadSafeCompilationOfMultipleTemplates() throws InterruptedException, PebbleException {
        final PebbleEngine engine = new PebbleEngine.Builder().templateCache(null).strictVariables(false).build();
        final ExecutorService es = Executors.newCachedThreadPool();
        final AtomicInteger totalFailed = new AtomicInteger();

        int numOfConcurrentEvaluations = Math.min(4, Runtime.getRuntime().availableProcessors());
        final Semaphore semaphore = new Semaphore(numOfConcurrentEvaluations);

        for (int i = 0; i < 1000; i++) {
            semaphore.acquire(1);
            es.submit(new Runnable() {

                @Override
                public void run() {
                    try {
                        PebbleTemplate template = engine.getTemplate("templates/template.concurrent1.peb");

                        int a = r.nextInt();
                        int b = r.nextInt();
                        int c = r.nextInt();
                        int d = r.nextInt();

                        TestObject testObject = new TestObject(a, b, c, "test" + d);

                        StringWriter writer = new StringWriter();
                        Map<String, Object> context = new HashMap<>();
                        context.put("test", testObject);
                        template.evaluate(writer, context);

                        String expectedResult = new StringBuilder().append(a).append(":").append(b).append(":")
                                .append(c).toString();

                        String actualResult = writer.toString();
                        if (!expectedResult.equals(actualResult)) {
                            System.out.println("Expected1: " + expectedResult);
                            System.out.println("Actual1: " + actualResult);
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
                        PebbleTemplate template = engine.getTemplate("templates/template.concurrent2.peb");

                        int a = r.nextInt();
                        int b = r.nextInt();
                        int c = r.nextInt();
                        int d = r.nextInt();

                        TestObject testObject = new TestObject(a, b, c, "test" + d);

                        StringWriter writer = new StringWriter();
                        Map<String, Object> context = new HashMap<>();
                        context.put("test", testObject);
                        template.evaluate(writer, context);

                        String expectedResult = new StringBuilder().append(a).append(":").append(b).append(":")
                                .append(c).toString();

                        String actualResult = writer.toString();
                        if (!expectedResult.equals(actualResult)) {
                            System.out.println("Expected2: " + expectedResult);
                            System.out.println("Actual2: " + actualResult);
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
        semaphore.acquire(numOfConcurrentEvaluations);
        es.shutdown();
        assertEquals(0, totalFailed.intValue());
    }

    /**
     * Issue #40
     * 
     * @throws InterruptedException
     * @throws PebbleException
     */
    @Test
    public void testConcurrentEvaluationWithDifferingLocals() throws InterruptedException, PebbleException {

        final PebbleEngine engine = new PebbleEngine.Builder().loader(new StringLoader()).strictVariables(false).build();
        String templateSource = "{{ 2000.234 | numberformat }}";
        final PebbleTemplate template = engine.getTemplate(templateSource);

        ExecutorService es = Executors.newCachedThreadPool();
        final AtomicInteger totalFailed = new AtomicInteger();

        int numOfConcurrentEvaluations = Math.min(4, Runtime.getRuntime().availableProcessors());
        final Semaphore semaphore = new Semaphore(numOfConcurrentEvaluations);

        final String germanResult = "2.000,234";
        final String canadianResult = "2,000.234";

        for (int i = 0; i < 1000; i++) {
            semaphore.acquire();
            es.submit(new Runnable() {

                @Override
                public void run() {
                    try {

                        final boolean isGerman = r.nextBoolean();

                        final Locale locale = isGerman ? Locale.GERMANY : Locale.CANADA;

                        final StringWriter writer = new StringWriter();

                        template.evaluate(writer, locale);

                        final String expectedResult = isGerman ? germanResult : canadianResult;

                        final String actualResult = writer.toString();

                        if (!expectedResult.equals(actualResult)) {
                            System.out.println(String.format("Locale: %s\nExpected: %s\nActual: %s\n",
                                    locale.toString(), expectedResult, actualResult));
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
        semaphore.acquire(numOfConcurrentEvaluations);
        es.shutdown();
        assertEquals(0, totalFailed.intValue());
    }
}
