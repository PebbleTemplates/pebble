/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Copyright (c) 2012 Mitchell Bosecke.
 * 
 * This work is licensed under the Creative Commons Attribution-ShareAlike 3.0 
 * Unported License. To view a copy of this license, visit 
 * http://creativecommons.org/licenses/by-sa/3.0/
 ******************************************************************************/
package com.mitchellbosecke.pebble.node;

import com.mitchellbosecke.pebble.compiler.Compiler;
import com.mitchellbosecke.pebble.utils.TreeWriter;

public class NodeTernary extends NodeExpression {

	private final NodeExpression expression1;
	private final NodeExpression expression2;
	private final NodeExpression expression3;

	public NodeTernary(int lineNumber, NodeExpression expression1, NodeExpression expression2, NodeExpression expression3) {

		super(lineNumber);
		this.expression1 = expression1;
		this.expression2 = expression2;
		this.expression3 = expression3;
	}

	@Override
	public void compile(Compiler compiler) {
		
		/*
		 * The 
		 */
		compiler.raw("(((Boolean)").subcompile(expression1).raw(")?").subcompile(expression2).raw(":").subcompile(expression3).raw(")");
		
	}
	
	@Override
	public void tree(TreeWriter tree) {
		tree.write("ternary");
		tree.subtree(expression1);
		tree.subtree(expression2);
		tree.subtree(expression3, true);
	}
}
