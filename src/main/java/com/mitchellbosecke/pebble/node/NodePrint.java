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

import com.mitchellbosecke.pebble.compiler.Compiler;
import com.mitchellbosecke.pebble.compiler.NodeVisitor;

public class NodePrint extends AbstractNode {

	private final NodeExpression expression;

	public NodePrint(NodeExpression expression, int lineNumber) {
		super(lineNumber);
		this.expression = expression;
	}

	@Override
	public void compile(Compiler compiler) {

		compiler.write("writer.write(printVariable(").subcompile(getExpression()).raw("));").newline();
	}

	@Override
	public void accept(NodeVisitor visitor) {
		visitor.visit(this);
	}

	public NodeExpression getExpression() {
		return expression;
	}

}
