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
import com.mitchellbosecke.pebble.utils.ObjectUtils;

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

		compiler.write("context = pushContext(context);\n");
		compiler.write("context.put(\"loop\", new HashMap<>());\n");

		// create the special "loop" variable
		compiler.write("((Map<String,Object>)context.get(\"loop\")).put(\"index\", 0);\n");

		// iterate through loop first to calculate length
		compiler.write("((Map<String,Object>)context.get(\"loop\")).put(\"length\", ").raw(ObjectUtils.class.getName())
				.raw(".getIteratorSize((Iterable)").subcompile(iterable).raw("));\n");

		// start the for loop
		compiler.write("for(").subcompile(iterationVariable).raw(" : (Iterable)").subcompile(iterable).raw("){\n")
				.indent();

		compiler.write("context.put(").string(iterationVariable.getName()).raw(",").raw(iterationVariable.getName())
				.raw(");\n").subcompile(body).raw("\n");

		// increment the special loop.index variable
		compiler.write("((Map<String,Object>)context.get(\"loop\")).put(\"index\", (int)((Map<String,Object>)context.get(\"loop\")).get(\"index\") + 1);\n");

		compiler.outdent().raw("\n").write("}\n");

		// remove context variables that are specific to this for loop
		compiler.write("context = popContext(context);\n");
	}
}
