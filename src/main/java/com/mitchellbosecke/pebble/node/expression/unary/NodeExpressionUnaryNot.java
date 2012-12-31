/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Copyright (c) 2012 Mitchell Bosecke.
 * 
 * This work is licensed under the Creative Commons Attribution-ShareAlike 3.0 
 * Unported License. To view a copy of this license, visit 
 * http://creativecommons.org/licenses/by-sa/3.0/
 ******************************************************************************/
package com.mitchellbosecke.pebble.node.expression.unary;

import com.mitchellbosecke.pebble.compiler.Compiler;
import com.mitchellbosecke.pebble.node.expression.NodeExpressionUnary;
import com.mitchellbosecke.pebble.utils.TreeWriter;

public class NodeExpressionUnaryNot extends NodeExpressionUnary {

	@Override
	public void operator(Compiler compiler) {
		compiler.raw("!");
	}
	
	@Override
	public void tree(TreeWriter tree) {
		tree.write("not").subtree(node, true);
	}

}
