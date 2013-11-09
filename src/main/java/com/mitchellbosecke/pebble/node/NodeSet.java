/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Original work Copyright (c) 2009-2013 by the Twig Team
 * Modified work Copyright (c) 2013 by Mitchell Bösecke
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.node;

import com.mitchellbosecke.pebble.compiler.Compiler;
import com.mitchellbosecke.pebble.node.expression.NodeExpressionDeclaration;
import com.mitchellbosecke.pebble.utils.TreeWriter;

public class NodeSet extends AbstractNode {

	private final NodeExpressionDeclaration name;

	private final NodeExpression value;


	public NodeSet(int lineNumber, NodeExpressionDeclaration name, NodeExpression value) {
		super(lineNumber);
		this.name = name;
		this.value = value;
	}

	@Override
	public void compile(Compiler compiler) {
		compiler.raw("\n").write("context.put(").string(name.getName()).raw(",").subcompile(value).raw(");\n");
	}
	
	@Override
	public void tree(TreeWriter tree) {
		tree.write("set").subtree(name).subtree(value, true);
	}
}
