/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Copyright (c) 2012 Mitchell Bosecke.
 * 
 * This work is licensed under the Creative Commons Attribution-ShareAlike 3.0 
 * Unported License. To view a copy of this license, visit 
 * http://creativecommons.org/licenses/by-sa/3.0/
 ******************************************************************************/
package com.mitchellbosecke.pebble.node.expression;

import com.mitchellbosecke.pebble.node.NodeExpression;

public abstract class NodeExpressionBinary extends NodeExpressionOperator {

	protected NodeExpression left;
	protected NodeExpression right;
	
	public NodeExpressionBinary(){
		super();
	}

	public NodeExpressionBinary(int lineNumber, NodeExpression left, NodeExpression right) {
		super(lineNumber);
		this.left = left;
		this.right = right;
	}
	
	public void setLeft(NodeExpression left){
		this.left = left;
	}
	
	public void setRight(NodeExpression right){
		this.right = right;
	}

}
