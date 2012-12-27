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
import com.mitchellbosecke.pebble.node.Node;
import com.mitchellbosecke.pebble.node.expression.NodeExpressionBinary;

public class NodeExpressionBinaryOr extends NodeExpressionBinary {

	public NodeExpressionBinaryOr() {
		super();
	}

	public NodeExpressionBinaryOr(int lineNumber, Node left, Node right) {
		super(lineNumber, left, right);
	}

	@Override
	public void operator(Compiler compiler) {
		compiler.raw("||");
	}
}
