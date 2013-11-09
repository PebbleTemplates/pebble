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

public class NodeExpressionFunctionCall extends NodeExpression {

	private final NodeExpressionConstant method;
	private final NodeExpressionArguments args;

	public NodeExpressionFunctionCall(int lineNumber, NodeExpressionConstant method,
			NodeExpressionArguments arguments) {
		super(lineNumber);
		this.method = method;
		this.args = arguments;
	}

	@Override
	public void compile(Compiler compiler) {

		compiler.subcompile(method).raw("(");

		boolean isFirst = true;
		for (NodeExpression arg : args.getArgs()) {
			if (!isFirst) {
				compiler.raw(", ");
			}
			isFirst = false;

			compiler.subcompile(arg);
		}

		compiler.raw(")");
	}
	
	public NodeExpressionConstant getMethod(){
		return method;
	}

	public NodeExpressionArguments getArguments() {
		return args;
	}
	

	@Override
	public void tree(TreeWriter tree) {
		tree.write("function call ").subtree(method).subtree(args, true);
	}

}
