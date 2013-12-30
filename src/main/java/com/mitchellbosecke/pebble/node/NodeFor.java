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
import com.mitchellbosecke.pebble.node.expression.NodeExpressionDeclaration;
import com.mitchellbosecke.pebble.node.expression.NodeExpressionVariableName;

public class NodeFor extends AbstractNode {

	private final NodeExpressionDeclaration iterationVariable;

	private final NodeExpressionVariableName iterable;

	private final NodeBody body;

	private final NodeBody elseBody;

	public NodeFor(int lineNumber, NodeExpressionDeclaration iterationVariable, NodeExpressionVariableName iterable,
			NodeBody body, NodeBody elseBody) {
		super(lineNumber);
		this.iterationVariable = iterationVariable;
		this.iterable = iterable;
		this.body = body;
		this.elseBody = elseBody;
	}

	@Override
	public void compile(Compiler compiler) {

		if (elseBody != null) {
			compiler.raw("\n").write("if (((Iterable)").subcompile(iterable).raw(").iterator().hasNext()){\n").indent();

			compileForLoop(compiler);

			compiler.raw("\n").outdent().write("} else {\n").indent().subcompile(elseBody);

			compiler.raw("\n").outdent().write("}\n");
		} else {
			compileForLoop(compiler);
		}
	}

	private void compileForLoop(Compiler compiler) {

		// create the special "loop" variable
		compiler.raw("\n").write("currentLoop = new HashMap<>();\n");
		compiler.write("currentLoop.put(\"index\", 0);\n");

		// iterate through loop first to calculate length
		compiler.write("currentLoopLength = 0;\n");
		compiler.write("currentLoopIterator = ((Iterable)").subcompile(iterable).raw(").iterator();\n");
		compiler.write("while(currentLoopIterator.hasNext()){\n");
		compiler.write("currentLoopIterator.next();\n");
		compiler.write("currentLoopLength++;\n");
		compiler.write("};\n");

		compiler.write("currentLoop.put(\"length\", currentLoopLength);\n");
		compiler.write("context.put(\"loop\", currentLoop);\n");

		// start the for loop
		compiler.write("for(").subcompile(iterationVariable).raw(" : (Iterable)").subcompile(iterable).raw("){\n")
				.indent();

		compiler.write("context.put(").string(iterationVariable.getName()).raw(",").raw(iterationVariable.getName())
				.raw(");\n").subcompile(body);

		// increment the special loop.index variable
		compiler.write("currentLoop.put(\"index\", (int)currentLoop.get(\"index\") + 1);\n");

		compiler.outdent().raw("\n").write("}\n");

		// remove context variables that are specific to this for loop
		compiler.write("context.remove(\"loop\");\n");
		compiler.write("context.remove(").string(iterationVariable.getName()).raw(");").raw("\n");
	}

}
