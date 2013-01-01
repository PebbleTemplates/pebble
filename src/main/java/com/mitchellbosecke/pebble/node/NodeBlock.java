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

public class NodeBlock extends AbstractNode {

	private NodeBody body;
	private String name;
	
	public NodeBlock(int lineNumber, String name) {
		this(lineNumber, name, null);
	}

	public NodeBlock(int lineNumber, String name, NodeBody body) {
		super(lineNumber);
		this.body = body;
		this.name = name;
	}
	
	public void setBody(NodeBody body){
		this.body = body;
	}

	@Override
	public void compile(Compiler compiler) {
		compiler.write(
				String.format(
						"public String block_%s(Map<String,Object> context) {\n",
						this.name)).indent();
		
		compiler.write("StringBuilder builder = new StringBuilder();\n");
		
		compiler.subcompile(body);
		
		compiler.write("return builder.toString();");
		
		compiler.raw("\n").outdent()
				.write("}\n");

	}
	

	@Override
	public void tree(TreeWriter tree) {
		tree.write(String.format("block [%s]", name)).subtree(body, true);
	}

}
