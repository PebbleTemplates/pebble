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
