/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Copyright (c) 2012 Mitchell Bosecke.
 * 
 * This work is licensed under the Creative Commons Attribution-ShareAlike 3.0 
 * Unported License. To view a copy of this license, visit 
 * http://creativecommons.org/licenses/by-sa/3.0/
 ******************************************************************************/
package com.mitchellbosecke.pebble.lexer;

import java.util.ArrayList;
import java.util.Collection;

import com.mitchellbosecke.pebble.error.SyntaxException;
import com.mitchellbosecke.pebble.lexer.Token.Type;

public class TokenStream {

	private ArrayList<Token> tokens = new ArrayList<>();
	private int current;
	private String filename;

	/**
	 * Constructor for a Token Stream
	 * 
	 * @param tokens
	 *            A collection of tokens
	 * @param name
	 *            The filename of the template that these tokens came from
	 */
	public TokenStream(Collection<Token> tokens, String name) {
		this.tokens.addAll(tokens);
		this.current = 0;
		this.filename = name;
	}

	public void injectTokens(Collection<Token> tokens) {
		this.tokens.addAll(current + 1, tokens);
	}

	/**
	 * Consumes and returns the next token in the stream.
	 * 
	 * @return The next token
	 */
	public Token next() {
		return tokens.get(++current);
	}

	/**
	 * Checks the current token to see if it matches the provided type. If it
	 * doesn't match this will throw a SyntaxException
	 * 
	 * @param type
	 *            The type of token that we expect
	 * @return The current token
	 * @throws SyntaxException 
	 */
	public Token expect(Token.Type type) throws SyntaxException {
		return expect(type, null, null);
	}
	
	/**
	 * Checks the current token to see if it matches the provided type. If it
	 * doesn't match this will throw a SyntaxException
	 * 
	 * @param type
	 *            The type of token that we expect
	 * @return The current token
	 * @throws SyntaxException 
	 */
	public Token expect(Token.Type type, String value) throws SyntaxException {
		return expect(type, value, null);
	}


	/**
	 * Checks the current token to see if it matches the provided type and
	 * value. If it doesn't match this will throw a SyntaxException with the
	 * provided message.
	 * 
	 * @param type
	 *            The type of token that we expect
	 * @param value
	 *            The value of the token that we expect, or null if we only care
	 *            about the type
	 * @param message
	 *            The message of the exception if the expectation fails
	 * @return The current token
	 * @throws SyntaxException 
	 */
	public Token expect(Token.Type type, String value, String message) throws SyntaxException {
		// TODO: message isn't used
		Token token = tokens.get(current);
		if (value == null) {
			if (!token.test(type)) {
				throw new SyntaxException("Unexpected token of value ["
						+ token.getValue() + "] (expected " + type.toString()
						+ ")", token.getLineNumber(), filename);
			}
		} else {
			if (!token.test(type, value)) {
				throw new SyntaxException("Unexpected token of value ["
						+ token.getValue() + "] (expected " + type.toString()
						+ ")", token.getLineNumber(), filename);
			}
		}
		this.next();
		return token;
	}

	/**
	 * Returns the next token in the stream without consuming it.
	 * 
	 * @return The next token
	 */
	public Token peek() {
		return peek(1);
	}

	/**
	 * Returns a future token in the stream without consuming any.
	 * 
	 * @param number
	 *            How many tokens to lookahead
	 * @return The token we are peeking at
	 */
	public Token peek(int number) {
		return this.tokens.get(this.current + number);
	}

	public boolean isEOF() {
		return this.tokens.get(current).getType().equals(Type.EOF);
	}

	@Override
	public String toString() {
		return tokens.toString();
	}

	public Token current() {
		return this.tokens.get(current);
	}

	public String getFilename() {
		return filename;
	}

	/**
	 * used for testing purposes
	 * 
	 * @return
	 */
	public ArrayList<Token> getTokens() {
		return tokens;
	}
}
