/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Original work Copyright (c) 2009-2013 by the Twig Team
 * Modified work Copyright (c) 2013 by Mitchell Bösecke
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
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
