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
import com.mitchellbosecke.pebble.node.NodeExpression;

public class NodeExpressionNamedArguments extends NodeExpression {

	/**
	 * An invokation of a function or macro can contain full fledged expressions
	 * in the arguments
	 */
	private List<NodeExpressionNamedArgument> args;

	public NodeExpressionNamedArguments(int lineNumber, List<NodeExpressionNamedArgument> args) {
		super(lineNumber);
		this.args = args;
	}

	/**
	 * Only called when not actually using named arguments, such as when calling
	 * a method of a context variable.
	 */
	@Override
	public void compile(Compiler compiler) {

		int amount = args.size();

		int i = 1;
		for (NodeExpression arg : args) {
			compiler.subcompile(arg);

			if (i < amount) {
				compiler.raw(", ");
			}

			i++;
		}

	}

	public boolean isEmpty() {
		return args.isEmpty();
	}

	/**
	 * Should only be used by NodeMacro when defining a new macro
	 * 
	 * @return
	 */
	public List<NodeExpressionNamedArgument> getArgs() {
		return args;
	}

}
