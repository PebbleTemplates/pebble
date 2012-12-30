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
import com.mitchellbosecke.pebble.node.expression.NodeExpressionVariableName;
import com.mitchellbosecke.pebble.utils.TreeWriter;

public class NodeImport extends AbstractNode {

	private final NodeExpression importExpression;

	private final NodeExpressionDeclaration var;

	public NodeImport(int lineNumber, NodeExpression importExpression, NodeExpressionDeclaration var) {
		super(lineNumber);
		this.importExpression = importExpression;
		this.var = var;
	}

	@Override
	public void compile(Compiler compiler) {

		compiler.write("context.put(").string(var.getName()).raw(",");

		if (importExpression instanceof NodeExpressionVariableName
				&& "_self".equals(((NodeExpressionVariableName) importExpression).getName())) {
			compiler.raw("this");
		}
		else{
			compiler.raw("this.engine.loadTemplate(").subcompile(importExpression).raw(")");
		}
		compiler.raw(");\n");
	}
	
	@Override
	public void tree(TreeWriter tree) {
		tree.write("import").subtree(importExpression, true);
	}
}
