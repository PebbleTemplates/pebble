package com.mitchellbosecke.pebble.operator;

import com.mitchellbosecke.pebble.node.expression.UnaryExpression;

public interface UnaryOperator {
	
	public abstract int getPrecedence();

	public abstract String getSymbol();

	public abstract Class<? extends UnaryExpression> getNodeClass();

}