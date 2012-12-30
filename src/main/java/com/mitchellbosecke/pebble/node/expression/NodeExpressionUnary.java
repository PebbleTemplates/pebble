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
import com.mitchellbosecke.pebble.node.Node;

public abstract class NodeExpressionUnary extends NodeExpressionOperator {

	private final Node node;

	public NodeExpressionUnary(int lineNumber, Node node) {
		super(lineNumber);
		this.node = node;
	}

	@Override
	public void compile(Compiler compiler) {
		compiler.raw("(");
		operator(compiler);
		compiler.subcompile(node).raw(")");
	}

	public abstract void operator(Compiler compiler);

}
