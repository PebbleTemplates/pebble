package com.mitchellbosecke.pebble.extension.core;

import com.mitchellbosecke.pebble.AbstractTest;
import com.mitchellbosecke.pebble.extension.Extension;
import com.mitchellbosecke.pebble.extension.NoOpExtension;
import org.junit.Test;

import static org.junit.Assert.*;

public class CoreExtensionBuilderTest extends AbstractTest {

    @Test
    public void testBuildDefault() throws Exception {
        Extension extension = new CoreExtension.Builder(null)
                .build();

        assertTrue("expected extension to be a CoreExtension", extension instanceof CoreExtension);
    }

    @Test
    public void testBuildWhenEnabled() throws Exception {
        Extension extension = new CoreExtension.Builder(null)
                .enable()
                .build();

        assertTrue("expected extension to be a CoreExtension", extension instanceof CoreExtension);
    }

    @Test
    public void testBuildWhenDisabled() throws Exception {
        Extension extension = new CoreExtension.Builder(null)
                .disable()
                .build();

        assertTrue("expected extension to be a NoOpExtension", extension instanceof NoOpExtension);
    }

}