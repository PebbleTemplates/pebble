package io.pebbletemplates.pebble.extension.core;

import io.pebbletemplates.pebble.PebbleEngine;
import io.pebbletemplates.pebble.error.PebbleException;
import io.pebbletemplates.pebble.loader.StringLoader;
import io.pebbletemplates.pebble.template.PebbleTemplate;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.MissingFormatArgumentException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;


class FormatFilterTest {

    @Test
    void itShouldThrowExceptionWhenNotEnoughArguments() {
        PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
            .strictVariables(false).build();

        String source = "{{ 'I need %s and %s and %s' | format('one', 'two') }}";
        PebbleTemplate template = pebble.getTemplate(source);

        Writer writer = new StringWriter();

        assertThatExceptionOfType(MissingFormatArgumentException.class)
            .isThrownBy(() -> template.evaluate(writer, new HashMap<>()));
    }

    @Test
    void itShouldReturnNullWhenInputIsNull() throws PebbleException, IOException {
        PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
            .strictVariables(false).build();

        String source = "{{ null | format('test') }}";
        PebbleTemplate template = pebble.getTemplate(source);

        Writer writer = new StringWriter();
        template.evaluate(writer, new HashMap<>());
        assertThat(writer.toString()).isEmpty();
    }

    @Test
    void itShouldHandleNoArguments() throws PebbleException, IOException {
        PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
            .strictVariables(false).build();

        String source = "{{ 'Just a string with no placeholders' | format() }}";
        PebbleTemplate template = pebble.getTemplate(source);

        Writer writer = new StringWriter();
        template.evaluate(writer, new HashMap<>());
        assertThat(writer.toString()).isEqualTo("Just a string with no placeholders");
    }

    @Test
    void itShouldFormatStringWithSingleArgument() throws PebbleException, IOException {
        PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
            .strictVariables(false).build();

        String source = "{{ 'Hello %s!' | format('World') }}";
        PebbleTemplate template = pebble.getTemplate(source);

        Writer writer = new StringWriter();
        template.evaluate(writer, new HashMap<>());
        assertThat(writer.toString()).isEqualTo("Hello World!");
    }



    @Test
    void itShouldFormatStringWithVariable() throws PebbleException, IOException {
        PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
            .strictVariables(false).build();

        String source = "{% set name = 'World' %}{{ 'Hello %s!' | format(name) }}";
        PebbleTemplate template = pebble.getTemplate(source);

        Writer writer = new StringWriter();
        template.evaluate(writer, new HashMap<>());
        assertThat(writer.toString()).isEqualTo("Hello World!");
    }

    @Test
    void itShouldFormatWithDifferentTypes() throws PebbleException, IOException {
        PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
            .strictVariables(false).build();

        String source = "{{ 'Number: %d, Float: %.2f, String: %s' | format(42, 3.14159, 'test') }}";
        PebbleTemplate template = pebble.getTemplate(source);

        Writer writer = new StringWriter();
        template.evaluate(writer, new HashMap<>());
        assertThat(writer.toString()).isEqualTo("Number: 42, Float: 3.14, String: test");
    }

    @Test
    void itShouldIgnoreExtraArguments() throws PebbleException, IOException {
        PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
            .strictVariables(false).build();

        String source = "{{ 'I only need %s' | format('one', 'two', 'three') }}";
        PebbleTemplate template = pebble.getTemplate(source);

        Writer writer = new StringWriter();
        template.evaluate(writer, new HashMap<>());
        assertThat(writer.toString()).isEqualTo("I only need one");
    }

    @Test
    void itShouldHandleNullArgument() throws PebbleException, IOException {
        PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
            .strictVariables(false).build();

        String source = "{{ 'Value is: %s' | format(null) }}";
        PebbleTemplate template = pebble.getTemplate(source);

        Writer writer = new StringWriter();
        template.evaluate(writer, new HashMap<>());
        assertThat(writer.toString()).isEqualTo("Value is: null");
    }

    @Test
    void itShouldHandleEscapedPercentSign() throws PebbleException, IOException {
        PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
            .strictVariables(false).build();

        String source = "{{ '100%% complete: %s' | format('done') }}";
        PebbleTemplate template = pebble.getTemplate(source);

        Writer writer = new StringWriter();
        template.evaluate(writer, new HashMap<>());
        assertThat(writer.toString()).isEqualTo("100% complete: done");
    }

    @Test
    void itShouldFormatWithBooleanValues() throws PebbleException, IOException {
        PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
            .strictVariables(false).build();

        String source = "{{ 'Enabled: %s, Disabled: %s' | format(true, false) }}";
        PebbleTemplate template = pebble.getTemplate(source);

        Writer writer = new StringWriter();
        template.evaluate(writer, new HashMap<>());
        assertThat(writer.toString()).isEqualTo("Enabled: true, Disabled: false");
    }

    @Test
    void itShouldFormatEmptyString() throws PebbleException, IOException {
        PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
            .strictVariables(false).build();

        String source = "{{ '' | format('ignored') }}";
        PebbleTemplate template = pebble.getTemplate(source);

        Writer writer = new StringWriter();
        template.evaluate(writer, new HashMap<>());
        assertThat(writer.toString()).isEmpty();
    }

    @Test
    void itShouldFormatWithMixedArgumentTypes() throws PebbleException, IOException {
        PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
            .strictVariables(false).build();

        String source = "{{ 'Count: %d items, Price: $%.2f, Name: %s' | format(5, 19.99, 'Widget') }}";
        PebbleTemplate template = pebble.getTemplate(source);

        Writer writer = new StringWriter();
        template.evaluate(writer, new HashMap<>());
        assertThat(writer.toString()).isEqualTo("Count: 5 items, Price: $19.99, Name: Widget");
    }
}
