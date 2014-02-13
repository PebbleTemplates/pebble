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

import java.util.ArrayList;
import java.util.List;

import com.mitchellbosecke.pebble.compiler.Compiler;
import com.mitchellbosecke.pebble.node.Node;
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
		compiler.raw("getAttribute(context, ").subcompile(node).raw(",\"").subcompile(attributeOrMethod).raw("\")");
	}
	
	@Override
	public List<Node> getChildren(){
		List<Node> children = new ArrayList<>();
		children.add(node);
		children.add(attributeOrMethod);
		return children;
	}

}
