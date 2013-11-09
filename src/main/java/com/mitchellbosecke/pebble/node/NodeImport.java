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

public class NodeImport extends AbstractNode {

	private final NodeExpression importExpression;

	private final NodeExpressionDeclaration var;

	public NodeImport(int lineNumber, NodeExpression importExpression, NodeExpressionDeclaration var) {
		super(lineNumber);
		this.importExpression = importExpression;
		this.var = var;
	}

	@Override
	public void compile(Compiler compiler) {

		compiler.write("context.put(").string(var.getName()).raw(",");

		compiler.raw("this.engine.loadTemplate(").subcompile(importExpression).raw(")");
		compiler.raw(");\n");
	}
	
	@Override
	public void tree(TreeWriter tree) {
		tree.write("import").subtree(importExpression, true);
	}
}
