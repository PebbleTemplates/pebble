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

import com.mitchellbosecke.pebble.compiler.Compiler;
import com.mitchellbosecke.pebble.compiler.NodeVisitor;
import com.mitchellbosecke.pebble.node.NodeExpression;

/**
 * This node can be either getting a field from a variable or calling a method
 * of that variable. Ex:
 * 
 * var.field OR var.getField() OR var.executeMethod(arg1, arg2)
 * 
 * @author Mitchell
 * 
 */
public class NodeExpressionGetAttribute extends NodeExpression {

	private final NodeExpression node;
	private final NodeExpressionConstant attributeOrMethod;

	public NodeExpressionGetAttribute(int lineNumber, NodeExpression node, NodeExpressionConstant attribute) {
		super(lineNumber);
		this.node = node;
		this.attributeOrMethod = attribute;
	}

	@Override
	public void compile(Compiler compiler) {
		compiler.raw("getAttribute(context, ").subcompile(getNode()).raw(",\"").subcompile(getAttributeOrMethod()).raw("\")");
	}

	@Override
	public void accept(NodeVisitor visitor) {
		visitor.visit(this);
	}

	public NodeExpression getNode() {
		return node;
	}

	public NodeExpressionConstant getAttributeOrMethod() {
		return attributeOrMethod;
	}

}
