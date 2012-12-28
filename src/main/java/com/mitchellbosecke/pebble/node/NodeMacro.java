/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Copyright (c) 2012 Mitchell Bosecke.
 * 
 * This work is licensed under the Creative Commons Attribution-ShareAlike 3.0 
 * Unported License. To view a copy of this license, visit 
 * http://creativecommons.org/licenses/by-sa/3.0/
 ******************************************************************************/
package com.mitchellbosecke.pebble.node;

import com.mitchellbosecke.pebble.compiler.Compiler;
import com.mitchellbosecke.pebble.node.expression.NodeExpressionArguments;
import com.mitchellbosecke.pebble.node.expression.NodeExpressionDeclaration;

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
		compiler.write(String.format("public String macro%s", name)).subcompile(args).raw("{\n\n").indent();

		// create a context that is local to this macro
		compiler.write("Map<String,Object> context = new HashMap<>();").raw("\n");

		// put args into context
		for (NodeExpression arg : args.getArgs()) {
			compiler.write("context.put(").string(((NodeExpressionDeclaration) arg).getName()).raw(",")
					.raw(((NodeExpressionDeclaration) arg).getName()).raw(");\n");
		}

		compiler.write("StringBuilder builder = new StringBuilder();").raw("\n");

		compiler.subcompile(body);

		compiler.write("return builder.toString();").raw("\n");

		compiler.raw("\n").outdent().write("}");

	}
}
