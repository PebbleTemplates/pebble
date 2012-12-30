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
import com.mitchellbosecke.pebble.utils.TreeWriter;

public class NodeExpressionDeclaration extends NodeExpression {

	private final String name;

	public NodeExpressionDeclaration(int lineNumber, String name) {
		super(lineNumber);
		this.name = name;
	}

	/**
	 * This compile function is really only useful when compiling a method
	 * declaration. It becomes useless when compiling a method call or the
	 * declaration is part of the "set" node.
	 * 
	 * useful:
	 * public String method(Object name){ ... };
	 * 
	 * useless:
	 * obj.method(name);
	 * context.put(name, true); 
	 */
	@Override
	public void compile(Compiler compiler) {
		compiler.raw(String.format("Object %s", name));
	}

	public String getName() {
		return name;
	}

	@Override
	public void tree(TreeWriter tree) {
		tree.write(String.format("declaration [%s]", name));
	}

}
