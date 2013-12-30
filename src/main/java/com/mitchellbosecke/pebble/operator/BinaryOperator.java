package com.mitchellbosecke.pebble.operator;

import com.mitchellbosecke.pebble.node.expression.NodeExpressionBinary;

public interface BinaryOperator {
	
	public abstract int getPrecedence();

	public abstract String getSymbol();

	public abstract Class<? extends NodeExpressionBinary> getNodeClass();
	
	public abstract Associativity getAssociativity();

}