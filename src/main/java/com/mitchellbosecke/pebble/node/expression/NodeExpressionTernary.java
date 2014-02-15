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

public class NodeExpressionTernary extends NodeExpression {

	private final NodeExpression expression1;
	private NodeExpression expression2;
	private NodeExpression expression3;

	public NodeExpressionTernary(int lineNumber, NodeExpression expression1, NodeExpression expression2,
			NodeExpression expression3) {

		super(lineNumber);
		this.expression1 = expression1;
		this.setExpression2(expression2);
		this.setExpression3(expression3);
	}

	@Override
	public void compile(Compiler compiler) {
		compiler.raw("(((Boolean)").subcompile(expression1).raw(")?").subcompile(getExpression2()).raw(":")
				.subcompile(getExpression3()).raw(")");
	}

	@Override
	public void accept(NodeVisitor visitor) {
		visitor.visit(this);
	}

	public NodeExpression getExpression1() {
		return expression1;
	}

	public NodeExpression getExpression2() {
		return expression2;
	}

	public NodeExpression getExpression3() {
		return expression3;
	}

	public void setExpression3(NodeExpression expression3) {
		this.expression3 = expression3;
	}

	public void setExpression2(NodeExpression expression2) {
		this.expression2 = expression2;
	}
}
