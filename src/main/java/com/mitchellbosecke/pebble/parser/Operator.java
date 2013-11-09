/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Original work Copyright (c) 2009-2013 by the Twig Team
 * Modified work Copyright (c) 2013 by Mitchell Bösecke
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.parser;

import com.mitchellbosecke.pebble.node.expression.NodeExpressionOperator;

public class Operator {

	private final int precedence;
	private final String symbol;
	private final Class<? extends NodeExpressionOperator> nodeClass;
	private final Associativity associativity;
	
	
	public enum Associativity { LEFT, RIGHT };

	public Operator(String symbol, int precedence, Class<? extends NodeExpressionOperator> nodeClass, Associativity associativity) {
		this.symbol = symbol;
		this.precedence = precedence;
		this.nodeClass = nodeClass;
		this.associativity = associativity;
	}

	public int getPrecedence() {
		return precedence;
	}

	public String getSymbol() {
		return symbol;
	}
	
	public NodeExpressionOperator getNodeInstance(){
		try {
			return nodeClass.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public Associativity getAssociativity() {
		return associativity;
	}
}
