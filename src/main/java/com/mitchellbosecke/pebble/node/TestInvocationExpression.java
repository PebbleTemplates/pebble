/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Original work Copyright (c) 2009-2013 by the Twig Team
 * Modified work Copyright (c) 2013 by Mitchell Bösecke
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.node;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.extension.NodeVisitor;
import com.mitchellbosecke.pebble.node.expression.Expression;
import com.mitchellbosecke.pebble.template.EvaluationContext;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;

/**
 * The right hand side to the test expression.
 * 
 * @author Mitchell
 * 
 */
public class TestInvocationExpression implements Expression<Object> {

	private final String testName;

	private final ArgumentsNode args;

	@Override
	public Object evaluate(PebbleTemplateImpl self, EvaluationContext context) throws PebbleException {
		throw new UnsupportedOperationException();
	}

	public TestInvocationExpression(int lineNumber, String testName, ArgumentsNode args) {
		this.testName = testName;
		this.args = args;
	}

	@Override
	public void accept(NodeVisitor visitor) {
		visitor.visit(this);
	}

	public ArgumentsNode getArgs() {
		return args;
	}

	public String getTestName() {
		return testName;
	}

}
