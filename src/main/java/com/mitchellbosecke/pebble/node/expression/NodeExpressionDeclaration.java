package com.mitchellbosecke.pebble.node.expression;

import com.mitchellbosecke.pebble.compiler.Compiler;
import com.mitchellbosecke.pebble.node.NodeExpression;

public class NodeExpressionDeclaration extends NodeExpression {

	private final String name;

	public NodeExpressionDeclaration(int lineNumber, String name) {
		super(lineNumber);
		this.name = name;
	}

	@Override
	public void compile(Compiler compiler) {
		compiler.raw(String.format("Object %s", name));
	}
	
	public String getName(){
		return name;
	}

}
