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

public abstract class NodeExpressionUnary extends NodeExpression {

	private NodeExpression childExpression;

	public void setNode(NodeExpression node) {
		this.setChildExpression(node);
	}

	@Override
	public void accept(NodeVisitor visitor) {
		visitor.visit(this);
	}

	public NodeExpression getChildExpression() {
		return childExpression;
	}

	public void setChildExpression(NodeExpression childExpression) {
		this.childExpression = childExpression;
	}

}
