package com.mitchellbosecke.pebble.extension.core;

import com.mitchellbosecke.pebble.tokenParser.TokenParser;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertTrue;

public class TokenParserBuilderTest {

    @Test
    public void testDefaultParser() throws Exception {
        List<TokenParser> tokenParsers = new TokenParserBuilder(null)
                .build();

        assertTrue("building without options was expected to return an non empty list", tokenParsers.size() > 0);
    }

    @Test
    public void testDisableAll() throws Exception {
        List<TokenParser> tokenParsers = new TokenParserBuilder(null)
                .disableAll()
                .build();

        assertTrue("expected an empty list when all was disabled", tokenParsers.isEmpty());
    }

}