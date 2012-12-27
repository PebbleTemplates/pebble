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
		compiler.write(String.format("public void macro%s", name)).subcompile(args).raw("{\n\n").indent();

		// put args into context
		for(NodeExpressionDeclaration arg : args.getArgs()){
			compiler.write("context.put(").string(arg.getName()).raw(",").raw(arg.getName()).raw(");\n");
		}
		
		compiler.subcompile(body);
		
		
		// remove args from scope of context
		for(NodeExpressionDeclaration arg : args.getArgs()){
			compiler.write("context.remove(").string(arg.getName()).raw(");\n");
		}
		
		compiler.raw("\n").outdent().write("}");

	}
}
