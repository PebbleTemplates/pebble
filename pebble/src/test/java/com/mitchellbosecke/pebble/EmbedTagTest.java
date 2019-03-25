package com.mitchellbosecke.pebble;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.loader.ClasspathLoader;
import com.mitchellbosecke.pebble.loader.DelegatingLoader;
import com.mitchellbosecke.pebble.loader.StringLoader;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
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

import static org.junit.Assert.assertEquals;

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
        PebbleTemplate template = pebble.getTemplate("template.peb");
        template.evaluate(writer, context);

        File file = new File(
                this
                        .getClass()
                        .getClassLoader()
                        .getResource("./" + templateDirectory + "/template.result.txt")
                        .getFile()
        );

        assertEquals(
                new String(Files.readAllBytes(file.toPath())),
                writer.toString()
        );
    }


//    @Test
//    public void testBasicEmbedTag() throws PebbleException, IOException {
//        PebbleTemplate template = pebble.getTemplate("{% embed 'template.embed1.peb' %}{% endembed %}");
//
//        template.evaluate(writer, context);
//
//        assertEquals(
//                "EMBED 1 BEFORE BLOCKS [FOO-BAR]\n" +
//                "EMBED BLOCK 1 [FOO-BAR]\n" +
//                "EMBED BLOCK 2 [FOO-BAR]\n" +
//                "EMBED 1 AFTER BLOCKS [FOO-BAR]\n",
//                writer.toString());
//    }
//
//    @Test
//    public void testBasicEmbedTagWithLocalVariables() throws PebbleException, IOException {
//        PebbleTemplate template = pebble.getTemplate("{% embed 'template.embed1.peb' with {'foo': 'NEWFOO', 'bar': 'NEWBAR'} %}{% endembed %}");
//
//        template.evaluate(writer, context);
//
//        assertEquals(
//                "EMBED 1 BEFORE BLOCKS [NEWFOO-NEWBAR]\n" +
//                "EMBED BLOCK 1 [NEWFOO-NEWBAR]\n" +
//                "EMBED BLOCK 2 [NEWFOO-NEWBAR]\n" +
//                "EMBED 1 AFTER BLOCKS [NEWFOO-NEWBAR]\n",
//                writer.toString());
//    }
//
//
//
//
//
//
//    @Test
//    public void testEmbedTagOverridingBlock1NameSyntax() throws PebbleException, IOException {
//        PebbleTemplate template = pebble.getTemplate("{% embed 'template.embed1.peb' %}" +
//                "{% block embedBlock1 %}overridden block 1 [{{ foo }}]{% endblock %}" +
//                "{% endembed %}");
//
//        template.evaluate(writer, context);
//
//        assertEquals(
//                "EMBED 1 BEFORE BLOCKS [FOO-BAR]\n" +
//                        "overridden block 1 [FOO]\n" +
//                        "EMBED BLOCK 2 [FOO-BAR]\n" +
//                        "EMBED 1 AFTER BLOCKS [FOO-BAR]\n",
//                writer.toString());
//    }
//
//    @Test
//    public void testEmbedTagOverridingBlock1StringSyntax() throws PebbleException, IOException {
//        PebbleTemplate template = pebble.getTemplate("{% embed 'template.embed1.peb' %}" +
//                "{% block 'embedBlock1' %}overridden block 1 [{{ foo }}]{% endblock %}" +
//                "{% endembed %}");
//
//        template.evaluate(writer, context);
//
//        assertEquals(
//                "EMBED 1 BEFORE BLOCKS [FOO-BAR]\n" +
//                        "overridden block 1 [FOO]\n" +
//                        "EMBED BLOCK 2 [FOO-BAR]\n" +
//                        "EMBED 1 AFTER BLOCKS [FOO-BAR]\n",
//                writer.toString());
//    }
//
//    @Test
//    public void testEmbedTagOverridingBlock2NameSyntax() throws PebbleException, IOException {
//        PebbleTemplate template = pebble.getTemplate("{% embed 'template.embed1.peb' %}" +
//                "{% block embedBlock2 %}overridden block 2 [{{ bar }}]{% endblock %}" +
//                "{% endembed %}");
//
//        template.evaluate(writer, context);
//
//        assertEquals(
//                "EMBED 1 BEFORE BLOCKS [FOO-BAR]\n" +
//                        "EMBED BLOCK 1 [FOO-BAR]\n" +
//                        "overridden block 2 [BAR]\n" +
//                        "EMBED 1 AFTER BLOCKS [FOO-BAR]\n",
//                writer.toString());
//    }
//
//    @Test
//    public void testEmbedTagOverridingBlock2StringSyntax() throws PebbleException, IOException {
//        PebbleTemplate template = pebble.getTemplate("{% embed 'template.embed1.peb' %}" +
//                "{% block 'embedBlock2' %}overridden block 2 [{{ bar }}]{% endblock %}" +
//                "{% endembed %}");
//
//        template.evaluate(writer, context);
//
//        assertEquals(
//                "EMBED 1 BEFORE BLOCKS [FOO-BAR]\n" +
//                        "EMBED BLOCK 1 [FOO-BAR]\n" +
//                        "overridden block 2 [BAR]\n" +
//                        "EMBED 1 AFTER BLOCKS [FOO-BAR]\n",
//                writer.toString());
//    }
//
//    @Test
//    public void testEmbedTagOverridingBlock1NameSyntaxWithLocalVariables() throws PebbleException, IOException {
//        PebbleTemplate template = pebble.getTemplate("{% embed 'template.embed1.peb' with {'foo': 'NEWFOO', 'bar': 'NEWBAR'} %}" +
//                "{% block embedBlock1 %}overridden block 1 [{{ foo }}]{% endblock %}" +
//                "{% endembed %}");
//
//        template.evaluate(writer, context);
//
//        assertEquals(
//                "EMBED 1 BEFORE BLOCKS [FOO-BAR]\n" +
//                        "overridden block 1 [NEWFOO]\n" +
//                        "EMBED BLOCK 2 [FOO-BAR]\n" +
//                        "EMBED 1 AFTER BLOCKS [FOO-BAR]\n",
//                writer.toString());
//    }
//
//    @Test
//    public void testEmbedTagOverridingBlock1StringSyntaxWithLocalVariables() throws PebbleException, IOException {
//        PebbleTemplate template = pebble.getTemplate("{% embed 'template.embed1.peb' with {'foo': 'NEWFOO', 'bar': 'NEWBAR'} %}" +
//                "{% block 'embedBlock1' %}overridden block 1 [{{ foo }}]{% endblock %}" +
//                "{% endembed %}");
//
//        template.evaluate(writer, context);
//
//        assertEquals(
//                "EMBED 1 BEFORE BLOCKS [FOO-BAR]\n" +
//                        "overridden block 1 [NEWFOO]\n" +
//                        "EMBED BLOCK 2 [FOO-BAR]\n" +
//                        "EMBED 1 AFTER BLOCKS [FOO-BAR]\n",
//                writer.toString());
//    }
//
//    @Test
//    public void testEmbedTagOverridingBlock2NameSyntaxWithLocalVariables() throws PebbleException, IOException {
//        PebbleTemplate template = pebble.getTemplate("{% embed 'template.embed1.peb' with {'foo': 'NEWFOO', 'bar': 'NEWBAR'} %}" +
//                "{% block embedBlock2 %}overridden block 2 [{{ bar }}]{% endblock %}" +
//                "{% endembed %}");
//
//        template.evaluate(writer, context);
//
//        assertEquals(
//                "EMBED 1 BEFORE BLOCKS [FOO-BAR]\n" +
//                        "EMBED BLOCK 1 [FOO-BAR]\n" +
//                        "overridden block 2 [NEWBAR]\n" +
//                        "EMBED 1 AFTER BLOCKS [FOO-BAR]\n",
//                writer.toString());
//    }
//
//    @Test
//    public void testEmbedTagOverridingBlock2StringSyntaxWithLocalVariables() throws PebbleException, IOException {
//        PebbleTemplate template = pebble.getTemplate("{% embed 'template.embed1.peb' with {'foo': 'NEWFOO', 'bar': 'NEWBAR'} %}" +
//                "{% block 'embedBlock2' %}overridden block 2 [{{ bar }}]{% endblock %}" +
//                "{% endembed %}");
//
//        template.evaluate(writer, context);
//
//        assertEquals(
//                "EMBED 1 BEFORE BLOCKS [FOO-BAR]\n" +
//                        "EMBED BLOCK 1 [FOO-BAR]\n" +
//                        "overridden block 2 [NEWBAR]\n" +
//                        "EMBED 1 AFTER BLOCKS [FOO-BAR]\n",
//                writer.toString());
//    }
//
//    @Test
//    public void testEmbedTagWithinExtendedTemplated() throws PebbleException, IOException {
//        PebbleTemplate template = pebble.getTemplate(
//                "{% extends 'template.embedExtendsParent.peb' %}\n" +
//                "{% block head %}\n" +
//                "EMBED CHILD HEAD\n" +
//
//                    "{% embed 'template.embedBase.peb' %}\n" +
//                        "{% block head %}\n" +
//                        "EMBED OVERRIDDEN HEAD\n" +
//                        "{% endblock %}\n" +
//
//                        "{% block head %}\n" +
//                        "EMBED OVERRIDDEN HEAD\n" +
//                        "{% endblock %}\n" +
//                    "{% endembed %}\n" +
//
//                "{% endblock %}\n");
//
//        template.evaluate(writer, context);
//
//        assertEquals(
//                "EMBED GRANDFATHER TEXT ABOVE HEAD\n" +
//                "EMBED CHILD HEAD\n" +
//
//                        "EMBED BASE TEXT ABOVE HEAD\n" +
//                        "EMBED OVERRIDDEN HEAD\n" +
//                        "EMBED BASE TEXT BELOW HEAD AND ABOVE FOOT\n" +
//                        "EMBED OVERRIDDEN FOOT\n" +
//                        "EMBED BASE TEXT BELOW FOOT\n" +
//
//                "EMBED GRANDFATHER TEXT BELOW HEAD AND ABOVE FOOT\n" +
//                "EMBED PARENT FOOT\n" +
//                "EMBED GRANDFATHER TEXT BELOW FOOT\n",
//                writer.toString());
//    }


// Helpers
//----------------------------------------------------------------------------------------------------------------------

}
