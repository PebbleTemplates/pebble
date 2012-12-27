package com.mitchellbosecke.pebble.node.expression.binary;

import com.mitchellbosecke.pebble.compiler.Compiler;
import com.mitchellbosecke.pebble.node.expression.NodeExpressionBinary;

public class NodeExpressionBinaryAnd extends NodeExpressionBinary {

	public NodeExpressionBinaryAnd() {
		super();
	}

	@Override
	public void operator(Compiler compiler) {
		compiler.raw("&&");
	}

}
