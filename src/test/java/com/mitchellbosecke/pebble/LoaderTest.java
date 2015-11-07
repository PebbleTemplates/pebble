/*******************************************************************************
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.mitchellbosecke.pebble.error.LoaderException;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.loader.ClasspathLoader;
import com.mitchellbosecke.pebble.loader.DelegatingLoader;
import com.mitchellbosecke.pebble.loader.FileLoader;
import com.mitchellbosecke.pebble.loader.Loader;
import com.mitchellbosecke.pebble.loader.StringLoader;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

public class LoaderTest extends AbstractTest {

    @Test
    public void testClassLoaderLoader() throws PebbleException, IOException {
        Loader<?> loader = new ClasspathLoader();
        loader.setPrefix("templates");
        loader.setSuffix(".peb");
        PebbleEngine engine = new PebbleEngine(loader);
        PebbleTemplate template1 = engine.getTemplate("template.loaderTest");
        Writer writer1 = new StringWriter();
        template1.evaluate(writer1);
        assertEquals("SUCCESS", writer1.toString());

    }

    @Test
    public void testClassLoaderLoaderWithNestedTemplate() throws PebbleException, IOException {
        Loader<?> loader = new ClasspathLoader();
        loader.setPrefix("templates");
        loader.setSuffix(".peb");
        PebbleEngine engine = new PebbleEngine(loader);
        PebbleTemplate template1 = engine.getTemplate("loader/template.loaderTest");
        Writer writer1 = new StringWriter();
        template1.evaluate(writer1);
        assertEquals("SUCCESS", writer1.toString());

    }

    @Test
    public void testClassLoaderLoaderWithNestedTemplateInJar() throws PebbleException, IOException {
        URL resource = this.getClass().getResource("/templateinjar.jar");
        assertNotNull(resource);
        Loader<?> loader = new ClasspathLoader(new URLClassLoader(new URL[] { resource }, null));
        loader.setPrefix("templates");
        loader.setSuffix(".peb");
        PebbleEngine engine = new PebbleEngine(loader);
        PebbleTemplate template1 = engine.getTemplate("loader/template.loaderTest");
        Writer writer1 = new StringWriter();
        template1.evaluate(writer1);
        assertEquals("SUCCESS", writer1.toString());

    }

    @Test
    public void testFileLoader() throws PebbleException, IOException {
        Loader<?> loader = new FileLoader();
        loader.setSuffix(".suffix");
        PebbleEngine engine = new PebbleEngine(loader);
        URL url = getClass().getResource("/templates/template.loaderTest.peb");
        PebbleTemplate template1 = engine.getTemplate(url.getPath());
        Writer writer1 = new StringWriter();
        template1.evaluate(writer1);
        assertEquals("SUCCESS", writer1.toString());

    }

    @Test
    public void testDelegatingLoader() throws PebbleException, IOException {
        List<Loader<?>> loaders = new ArrayList<>();
        loaders.add(new StringLoaderFailure());
        loaders.add(new StringLoaderOne());
        loaders.add(new StringLoaderTwo());
        Loader<?> loader = new DelegatingLoader(loaders);

        PebbleEngine engine = new PebbleEngine(loader);
        PebbleTemplate template = engine.getTemplate("fake template name");
        Writer writer = new StringWriter();
        template.evaluate(writer);

        assertEquals("LOADER ONE", writer.toString());
    }

    /**
     * Always fail to find a template
     *
     */
    private class StringLoaderFailure extends StringLoader {

        @Override
        public Reader getReader(String templateName) throws LoaderException {
            throw new LoaderException(null, "Could not find template ");
        }
    }

    private class StringLoaderOne extends StringLoader {

        @Override
        public Reader getReader(String templateName) throws LoaderException {
            return new StringReader("LOADER ONE");
        }

    }

    private class StringLoaderTwo extends StringLoader {

        @Override
        public Reader getReader(String templateName) throws LoaderException {
            return new StringReader("LOADER TWO");
        }

    }
}
