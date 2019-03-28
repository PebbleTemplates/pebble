package com.mitchellbosecke.pebble;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.loader.ClasspathLoader;
import com.mitchellbosecke.pebble.loader.DelegatingLoader;
import com.mitchellbosecke.pebble.loader.StringLoader;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import com.mitchellbosecke.pebble.utils.Pair;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class EmbedTagTest {

    @Parameterized.Parameters(name = "{index}: embed({0})")
    public static Iterable<? extends Object> data() {
        return Arrays.asList(
                "test0"
                , "test1"
                , "test2"
                , "test3"
                , "test4"
                , "test5"
                , "test6"
                , "test7"
                , "test8"
                , "test9"
                , "test10"
                , "test11"
                , "test12"
                , "test13"
                , "test14"
                , "test15"
                , "test16"
                , "test17"
                , "test18"
        );
    }

    private final String input;
    private final String templateDirectory;

    private final PebbleEngine pebble;
    private final Writer writer;
    private final Map<String, Object> context;

    public EmbedTagTest(String input) {
        this.input = input;

        StringLoader stringLoader = new StringLoader();
        ClasspathLoader classpathLoader = new ClasspathLoader();
        templateDirectory = "templates/embed/" + input;
        classpathLoader.setPrefix(templateDirectory);

        pebble = new PebbleEngine.Builder()
                .loader(new DelegatingLoader(Arrays.asList(
                        classpathLoader,
                        stringLoader
                )))
                .strictVariables(false)
                .build();

        writer = new StringWriter();
        context = new HashMap<>();
        context.put("foo", "FOO");
        context.put("bar", "BAR");
    }

    @Test
    public void tests() throws PebbleException, IOException {
        renderTemplateAndCheck();
    }

    private void renderTemplateAndCheck() throws PebbleException, IOException {
        Pair<String, Throwable> actualTemplate = renderTemplate();
        String expectedTemplate = getResource("./" + templateDirectory + "/template.result.txt");
        String expectedTwigTemplate = getResource("./" + templateDirectory + "/template.result.twig.txt");
        String expectedError = getResource("./" + templateDirectory + "/template.error.txt");

        // template rendered correctly
        if(actualTemplate.getLeft() != null) {
            assertNotNull(actualTemplate.getLeft());
            assertNull(actualTemplate.getRight());

            assertNotNull(expectedTemplate);
            assertNull(expectedError);

            assertEquals(expectedTemplate, actualTemplate.getLeft());

            // if Twig could render the same template (meaning it doesn't use Pebble-specific syntax), make sure it renders
            // the same thing Twig does (ignoring whitespace)
            if(expectedTwigTemplate != null) {
                assertNotNull(expectedTwigTemplate);
                assertEquals(expectedTwigTemplate.replaceAll("\\s", ""), actualTemplate.getLeft().replaceAll("\\s", ""));
            }
        }

        // template did not render correctly, check the error message
        else {
            assertNull(actualTemplate.getLeft());
            assertNotNull(actualTemplate.getRight());

            actualTemplate.getRight().printStackTrace();

            assertNull(expectedTemplate);
            assertNotNull(expectedError);

            assertEquals(expectedError, actualTemplate.getRight().getMessage());
        }
    }

    private Pair<String, Throwable> renderTemplate() {
        try {
            PebbleTemplate template = pebble.getTemplate("template.peb");
            template.evaluate(writer, context);
            return new Pair<>(writer.toString(), null);
        }
        catch (Throwable t) {
            return new Pair<>(null, t);
        }
    }

    private String getResource(String filename) {
        try {
            File file = new File(
                    this
                            .getClass()
                            .getClassLoader()
                            .getResource(filename)
                            .getFile()
            );

            return new String(Files.readAllBytes(file.toPath()));
        }
        catch (Exception e) {
            return null;
        }
    }

}
