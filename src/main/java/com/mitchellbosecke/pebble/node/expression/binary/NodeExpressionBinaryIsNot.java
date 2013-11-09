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
import com.mitchellbosecke.pebble.node.expression.NodeExpressionBinaryCallable;
import com.mitchellbosecke.pebble.node.expression.NodeExpressionConstant;
import com.mitchellbosecke.pebble.node.expression.NodeExpressionFunctionCall;
import com.mitchellbosecke.pebble.node.expression.NodeExpressionVariableName;
import com.mitchellbosecke.pebble.utils.TreeWriter;

public class NodeExpressionBinaryIsNot extends NodeExpressionBinaryCallable {

	@Override
	public void compile(Compiler compiler) {

		NodeExpressionConstant method;
		NodeExpressionArguments args = null;

		if (right instanceof NodeExpressionFunctionCall) {

			method = ((NodeExpressionFunctionCall) right).getMethod();
			args = ((NodeExpressionFunctionCall) right).getArguments();

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
			NodeExpressionVariableName name = (NodeExpressionVariableName) right;
			method = new NodeExpressionConstant(name.getLineNumber(), name.getName());
		}

		compiler.raw("(applyTest(").string(String.valueOf(method.getValue()));

		compiler.raw(",").subcompile(left);

		if (args != null) {
			for (NodeExpression arg : args.getArgs()) {
				compiler.raw(", ").subcompile(arg);
			}
		}

		compiler.raw(") == false)");
	}

	@Override
	public void tree(TreeWriter tree) {
		tree.write("binary is not").subtree(left).subtree(right, true);

	}

}
