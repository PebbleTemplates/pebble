package com.mitchellbosecke.pebble;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import com.mitchellbosecke.pebble.error.SyntaxException;
import com.mitchellbosecke.pebble.lexer.Token;
import com.mitchellbosecke.pebble.lexer.TokenStream;

public class LexerTest extends AbstractTest {

	/**
	 * single variable and weird whitespace
	 */
	@Test
	public void testLexer1() {
		String source = loader.getSource("lexer1.test");
		TokenStream stream = pebble.tokenize(source, "lexer1.test");

		assertNotNull("Lexer returned null stream", stream);

		ArrayList<Token> tokens = stream.getTokens();

		assertTrue(tokens.size() == 6);
		assertTrue(tokens.get(0).getType().equals(Token.Type.TEXT));
		assertTrue(tokens.get(1).getType().equals(Token.Type.VARIABLE_START));
		assertTrue(tokens.get(2).getType().equals(Token.Type.NAME));
		assertTrue(tokens.get(3).getType().equals(Token.Type.VARIABLE_END));
		assertTrue(tokens.get(4).getType().equals(Token.Type.TEXT));
		assertTrue(tokens.get(5).getType().equals(Token.Type.EOF));
	}

	/**
	 * expression with numbers and binary operator
	 */
	@Test
	public void testLexer2() {
		TokenStream stream = pebble.tokenize(pebble.getLoader().getSource(
				"lexer2.test"),"lexer2.test");

		assertNotNull("Lexer returned null stream", stream);

		ArrayList<Token> tokens = stream.getTokens();

		assertTrue(tokens.size() == 6);
		assertTrue(tokens.get(0).getType().equals(Token.Type.TEXT));
		assertTrue(tokens.get(1).getType().equals(Token.Type.BLOCK_START));
		assertTrue(tokens.get(2).getType().equals(Token.Type.NUMBER));
		assertTrue(tokens.get(3).getType().equals(Token.Type.BLOCK_END));
		assertTrue(tokens.get(4).getType().equals(Token.Type.TEXT));
		assertTrue(tokens.get(5).getType().equals(Token.Type.EOF));
	}

	/**
	 * syntax error with unclosed bracket
	 */
	@Test(expected = SyntaxException.class)
	public void testLexer3() {
		pebble.tokenize(pebble.getLoader().getSource("lexer3.test"),"lexer3.test");
	}

	/**
	 * successful bracket matching
	 */
	@Test
	public void testLexer4() {
		pebble.tokenize(pebble.getLoader().getSource("lexer4.test"),"lexer4.test");
	}
	
	/**
	 * string lexing
	 */
	@Test
	public void testLexer5() {
		TokenStream stream = pebble.tokenize(pebble.getLoader().getSource("lexer5.test"),"lexer5.test");
		
		ArrayList<Token> tokens = stream.getTokens();
		assertTrue(tokens.size() == 6);
		assertTrue(tokens.get(0).getType().equals(Token.Type.BLOCK_START));
		assertTrue(tokens.get(1).getType().equals(Token.Type.NAME));
		assertTrue(tokens.get(2).getType().equals(Token.Type.STRING));
		assertTrue(tokens.get(3).getType().equals(Token.Type.BLOCK_END));
		assertTrue(tokens.get(4).getType().equals(Token.Type.TEXT));
		assertTrue(tokens.get(5).getType().equals(Token.Type.EOF));
	}
	
}
