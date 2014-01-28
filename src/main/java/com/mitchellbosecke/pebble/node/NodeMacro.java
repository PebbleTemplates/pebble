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
import com.mitchellbosecke.pebble.node.expression.NodeExpressionArguments;
import com.mitchellbosecke.pebble.node.expression.NodeExpressionDeclaration;
import com.mitchellbosecke.pebble.template.AbstractMacro;

public class NodeMacro extends AbstractNode {

	private final NodeExpressionArguments args;

	private final String name;

	private final NodeBody body;

	public NodeMacro(int lineNumber, String name, NodeExpressionArguments args, NodeBody body) {
		super(lineNumber);
		this.name = name;
		this.args = args;
		this.body = body;
	}

	@Override
	public void compile(Compiler compiler) {
		compiler.write("this.registerMacro(new ").raw(AbstractMacro.class.getName()).raw("(){").newline().indent();

		compileGetNameMethod(compiler);
		compileInit(compiler);
		compileEvaluate(compiler);
		compiler.outdent().write("});");

	}

	public void compileGetNameMethod(Compiler compiler) {
		compiler.write("public String getName() { return ").string(name).raw("; }").newline();
	}

	public void compileInit(Compiler compiler) {
		compiler.write("public void init(){").indent();

		for (NodeExpression arg : args.getArgs()) {
			NodeExpressionDeclaration variableDeclaration = (NodeExpressionDeclaration) arg;
			compiler.write("argNames.add(").string(variableDeclaration.getName()).raw(");").newline();
		}

		compiler.outdent().write("}").newline();

	}

	public void compileEvaluate(Compiler compiler) {
		compiler.write(
				"public void evaluate(java.io.Writer writer, Context context) throws com.mitchellbosecke.pebble.error.PebbleException, java.io.IOException {")
				.indent();
		compiler.subcompile(body);
		compiler.outdent().newline().write("}").newline();
	}
}
