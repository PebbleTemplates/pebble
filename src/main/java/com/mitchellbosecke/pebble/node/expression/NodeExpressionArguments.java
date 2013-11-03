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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mitchellbosecke.pebble.compiler.Compiler;
import com.mitchellbosecke.pebble.node.NodeExpression;
import com.mitchellbosecke.pebble.utils.TreeWriter;

public class NodeExpressionArguments extends NodeExpression {

	private NodeExpression[] args;

	public NodeExpressionArguments(int lineNumber, NodeExpression[] args) {
		super(lineNumber);
		this.args = args;
	}
	
	/**
	 * NodeMacro will use this method to add a secret _context argument
	 * when compiling macro declarations.
	 */
	public void addArgument(NodeExpressionDeclaration declaration){
		List<NodeExpression> arguments = new ArrayList<>(Arrays.asList(args));
		arguments.add(declaration);
		this.args = arguments.toArray(new NodeExpression[arguments.size()]);
	}

	@Override
	public void compile(Compiler compiler) {
		compiler.raw("(");

		NodeExpression var;
		for (int i = 0; i < args.length; ++i) {
			var = args[i];
			compiler.subcompile(var, true);

			if (i < (args.length - 1)) {
				compiler.raw(",");
			}
		}

		compiler.raw(")");
	}

	public NodeExpression[] getArgs(){
		return args;
	}
	
	@Override
	public void tree(TreeWriter tree) {
		tree.write("arguments");
		
		for (int i = 0; i < args.length; ++i) {
			if (i == (args.length - 1)) {
				tree.subtree(args[i], true);
			} else {
				tree.subtree(args[i]);
			}
		}
	}
}
