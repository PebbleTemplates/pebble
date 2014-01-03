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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mitchellbosecke.pebble.compiler.Compiler;
import com.mitchellbosecke.pebble.node.NodeExpression;

public class NodeExpressionArguments extends NodeExpression {

	private NodeExpression[] args;

	public NodeExpressionArguments(int lineNumber, NodeExpression[] args) {
		super(lineNumber);
		this.args = args;
	}

	/**
	 * NodeMacro will use this method to add a secret context and writer
	 * arguments.
	 */
	public void addArgument(NodeExpressionDeclaration declaration) {
		List<NodeExpression> arguments = new ArrayList<>(Arrays.asList(args));
		arguments.add(declaration);
		this.args = arguments.toArray(new NodeExpression[arguments.size()]);
	}

	@Override
	public void compile(Compiler compiler) {
		compiler.raw("(");

		NodeExpression var;
		for (int i = 0; i < args.length; ++i) {
			var = args[i];
			compiler.subcompile(var, true);

			if (i < (args.length - 1)) {
				compiler.raw(", ");
			}
		}

		compiler.raw(")");
	}

	public NodeExpression[] getArgs() {
		return args;
	}

}
