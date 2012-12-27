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
				compiler.raw("\n").outdent().write("} else if (");
			} else {
				compiler.write("if (");
			}

			compiler.subcompile(ifStatement.getLeft()).raw(") {\n").indent().subcompile(ifStatement.getRight());

		}

		if (elseBody != null) {
			compiler.raw("\n").outdent().write("} else {\n").indent().subcompile(elseBody);
		}

		compiler.raw("\n").outdent().write("}\n");
	}
}
