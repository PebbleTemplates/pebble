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

import java.util.List;

import com.mitchellbosecke.pebble.compiler.Compiler;

public class NodeBody extends AbstractNode {
	
	private final List<Node> children;

	public NodeBody(int lineNumber, List<Node> children) {
		super(lineNumber);
		this.children = children;
	}

	@Override
	public void compile(Compiler compiler) {
		for(Node child: children){
			child.compile(compiler);
		}
	}

}
