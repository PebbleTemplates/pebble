/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Original work Copyright (c) 2009-2013 by the Twig Team
 * Modified work Copyright (c) 2013 by Mitchell Bösecke
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.node.expression.binary;

import com.mitchellbosecke.pebble.compiler.Compiler;
import com.mitchellbosecke.pebble.node.NodeExpression;
import com.mitchellbosecke.pebble.node.expression.NodeExpressionArguments;
import com.mitchellbosecke.pebble.node.expression.NodeExpressionBinary;
import com.mitchellbosecke.pebble.node.expression.NodeExpressionConstant;
import com.mitchellbosecke.pebble.node.expression.NodeExpressionFunctionCall;
import com.mitchellbosecke.pebble.node.expression.NodeExpressionVariableName;

public class NodeExpressionBinaryIsNot extends NodeExpressionBinary {

	@Override
	public void compile(Compiler compiler) {

		NodeExpressionConstant method;
		NodeExpressionArguments args = null;

		if (rightExpression instanceof NodeExpressionFunctionCall) {

			method = ((NodeExpressionFunctionCall) rightExpression).getMethod();
			args = ((NodeExpressionFunctionCall) rightExpression).getArguments();

		} else {

			/*
			 * We allow the user to omit the brackets when calling tests that
			 * dont require arguments. The parser parses this as a
			 * "variable name" node instead of a constant or function call. We
			 * have to make the conversion here.
			 * 
			 * TODO: Is this too much of a hack? Should the parser somehow be
			 * tweaked to be more intelligent?
			 */
			NodeExpressionVariableName name = (NodeExpressionVariableName) rightExpression;
			method = new NodeExpressionConstant(name.getLineNumber(), name.getName());
		}

		compiler.raw("(applyTest(").string(String.valueOf(method.getValue()));

		compiler.raw(",").subcompile(leftExpression);

		if (args != null) {
			for (NodeExpression arg : args.getArgs()) {
				compiler.raw(", ").subcompile(arg);
			}
		}

		compiler.raw(") == false)");
	}

}
