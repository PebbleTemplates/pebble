/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Copyright (c) 2012 Mitchell Bosecke.
 * 
 * This work is licensed under the Creative Commons Attribution-ShareAlike 3.0 
 * Unported License. To view a copy of this license, visit 
 * http://creativecommons.org/licenses/by-sa/3.0/
 ******************************************************************************/
package com.mitchellbosecke.pebble.node.expression;

import com.mitchellbosecke.pebble.compiler.Compiler;
import com.mitchellbosecke.pebble.node.NodeExpression;

public class NodeExpressionGetAttribute extends NodeExpression {

	public static enum Type {
		ANY, METHOD
	};

	private final Type type;
	private final NodeExpression node;
	private final NodeExpressionConstant attribute;
	private final NodeExpressionArguments args;

	public NodeExpressionGetAttribute(int lineNumber, Type type, NodeExpression node, NodeExpressionConstant attribute) {
		this(lineNumber, type, node, attribute, null);
	}

	public NodeExpressionGetAttribute(int lineNumber, Type type, NodeExpression node, NodeExpressionConstant attribute,
			NodeExpressionArguments args) {
		super(lineNumber);
		this.node = node;
		this.attribute = attribute;
		this.type = type;
		this.args = args;
	}

	@Override
	public void compile(Compiler compiler) {
		compiler.raw("getAttribute(").raw(NodeExpressionGetAttribute.Type.class.getCanonicalName()).raw(".")
				.raw(type.toString()).raw(",").subcompile(node).raw(",\"").subcompile(attribute).raw("\"");

		if (args != null) {
			for (NodeExpression arg : args.getArgs()) {
				compiler.raw(",").subcompile(arg);
			}
		}
		compiler.raw(")");
	}

}
