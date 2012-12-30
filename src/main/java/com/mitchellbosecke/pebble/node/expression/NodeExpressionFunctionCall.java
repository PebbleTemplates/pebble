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
import com.mitchellbosecke.pebble.utils.TreeWriter;

public class NodeExpressionFunctionCall extends NodeExpression {

	private final NodeExpressionConstant method;
	private final NodeExpressionArguments args;

	public NodeExpressionFunctionCall(int lineNumber, NodeExpressionConstant method,
			NodeExpressionArguments arguments) {
		super(lineNumber);
		this.method = method;
		this.args = arguments;
	}

	@Override
	public void compile(Compiler compiler) {

		compiler.subcompile(method).raw("(");

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
	
	public NodeExpressionConstant getMethod(){
		return method;
	}

	public NodeExpressionArguments getArguments() {
		return args;
	}
	

	@Override
	public void tree(TreeWriter tree) {
		tree.write("function call ").subtree(method).subtree(args, true);
	}

}
