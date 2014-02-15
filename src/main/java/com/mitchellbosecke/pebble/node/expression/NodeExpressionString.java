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

public class NodeExpressionString extends NodeExpression {

	private final String value;

	public NodeExpressionString(int lineNumber, String value) {
		super(lineNumber);
		this.value = value;
	}

	@Override
	public void compile(Compiler compiler) {
		compiler.string(value);
	}

	@Override
	public void accept(NodeVisitor visitor) {
		visitor.visit(this);
	}

	public String getValue() {
		return value;
	}

}
