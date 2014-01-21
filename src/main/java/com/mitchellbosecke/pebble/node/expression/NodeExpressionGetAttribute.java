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
	private final NodeExpressionArguments args;

	public NodeExpressionGetAttribute(int lineNumber, NodeExpression node, NodeExpressionConstant attribute,
			NodeExpressionArguments args) {
		super(lineNumber);
		this.node = node;
		this.attributeOrMethod = attribute;
		this.args = args;
	}

	@Override
	public void compile(Compiler compiler) {
		compiler.raw("getAttribute(context, ").subcompile(node).raw(",\"").subcompile(attributeOrMethod).raw("\"");

		if (args != null) {
			for (NodeExpression arg : args.getArgs()) {
				compiler.raw(",").subcompile(arg);
			}
		}
		compiler.raw(") ");
	}

}
