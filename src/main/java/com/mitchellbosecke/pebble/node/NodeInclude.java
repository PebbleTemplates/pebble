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
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;

public class NodeInclude extends AbstractNode {

	private final NodeExpression includeExpression;

	public NodeInclude(int lineNumber, NodeExpression includeExpression) {
		super(lineNumber);
		this.includeExpression = includeExpression;
	}

	@Override
	public void compile(Compiler compiler) {

		compiler.newline();
		compiler.write("((").raw(PebbleTemplateImpl.class.getName()).raw(")engine.compile(")
				.subcompile(includeExpression).raw(")).evaluate(writer, context);");

		compiler.newline();
	}

	@Override
	public void accept(NodeVisitor visitor) {
		visitor.visit(this);
	}

}
