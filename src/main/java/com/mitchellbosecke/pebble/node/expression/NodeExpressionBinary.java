/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Original work Copyright (c) 2009-2013 by the Twig Team
 * Modified work Copyright (c) 2013 by Mitchell Bösecke
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.node.expression;

import com.mitchellbosecke.pebble.compiler.NodeVisitor;
import com.mitchellbosecke.pebble.node.NodeExpression;

public abstract class NodeExpressionBinary extends NodeExpression {

	private NodeExpression leftExpression;
	private NodeExpression rightExpression;

	public void setLeft(NodeExpression left) {
		this.leftExpression = left;
	}

	public void setRight(NodeExpression right) {
		this.rightExpression = right;
	}

	public NodeExpressionBinary(int lineNumber) {
		super(lineNumber);
	}

	public NodeExpressionBinary() {

	}

	@Override
	public void accept(NodeVisitor visitor) {
		visitor.visit(this);
	}

	public NodeExpression getLeftExpression() {
		return leftExpression;
	}

	public NodeExpression getRightExpression() {
		return rightExpression;
	}

}
