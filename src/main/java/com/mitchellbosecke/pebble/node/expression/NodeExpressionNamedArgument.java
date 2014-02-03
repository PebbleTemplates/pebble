package com.mitchellbosecke.pebble.node.expression;

import com.mitchellbosecke.pebble.compiler.Compiler;
import com.mitchellbosecke.pebble.node.NodeExpression;

public class NodeExpressionNamedArgument extends NodeExpression{
	
	private NodeExpressionNewVariableName name;
	
	private NodeExpression value;
	
	public NodeExpressionNamedArgument(NodeExpressionNewVariableName name, NodeExpression value){
		this.name = name;
		this.value = value;
	}

	@Override
	public void compile(Compiler compiler) {
		// TODO Auto-generated method stub
		
	}

	
	

}
