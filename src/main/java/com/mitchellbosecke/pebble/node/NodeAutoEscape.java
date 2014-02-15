package com.mitchellbosecke.pebble.node;

import com.mitchellbosecke.pebble.compiler.Compiler;
import com.mitchellbosecke.pebble.extension.NodeVisitor;

public class NodeAutoEscape extends AbstractNode {

	private final NodeBody body;

	private final String strategy;

	private final boolean active;

	public NodeAutoEscape(int lineNumber, NodeBody body, boolean active, String strategy) {
		super(lineNumber);
		this.body = body;
		this.strategy = strategy;
		this.active = active;
	}

	@Override
	public void compile(Compiler compiler) {
		compiler.subcompile(getBody());
	}

	@Override
	public void accept(NodeVisitor visitor) {
		visitor.visit(this);
	}

	public NodeBody getBody() {
		return body;
	}

	public String getStrategy() {
		return strategy;
	}

	public boolean isActive() {
		return active;
	}

}
