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

import java.util.ArrayList;
import java.util.List;

import com.mitchellbosecke.pebble.compiler.Compiler;
import com.mitchellbosecke.pebble.node.expression.NodeExpressionNewVariableName;
import com.mitchellbosecke.pebble.utils.ObjectUtils;

public class NodeFor extends AbstractNode {

	private final NodeExpressionNewVariableName iterationVariable;

	private final NodeExpression iterable;

	private final NodeBody body;

	private final NodeBody elseBody;

	public NodeFor(int lineNumber, NodeExpressionNewVariableName iterationVariable, NodeExpression iterable,
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
			compiler.newline().write("if (").subcompile(iterable).write(" != null && ((Iterable)(")
					.subcompile(iterable).raw(")).iterator().hasNext()){").newline().indent();

			compileForLoop(compiler);

			compiler.newline().outdent().write("} else {").newline().indent().subcompile(elseBody);

			compiler.newline().outdent().write("}").newline();
		} else {
			compiler.newline().write("if (").subcompile(iterable).raw(" != null){").newline().indent();

			compileForLoop(compiler);

			compiler.newline().outdent().write("}").newline();
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
		compiler.write("for( Object ").subcompile(iterationVariable).raw(" : (Iterable)(").subcompile(iterable)
				.raw(")){").newline().indent();

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

	@Override
	public List<Node> getChildren() {
		List<Node> children = new ArrayList<>();
		children.add(iterationVariable);
		children.add(iterable);
		children.add(body);
		children.add(elseBody);
		return children;
	}
}
