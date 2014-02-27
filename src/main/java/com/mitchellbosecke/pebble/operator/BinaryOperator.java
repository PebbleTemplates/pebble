package com.mitchellbosecke.pebble.operator;

import com.mitchellbosecke.pebble.node.expression.BinaryExpression;

public interface BinaryOperator {
	
	public abstract int getPrecedence();

	public abstract String getSymbol();

	public abstract Class<? extends BinaryExpression<?>> getNodeClass();
	
	public abstract Associativity getAssociativity();

}