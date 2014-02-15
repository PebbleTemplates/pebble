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
import com.mitchellbosecke.pebble.extension.NodeVisitor;
import com.mitchellbosecke.pebble.node.NodeExpression;

/**
 * The right hand side to the test expression.
 * 
 * @author Mitchell
 * 
 */
public class NodeExpressionTestInvocation extends NodeExpression {

	private final NodeExpressionConstant testName;

	private final NodeExpressionNamedArguments args;

	public NodeExpressionTestInvocation(int lineNumber, NodeExpressionConstant testName,
			NodeExpressionNamedArguments args) {
		super(lineNumber);
		this.testName = testName;
		this.args = args;
	}

	@Override
	public void compile(Compiler compiler) {
		/*
		 * The NodeExpressionBinaryTestPositive.class will handle compilation
		 */
		throw new RuntimeException("Compile method on TestInvokation node is not supported");
	}

	@Override
	public void accept(NodeVisitor visitor) {
		visitor.visit(this);
	}

	public NodeExpressionNamedArguments getArgs() {
		return args;
	}

	public NodeExpressionConstant getTestName() {
		return testName;
	}

}
