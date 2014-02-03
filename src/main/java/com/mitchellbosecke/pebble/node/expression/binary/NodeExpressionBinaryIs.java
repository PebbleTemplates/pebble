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
import com.mitchellbosecke.pebble.node.expression.NodeExpressionArguments;
import com.mitchellbosecke.pebble.node.expression.NodeExpressionBinary;
import com.mitchellbosecke.pebble.node.expression.NodeExpressionConstant;
import com.mitchellbosecke.pebble.node.expression.NodeExpressionContextVariable;
import com.mitchellbosecke.pebble.node.expression.NodeExpressionFunctionOrMacroCall;

public class NodeExpressionBinaryIs extends NodeExpressionBinary {

	@Override
	public void compile(Compiler compiler) {

		NodeExpressionConstant testName;
		NodeExpressionArguments args = null;

		if (rightExpression instanceof NodeExpressionFunctionOrMacroCall) {

			testName = ((NodeExpressionFunctionOrMacroCall) rightExpression).getFunctionName();
			args = ((NodeExpressionFunctionOrMacroCall) rightExpression).getArguments();

		} else if (rightExpression instanceof NodeExpressionContextVariable){

			/*
			 * We allow the user to omit the brackets when calling tests that
			 * dont require arguments. The parser parses this as a
			 * "variable name" node instead of a constant or function call. We
			 * have to make the conversion here.
			 * 
			 * TODO: Is this too much of a hack? Should the parser somehow be
			 * tweaked to be more intelligent? Perhaps parser has access to test
			 * names through the main engine?
			 */
			NodeExpressionContextVariable name = (NodeExpressionContextVariable) rightExpression;
			testName = new NodeExpressionConstant(name.getLineNumber(), name.getName());
			
		} else {
			testName = ((NodeExpressionConstant) rightExpression);
		}

		compiler.raw("applyTest(").string(String.valueOf(testName.getValue()));

		compiler.raw(",").subcompile(leftExpression);

		if (args != null && !args.getArgs().isEmpty()) {
			compiler.raw(", ");
			compiler.subcompile(args);
		}

		compiler.raw(")");
	}

}
