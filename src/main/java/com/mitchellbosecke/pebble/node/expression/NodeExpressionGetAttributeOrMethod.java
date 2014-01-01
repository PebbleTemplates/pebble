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
public class NodeExpressionGetAttributeOrMethod extends NodeExpression {

	public static enum Type {
		ANY, METHOD
	};

	private final Type type;
	private final NodeExpression node;
	private final NodeExpressionConstant attributeOrMethod;
	private final NodeExpressionArguments args;

	public NodeExpressionGetAttributeOrMethod(int lineNumber, Type type, NodeExpression node,
			NodeExpressionConstant attribute) {
		this(lineNumber, type, node, attribute, null);
	}

	public NodeExpressionGetAttributeOrMethod(int lineNumber, Type type, NodeExpression node,
			NodeExpressionConstant attribute, NodeExpressionArguments args) {
		super(lineNumber);
		this.node = node;
		this.attributeOrMethod = attribute;
		this.type = type;
		this.args = args;
	}

	@Override
	public void compile(Compiler compiler) {
		compiler.raw("getAttribute(").raw(NodeExpressionGetAttributeOrMethod.Type.class.getCanonicalName()).raw(".")
				.raw(type.toString()).raw(",").subcompile(node).raw(",\"").subcompile(attributeOrMethod).raw("\"");

		if (args != null) {
			for (NodeExpression arg : args.getArgs()) {
				compiler.raw(",").subcompile(arg);
			}
		}
		compiler.raw(") ");
	}

}
