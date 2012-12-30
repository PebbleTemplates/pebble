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

public class NodeExpressionFilter extends NodeExpression {

	protected final NodeExpression node;

	protected final NodeExpressionConstant filterName;

	protected final NodeExpressionArguments args;

	public NodeExpressionFilter(int lineNumber, NodeExpression node, NodeExpressionConstant filterName,
			NodeExpressionArguments args) {
		super(lineNumber);
		this.node = node;
		this.filterName = filterName;
		this.args = args;
	}

	@Override
	public void compile(Compiler compiler) {

		compiler.raw("applyFilter(").string(String.valueOf(filterName.getValue()));
		
		compiler.raw(",").subcompile(node);

		if (args != null) {
			for (NodeExpression arg : args.getArgs()) {
				compiler.raw(", ").subcompile(arg);
			}
		}

		compiler.raw(")");
	}
	
	@Override
	public void tree(TreeWriter tree) {
		tree.write("filter").subtree(node).subtree(filterName).subtree(args, true);
	}

}
