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

public class NodeExpressionMethodCall extends NodeExpression {

	private final NodeExpression node;
	private final NodeExpressionConstant method;
	private final NodeExpressionArguments args;

	public NodeExpressionMethodCall(int lineNumber, NodeExpression node, NodeExpressionConstant method,
			NodeExpressionArguments arguments) {
		super(lineNumber);
		this.node = node;
		this.method = method;
		this.args = arguments;
	}

	@Override
	public void compile(Compiler compiler) {

		compiler.subcompile(node).raw(".").subcompile(method).raw("(");

		boolean isFirst = true;
		for (NodeExpression arg : args.getArgs()) {
			if (!isFirst) {
				compiler.raw(", ");
			}
			isFirst = false;

			compiler.subcompile(arg);
		}

		compiler.raw(")");
	}

}
