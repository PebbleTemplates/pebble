/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Copyright (c) 2012 Mitchell Bosecke.
 * 
 * This work is licensed under the Creative Commons Attribution-ShareAlike 3.0 
 * Unported License. To view a copy of this license, visit 
 * http://creativecommons.org/licenses/by-sa/3.0/
 ******************************************************************************/
package com.mitchellbosecke.pebble.parser;

import com.mitchellbosecke.pebble.node.expression.NodeExpressionOperator;

public class Operator {

	private final int precedence;
	private final String symbol;
	private final NodeExpressionOperator node;
	private final Associativity associativity;
	
	
	public enum Associativity { LEFT, RIGHT };

	public Operator(String symbol, int precedence, NodeExpressionOperator node, Associativity associativity) {
		this.symbol = symbol;
		this.precedence = precedence;
		this.node = node;
		this.associativity = associativity;
	}

	public int getPrecedence() {
		return precedence;
	}

	public String getSymbol() {
		return symbol;
	}

	public NodeExpressionOperator getNode() {
		return node;
	}

	public Associativity getAssociativity() {
		return associativity;
	}
}
