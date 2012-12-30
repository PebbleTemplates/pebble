/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Copyright (c) 2012 Mitchell Bosecke.
 * 
 * This work is licensed under the Creative Commons Attribution-ShareAlike 3.0 
 * Unported License. To view a copy of this license, visit 
 * http://creativecommons.org/licenses/by-sa/3.0/
 ******************************************************************************/
package com.mitchellbosecke.pebble.node.expression.binary;

import com.mitchellbosecke.pebble.compiler.Compiler;
import com.mitchellbosecke.pebble.node.expression.NodeExpressionBinarySimple;
import com.mitchellbosecke.pebble.utils.TreeWriter;

public class NodeExpressionBinaryAdd extends NodeExpressionBinarySimple {

	@Override
	public void operator(Compiler compiler) {
		compiler.raw("+");
	}

	@Override
	public void tree(TreeWriter tree) {
		tree.write("+").subtree(left).subtree(right, true);
	}
}
