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
import com.mitchellbosecke.pebble.utils.TreeWriter;

public class NodeMacro extends AbstractNode {

	public static final String MACRO_PREFIX = "macro_";

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
		
		/*
		 * Add a map as a secret argument
		 */
		NodeExpressionDeclaration mapDeclaration = new NodeExpressionDeclaration(args.getLineNumber(), "_context");
		args.addArgument(mapDeclaration);
		
		compiler.write(String.format("public String %s%s", MACRO_PREFIX, name)).subcompile(args).raw("{\n\n").indent();

		/*
		 * Each macro has it's own copy of the main context. We will add the
		 * macro arguments into this new context to give them scope and easy
		 * accessibility.
		 */
		compiler.write("Map<String,Object> context = new HashMap<>();").raw("\n");
		compiler.write("context.putAll((Map<String,Object>)_context);").raw("\n");

		// put args into scoped context
		for (NodeExpression arg : args.getArgs()) {
			compiler.write("context.put(").string(((NodeExpressionDeclaration) arg).getName()).raw(",")
					.raw(((NodeExpressionDeclaration) arg).getName()).raw(");\n");
		}

		compiler.write("StringBuilder builder = new StringBuilder();").raw("\n");

		compiler.subcompile(body);

		compiler.raw("\n").write("return builder.toString();").raw("\n");

		compiler.raw("\n").outdent().write("}");

	}

	@Override
	public void tree(TreeWriter tree) {
		tree.write(String.format("macro [%s]", name)).subtree(args).subtree(body);
	}
}
