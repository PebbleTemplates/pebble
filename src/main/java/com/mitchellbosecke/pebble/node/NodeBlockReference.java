package com.mitchellbosecke.pebble.node;

import com.mitchellbosecke.pebble.compiler.Compiler;

public class NodeBlockReference extends AbstractNode implements DisplayableNode{

	private final String name;

	public NodeBlockReference(int lineNumber, String name) {
		super(lineNumber);
		this.name = name;
	}

	@Override
	public void compile(Compiler compiler) {
		compiler.raw("\n").write(String.format(
				"block_%s(context);\n", this.name));
	}

}
