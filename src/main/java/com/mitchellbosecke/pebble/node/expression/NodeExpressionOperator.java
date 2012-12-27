package com.mitchellbosecke.pebble.node.expression;

import com.mitchellbosecke.pebble.compiler.Compiler;
import com.mitchellbosecke.pebble.node.NodeExpression;

public abstract class NodeExpressionOperator extends NodeExpression{
	
	public NodeExpressionOperator(){
		super();
	}

	public NodeExpressionOperator(int lineNumber) {
		super(lineNumber);
	}

	public abstract void operator(Compiler compiler);
}
