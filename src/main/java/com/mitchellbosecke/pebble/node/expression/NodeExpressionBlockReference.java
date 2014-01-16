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
import com.mitchellbosecke.pebble.node.DisplayableNode;
import com.mitchellbosecke.pebble.node.NodeExpression;

public class NodeExpressionBlockReference extends NodeExpression implements DisplayableNode {

	private final String name;

	/*
	 * output is true if the block is referenced in an expression using the {{
	 * block() }} function, otherwise it is false if it is referenced using
	 * block tags, ie. {% block name %}{% endblock %}
	 */
	private final boolean isExpression;

	public NodeExpressionBlockReference(int lineNumber, String name, boolean isExpression) {
		super(lineNumber);
		this.name = name;
		this.isExpression = isExpression;
	}

	@Override
	public void compile(Compiler compiler) {
		if (this.isExpression) {
			compiler.raw("block(").string(this.name).raw(", context, false)");
		} else {
			compiler.raw("\n").write("block(").string(this.name).raw(", context, false, writer);\n");
		}
	}

}
