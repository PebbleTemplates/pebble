/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Copyright (c) 2014 by Mitchell Bösecke
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.lexer;

import java.util.Arrays;

public class Token {

    private String value;

    private Type type;

    private int lineNumber;

    public enum Type {
        EOF, TEXT, EXECUTE_START, EXECUTE_END, PRINT_START, PRINT_END, NAME, NUMBER, STRING, OPERATOR, PUNCTUATION
    }

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
