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

public class NodeExpressionArguments extends NodeExpression {

	private final NodeExpressionDeclaration[] args;

	public NodeExpressionArguments(int lineNumber, NodeExpressionDeclaration[] args) {
		super(lineNumber);
		this.args = args;
	}

	@Override
	public void compile(Compiler compiler) {
		compiler.raw("(");

		NodeExpressionDeclaration var;
		for (int i = 0; i < args.length; ++i) {
			var = args[i];
			compiler.subcompile(var, true);

			if (i < (args.length - 1)) {
				compiler.raw(",");
			}
		}

		compiler.raw(")");
	}

	public NodeExpressionDeclaration[] getArgs(){
		return args;
	}
}
