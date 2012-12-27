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

import java.util.Arrays;

public class Token {
	private String value;
	private Type type;
	private int lineNumber;

	public static enum Type {
		EOF, TEXT, BLOCK_START, BLOCK_END, VARIABLE_START, VARIABLE_END, NAME, NUMBER, STRING, OPERATOR, PUNCTUATION
	};

	public Token(Type type, String value, int lineNumber) {
		this.type = type;
		this.value = value;
		this.lineNumber = lineNumber;
	}

	public boolean test(Type type) {
		return test(type, new String[0]);
	}

	public boolean test(Type type, String... values) {
		boolean test = true;
		if (values.length > 0) {
			test = Arrays.asList(values).contains(this.value);
		}
		return test && this.type.equals(type);
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}

	@Override
	public String toString() {
		return "Token[" + this.getType() + "](" + this.getValue() + ")";
	}
}
