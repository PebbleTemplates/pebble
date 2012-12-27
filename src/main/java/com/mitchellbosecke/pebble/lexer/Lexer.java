package com.mitchellbosecke.pebble.lexer;


public interface Lexer {

	public TokenStream tokenize(String source, String name);
}
