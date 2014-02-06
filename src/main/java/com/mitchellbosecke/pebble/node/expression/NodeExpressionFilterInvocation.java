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

/**
 * The right hand side to the filter expression.
 * 
 * @author Mitchell
 *
 */
public class NodeExpressionFilterInvocation extends NodeExpressionBinary {

	private final NodeExpressionConstant filterName;

	private final NodeExpressionNamedArguments args;

	public NodeExpressionFilterInvocation(int lineNumber, NodeExpressionConstant filterName,
			NodeExpressionNamedArguments args) {
		super(lineNumber);
		this.filterName = filterName;
		this.args = args;
	}

	@Override
	public void compile(Compiler compiler) {
		/*
		 * The NodeExpressionBinaryFilter.class will handle compilation
		 */
		throw new RuntimeException("Compile method on FilterInvokation node is not supported");
	}

	public NodeExpressionNamedArguments getArgs() {
		return args;
	}

	public NodeExpressionConstant getFilterName() {
		return filterName;
	}

}
