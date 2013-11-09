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

public class NodeExpressionTernary extends NodeExpression {

	private final NodeExpression expression1;
	private final NodeExpression expression2;
	private final NodeExpression expression3;

	public NodeExpressionTernary(int lineNumber, NodeExpression expression1, NodeExpression expression2, NodeExpression expression3) {

		super(lineNumber);
		this.expression1 = expression1;
		this.expression2 = expression2;
		this.expression3 = expression3;
	}

	@Override
	public void compile(Compiler compiler) {
		
		/*
		 * The 
		 */
		compiler.raw("(((Boolean)").subcompile(expression1).raw(")?").subcompile(expression2).raw(":").subcompile(expression3).raw(")");
		
	}
	
	@Override
	public void tree(TreeWriter tree) {
		tree.write("ternary");
		tree.subtree(expression1);
		tree.subtree(expression2);
		tree.subtree(expression3, true);
	}
}
