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
 * The right hand side to the test expression.
 * 
 * @author Mitchell
 * 
 */
public class NodeExpressionTestInvokation extends NodeExpressionBinary {

	private final NodeExpressionConstant testName;

	private final NodeExpressionArguments args;

	public NodeExpressionTestInvokation(int lineNumber, NodeExpressionConstant testName, NodeExpressionArguments args) {
		super(lineNumber);
		this.testName = testName;
		this.args = args;
	}

	@Override
	public void compile(Compiler compiler) {
		// should not be called
		throw new RuntimeException("Compile method on TestInvokation node is not supported");
	}

	public NodeExpressionArguments getArgs() {
		return args;
	}

	public NodeExpressionConstant getTestName() {
		return testName;
	}

}
