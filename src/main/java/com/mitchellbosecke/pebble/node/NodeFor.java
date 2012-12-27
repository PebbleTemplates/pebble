package com.mitchellbosecke.pebble.node;

import com.mitchellbosecke.pebble.compiler.Compiler;
import com.mitchellbosecke.pebble.node.expression.NodeExpressionDeclaration;
import com.mitchellbosecke.pebble.node.expression.NodeExpressionName;

public class NodeFor extends AbstractNode {

	private NodeExpressionDeclaration iterationVariable;

	private NodeExpressionName iterable;

	private NodeBody body;

	public NodeFor(int lineNumber, NodeExpressionDeclaration iterationVariable, NodeExpressionName iterable,
			NodeBody body) {
		super(lineNumber);
		this.iterationVariable = iterationVariable;
		this.iterable = iterable;
		this.body = body;
	}

	@Override
	public void compile(Compiler compiler) {

		compiler.raw("\n").write("for(").subcompile(iterationVariable).raw(" : (Iterable)").subcompile(iterable).raw("){\n");

		compiler.indent().write("context.put(").string(iterationVariable.getName()).raw(",")
				.raw(iterationVariable.getName()).raw(");\n").subcompile(body);

		compiler.outdent().raw("\n").write("}\n");

		// remove iteration variable from context as it is no longer in the
		// scope once we leave the for loop.
		compiler.write("context.remove(").string(iterationVariable.getName()).raw(");").raw("\n");

	}
}
