package com.mitchellbosecke.pebble.node.expression;

import com.mitchellbosecke.pebble.compiler.Compiler;
import com.mitchellbosecke.pebble.node.Node;

public abstract class NodeExpressionBinary extends NodeExpressionOperator {

	private Node left;
	private Node right;
	
	public NodeExpressionBinary(){
		super();
	}

	public NodeExpressionBinary(int lineNumber, Node left, Node right) {
		super(lineNumber);
		this.left = left;
		this.right = right;
	}

	@Override
	public void compile(Compiler compiler) {
		compiler.raw("(").subcompile(left).raw(" ");
		this.operator(compiler);
		compiler.raw(" ").subcompile(right).raw(")");
	}
	
	public void setLeft(Node left){
		this.left = left;
	}
	
	public void setRight(Node right){
		this.right = right;
	}

}
