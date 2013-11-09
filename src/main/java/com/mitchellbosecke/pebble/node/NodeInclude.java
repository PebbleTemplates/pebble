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
import com.mitchellbosecke.pebble.utils.TreeWriter;

public class NodeInclude extends AbstractNode implements DisplayableNode{

	private final NodeExpression includeExpression;

	public NodeInclude(int lineNumber, NodeExpression includeExpression) {
		super(lineNumber);
		this.includeExpression = includeExpression;
	}

	@Override
	public void compile(Compiler compiler) {

		compiler.raw("\n").write("builder.append(");
		
		compiler.raw("this.engine.loadTemplate(").subcompile(includeExpression).raw(").render()");
		
		compiler.raw(");\n");
	}
	
	@Override
	public void tree(TreeWriter tree) {
		tree.write("include").subtree(includeExpression, true);
	}
}
