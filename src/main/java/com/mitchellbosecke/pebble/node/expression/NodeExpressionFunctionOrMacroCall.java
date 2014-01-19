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

public class NodeExpressionFunctionOrMacroCall extends NodeExpression {

	private final NodeExpressionConstant functionName;
	private final NodeExpressionArguments args;

	public NodeExpressionFunctionOrMacroCall(int lineNumber, NodeExpressionConstant functionName,
			NodeExpressionArguments arguments) {
		super(lineNumber);
		this.functionName = functionName;
		this.args = arguments;
	}

	@Override
	public void compile(Compiler compiler) {

		compiler.raw("applyFunctionOrMacro(").string(String.valueOf(functionName.getValue())).raw(", context");

		if (args != null) {
			for (NodeExpression arg : args.getArgs()) {
				compiler.raw(", ").subcompile(arg);
			}
		}

		compiler.raw(")");
	}

	public NodeExpressionConstant getFunctionName() {
		return functionName;
	}

	public NodeExpressionArguments getArguments() {
		return args;
	}

}
