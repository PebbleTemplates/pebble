package com.mitchellbosecke.pebble.utils;

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
