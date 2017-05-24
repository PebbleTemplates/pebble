package com.mitchellbosecke.pebble;

import org.junit.Test;

import static org.junit.Assert.*;

public class PebbleEngineBuilderTest {

    @Test
    public void testCoreApi() throws Exception {
        PebbleEngine.Builder builder = new PebbleEngine.Builder();
        PebbleEngine.Builder secondBuilder = builder
                .core()
                    .enable()
                    .and();

        assertEquals(builder, secondBuilder);
    }

    @Test
    public void testI18nApi() throws Exception {
        PebbleEngine.Builder builder = new PebbleEngine.Builder();
        PebbleEngine.Builder secondBuilder = builder
                .i18n()
                    .enable()
                    .and();

        assertEquals(builder, secondBuilder);
    }

}