package com.mitchellbosecke.pebble.node.expression;

import java.util.ArrayList;
import java.util.List;

import com.mitchellbosecke.pebble.compiler.Compiler;
import com.mitchellbosecke.pebble.node.Node;
import com.mitchellbosecke.pebble.node.NodeExpression;

public class NodeExpressionNamedArgument extends NodeExpression {

	private NodeExpressionNewVariableName name;

	private NodeExpression value;

	public NodeExpressionNamedArgument(NodeExpressionNewVariableName name, NodeExpression value) {
		this.setName(name);
		this.setValue(value);
	}

	@Override
	public void compile(Compiler compiler) {
		compiler.subcompile(value);
	}
	
	@Override
	public List<Node> getChildren(){
		List<Node> children = new ArrayList<>();
		children.add(name);
		children.add(value);
		return children;
	}

	public NodeExpression getValue() {
		return value;
	}

	public void setValue(NodeExpression value) {
		this.value = value;
	}

	public NodeExpressionNewVariableName getName() {
		return name;
	}

	public void setName(NodeExpressionNewVariableName name) {
		this.name = name;
	}

}
