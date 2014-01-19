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
			compiler.newline().write("if (((Iterable)").subcompile(iterable).raw(").iterator().hasNext()){").newline()
					.indent();

			compileForLoop(compiler);

			compiler.newline().outdent().write("} else {\n").indent().subcompile(elseBody);

			compiler.newline().outdent().write("}").newline();
		} else {
			compileForLoop(compiler);
		}
	}

	private void compileForLoop(Compiler compiler) {

		compiler.write("context.pushScope();").newline();
		compiler.write("context.put(\"loop\", new HashMap<>());").newline();

		// create the special "loop" variable
		compiler.write("((Map<String,Object>)context.get(\"loop\")).put(\"index\", 0);").newline();

		// iterate through loop first to calculate length
		compiler.write("((Map<String,Object>)context.get(\"loop\")).put(\"length\", ").raw(ObjectUtils.class.getName())
				.raw(".getIteratorSize((Iterable)").subcompile(iterable).raw("));").newline();

		// start the for loop
		compiler.write("for(").subcompile(iterationVariable).raw(" : (Iterable)").subcompile(iterable).raw("){")
				.newline().indent();

		compiler.write("context.put(").string(iterationVariable.getName()).raw(",").raw(iterationVariable.getName())
				.raw(");").newline().subcompile(body).newline();

		// increment the special loop.index variable
		compiler.write(
				"((Map<String,Object>)context.get(\"loop\")).put(\"index\", (int)((Map<String,Object>)context.get(\"loop\")).get(\"index\") + 1);")
				.newline();

		compiler.outdent().newline().write("}").newline();

		// remove context variables that are specific to this for loop
		compiler.write("context.popScope();").newline();
	}
}
