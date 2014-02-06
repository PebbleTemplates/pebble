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

import java.util.List;

import com.mitchellbosecke.pebble.compiler.Compiler;
import com.mitchellbosecke.pebble.utils.Pair;

public class NodeIf extends AbstractNode {

	private final List<Pair<NodeExpression, NodeBody>> conditionsWithBodies;

	private NodeBody elseBody;

	public NodeIf(int lineNumber, List<Pair<NodeExpression, NodeBody>> conditionsWithBodies) {
		this(lineNumber, conditionsWithBodies, null);
	}

	public NodeIf(int lineNumber, List<Pair<NodeExpression, NodeBody>> conditionsWithBodies, NodeBody elseBody) {
		super(lineNumber);
		this.conditionsWithBodies = conditionsWithBodies;
		this.elseBody = elseBody;
	}

	@Override
	public void compile(Compiler compiler) {
		boolean isFirst = true;
		for (Pair<NodeExpression, NodeBody> ifStatement : conditionsWithBodies) {
			if (!isFirst) {
				compiler.newline().outdent().write("} else if (");
			} else {
				compiler.newline().write("if (");
			}

			compiler.raw("(Boolean) ").subcompile(ifStatement.getLeft()).raw(") {").
			        newline().indent().subcompile(ifStatement.getRight());

		}

		if (elseBody != null) {
			compiler.newline().outdent().write("} else {").newline().indent().subcompile(elseBody);
		}

		compiler.newline().outdent().write("}").newline();
	}

}
