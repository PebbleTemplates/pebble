package com.mitchellbosecke.pebble.extension.core;

import com.mitchellbosecke.pebble.error.ParserException;
import com.mitchellbosecke.pebble.lexer.Token;
import com.mitchellbosecke.pebble.node.RenderableNode;
import com.mitchellbosecke.pebble.parser.Parser;
import com.mitchellbosecke.pebble.tokenParser.AbstractTokenParser;
import com.mitchellbosecke.pebble.tokenParser.BlockTokenParser;
import com.mitchellbosecke.pebble.tokenParser.ForTokenParser;
import com.mitchellbosecke.pebble.tokenParser.TokenParser;
import com.sun.org.apache.xml.internal.security.utils.I18n;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

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