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
import com.mitchellbosecke.pebble.node.DisplayableNode;
import com.mitchellbosecke.pebble.node.NodeBlock;
import com.mitchellbosecke.pebble.node.NodeExpression;
import com.mitchellbosecke.pebble.utils.TreeWriter;

public class NodeExpressionBlockReference extends NodeExpression implements DisplayableNode {

	private final String name;

	private final boolean output;

	public NodeExpressionBlockReference(int lineNumber, String name, boolean output) {
		super(lineNumber);
		this.name = name;
		this.output = output;
	}

	@Override
	public void compile(Compiler compiler) {
		if (!this.output) {
			compiler.raw("\n").write(String.format("builder.append(block_%s(context));\n", this.name));
		} else {
			compiler.raw(String.format("%s%s(context)\n", NodeBlock.BLOCK_PREFIX, this.name));
		}
	}

	@Override
	public void tree(TreeWriter tree) {
		tree.write(String.format("block reference [%s]", name));
	}

}
