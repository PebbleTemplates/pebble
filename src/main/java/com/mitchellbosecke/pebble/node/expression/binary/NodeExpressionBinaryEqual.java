package com.mitchellbosecke.pebble.node.expression.binary;

import com.mitchellbosecke.pebble.compiler.Compiler;
import com.mitchellbosecke.pebble.node.Node;
import com.mitchellbosecke.pebble.node.expression.NodeExpressionBinary;

public class NodeExpressionBinaryEqual extends NodeExpressionBinary {

	public NodeExpressionBinaryEqual(int lineNumber, Node left, Node right) {
		super(lineNumber, left, right);
	}

	public NodeExpressionBinaryEqual() {
		super();
	}

	@Override
	public void operator(Compiler compiler) {
		compiler.raw("==");
	}

}
