/*******************************************************************************
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble;

import com.mitchellbosecke.pebble.error.LoaderException;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.loader.*;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import org.junit.Test;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class LoaderTest extends AbstractTest {

    @Test
    public void testClassLoaderLoader() throws PebbleException, IOException {
        Loader<?> loader = new ClasspathLoader();
        loader.setPrefix("templates");
        loader.setSuffix(".peb");
        PebbleEngine pebble = new PebbleEngine.Builder().loader(loader).strictVariables(false).build();
        PebbleTemplate template1 = pebble.getTemplate("template.loaderTest");
        Writer writer1 = new StringWriter();
        template1.evaluate(writer1);
        assertEquals("SUCCESS", writer1.toString());

    }

    @Test
    public void testClassLoaderLoaderWithNestedTemplate() throws PebbleException, IOException {
        Loader<?> loader = new ClasspathLoader();
        loader.setPrefix("templates");
        loader.setSuffix(".peb");
        PebbleEngine engine = new PebbleEngine.Builder().loader(loader).strictVariables(false).build();
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
        PebbleEngine engine = new PebbleEngine.Builder().loader(loader).strictVariables(false).build();
        PebbleTemplate template1 = engine.getTemplate("loader/template.loaderTest");
        Writer writer1 = new StringWriter();
        template1.evaluate(writer1);
        assertEquals("SUCCESS", writer1.toString());

    }

    @Test
    public void testFileLoader() throws PebbleException, IOException, URISyntaxException {
        Loader<?> loader = new FileLoader();
        loader.setSuffix(".suffix");
        PebbleEngine engine = new PebbleEngine.Builder().loader(loader).strictVariables(false).build();
        URL url = getClass().getResource("/templates/template.loaderTest.peb");
        PebbleTemplate template1 = engine.getTemplate(new File(url.toURI()).getPath());
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

        PebbleEngine engine = new PebbleEngine.Builder().loader(loader).strictVariables(false).build();
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
