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

import java.util.List;

import com.mitchellbosecke.pebble.compiler.Compiler;
import com.mitchellbosecke.pebble.extension.NodeVisitor;
import com.mitchellbosecke.pebble.node.NodeExpression;
import com.mitchellbosecke.pebble.template.ArgumentMap;

public class NodeExpressionNamedArguments extends NodeExpression {

	/**
	 * An invokation of a function or macro can contain full fledged expressions
	 * in the arguments
	 */
	private final List<NodeExpressionNamedArgument> args;

	public NodeExpressionNamedArguments(int lineNumber, List<NodeExpressionNamedArgument> args) {
		super(lineNumber);
		this.args = args;
	}

	@Override
	public void compile(Compiler compiler) {

		compiler.raw(ArgumentMap.class.getName()).raw(".create()");

		if (getArgs() != null) {
			for (NodeExpressionNamedArgument arg : getArgs()) {
				compiler.raw(".add(");
				if (arg.getName() == null) {
					compiler.raw("null");
				} else {
					compiler.string(arg.getName().getName());
				}
				compiler.raw(",").subcompile(arg.getValue()).raw(")");
			}
		}
	}

	@Override
	public void accept(NodeVisitor visitor) {
		visitor.visit(this);
	}

	public List<NodeExpressionNamedArgument> getArgs() {
		return args;
	}

}
