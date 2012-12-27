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
