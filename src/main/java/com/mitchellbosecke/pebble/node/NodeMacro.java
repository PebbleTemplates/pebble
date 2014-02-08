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
import com.mitchellbosecke.pebble.node.expression.NodeExpressionNamedArgument;
import com.mitchellbosecke.pebble.node.expression.NodeExpressionNamedArguments;
import com.mitchellbosecke.pebble.node.expression.NodeExpressionNewVariableName;
import com.mitchellbosecke.pebble.template.AbstractMacro;
import com.mitchellbosecke.pebble.template.EvaluationContext;

public class NodeMacro extends AbstractNode {

	private final NodeExpressionNamedArguments args;

	private final String name;

	private final NodeBody body;

	public NodeMacro(int lineNumber, String name, NodeExpressionNamedArguments args, NodeBody body) {
		super(lineNumber);
		this.name = name;
		this.args = args;
		this.body = body;
	}

	@Override
	public void compile(Compiler compiler) {
		compiler.write("this.registerMacro(new ").raw(AbstractMacro.class.getName()).raw("(){").newline().indent();

		compileGetNameMethod(compiler);
		compileGetArgumentNamesMethod(compiler);
		compileEvaluate(compiler);
		compiler.outdent().write("});");

	}

	public void compileGetNameMethod(Compiler compiler) {
		compiler.write("public String getName() { return ").string(name).raw("; }").newline();
	}

	public void compileGetArgumentNamesMethod(Compiler compiler) {
		compiler.write("public ").raw(List.class.getName()).raw("<String> getArgumentNames(){").indent().newline();

		compiler.write(List.class.getName()).raw("<String> result = new ").raw(ArrayList.class.getName()).raw("<>();")
				.newline();

		for (NodeExpressionNamedArgument arg : args.getArgs()) {
			NodeExpressionNewVariableName variableName = arg.getName();
			compiler.write("result.add(").string(variableName.getName()).raw(");").newline();
		}

		compiler.write("return result;").newline();
		compiler.outdent().write("}").newline();

	}

	public void compileEvaluate(Compiler compiler) {
		compiler.write("public void evaluate(java.io.Writer writer, ").raw(EvaluationContext.class.getName())
				.raw(" context) throws com.mitchellbosecke.pebble.error.PebbleException, java.io.IOException {")
				.indent();
		compiler.subcompile(body);
		compiler.outdent().newline().write("}").newline();
	}
}
