package com.mitchellbosecke.pebble.node;

import com.mitchellbosecke.pebble.compiler.Compiler;

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
						"public void block_%s(Map<String,Object> context) {\n",
						this.name)).indent().subcompile(body).raw("\n").outdent()
				.write("}\n");

	}
	
	

}
