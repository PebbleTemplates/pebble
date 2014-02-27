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

import com.mitchellbosecke.pebble.extension.NodeVisitor;

public abstract class BinaryExpression<T> implements Expression<T> {

	private Expression<?> leftExpression;
	private Expression<?> rightExpression;

	public void setLeft(Expression<?> left) {
		this.leftExpression = left;
	}

	public void setRight(Expression<?> right) {
		this.rightExpression = right;
	}

	public Expression<?> getLeftExpression() {
		return leftExpression;
	}

	public Expression<?> getRightExpression() {
		return rightExpression;
	}

	@Override
	public void accept(NodeVisitor visitor) {
		visitor.visit(this);
	}

}
