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

public class NodeText extends AbstractNode implements DisplayableNode {

	private final String data;

	public NodeText(String data, int lineNumber) {
		super(lineNumber);
		this.data = data;
	}

	@Override
	public void compile(Compiler compiler) {
		compiler.raw("\n").write("builder.append(String.valueOf(").string(getData()).raw("));");
	}

	public String getData() {
		return data;
	}
	
	@Override
	public void tree(TreeWriter tree) {
		String data = this.data.replace("\r\n", " ").replace("\n", " ");
		tree.write(String.format("text [%s]", data));
	}

}
