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

public class NodeInclude extends AbstractNode implements DisplayableNode{

	private final NodeExpression includeExpression;

	public NodeInclude(int lineNumber, NodeExpression includeExpression) {
		super(lineNumber);
		this.includeExpression = includeExpression;
	}

	@Override
	public void compile(Compiler compiler) {

		compiler.raw("\n").write("builder.append(");
		
		compiler.raw("this.engine.loadTemplate(").subcompile(includeExpression).raw(").render()");
		
		compiler.raw(");\n");
	}
	
	@Override
	public void tree(TreeWriter tree) {
		tree.write("include").subtree(includeExpression, true);
	}
}
