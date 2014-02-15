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
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.template.Evaluatable;
import com.mitchellbosecke.pebble.template.EvaluationContext;

public class NodeParallel extends AbstractNode {

	private final NodeBody body;

	public NodeParallel(int lineNumber, NodeBody body) {
		super(lineNumber);
		this.body = body;
	}

	@Override
	public void compile(Compiler compiler) {
		compiler.write("evaluateInParallel(writer, context, new ").raw(Evaluatable.class.getName()).raw("(){")
				.newline().indent();

		compiler.write("public void evaluate(java.io.Writer writer, ").raw(EvaluationContext.class.getName())
				.raw(" context) throws ").raw(PebbleException.class.getName()).raw(", java.io.IOException {").newline()
				.indent();

		getBody().compile(compiler);

		compiler.outdent().write("}").newline();

		compiler.outdent().write("});").newline();
	}

	@Override
	public void accept(NodeVisitor visitor) {
		visitor.visit(this);
	}

	public NodeBody getBody() {
		return body;
	}
}
