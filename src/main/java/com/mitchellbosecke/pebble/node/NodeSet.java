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
import com.mitchellbosecke.pebble.node.expression.NodeExpressionDeclaration;
import com.mitchellbosecke.pebble.utils.TreeWriter;

public class NodeSet extends AbstractNode {

	private final NodeExpressionDeclaration name;

	private final NodeExpression value;


	public NodeSet(int lineNumber, NodeExpressionDeclaration name, NodeExpression value) {
		super(lineNumber);
		this.name = name;
		this.value = value;
	}

	@Override
	public void compile(Compiler compiler) {
		compiler.raw("\n").write("context.put(").string(name.getName()).raw(",").subcompile(value).raw(");\n");
	}
	
	@Override
	public void tree(TreeWriter tree) {
		tree.write("set").subtree(name).subtree(value, true);
	}
}
