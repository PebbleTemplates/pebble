/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Original work Copyright (c) 2009-2013 by the Twig Team
 * Modified work Copyright (c) 2013 by Mitchell Bösecke
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.operator;

import com.mitchellbosecke.pebble.node.expression.NodeExpressionUnary;

public class UnaryOperatorImpl implements UnaryOperator {

	private final int precedence;
	private final String symbol;
	private final Class<? extends NodeExpressionUnary> nodeClass;

	public UnaryOperatorImpl(String symbol, int precedence, Class<? extends NodeExpressionUnary> nodeClass) {
		this.symbol = symbol;
		this.precedence = precedence;
		this.nodeClass = nodeClass;
	}

	@Override
	public int getPrecedence() {
		return precedence;
	}

	@Override
	public String getSymbol() {
		return symbol;
	}

	@Override
	public Class<? extends NodeExpressionUnary> getNodeClass() {
		return nodeClass;
	}
}
