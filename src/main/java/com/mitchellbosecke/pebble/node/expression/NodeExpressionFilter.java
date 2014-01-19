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

public class NodeExpressionFilter extends NodeExpression {

	protected final NodeExpression node;

	protected final NodeExpressionConstant filterName;

	protected final NodeExpressionArguments args;

	public NodeExpressionFilter(int lineNumber, NodeExpression node, NodeExpressionConstant filterName,
			NodeExpressionArguments args) {
		super(lineNumber);
		this.node = node;
		this.filterName = filterName;
		this.args = args;
	}

	@Override
	public void compile(Compiler compiler) {

		compiler.raw("applyFilter(").string(String.valueOf(filterName.getValue())).raw(", context");

		compiler.raw(",").subcompile(node);

		if (args != null) {
			for (NodeExpression arg : args.getArgs()) {
				compiler.raw(", ").subcompile(arg);
			}
		}

		compiler.raw(")");
	}

}
