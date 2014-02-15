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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mitchellbosecke.pebble.compiler.Compiler;
import com.mitchellbosecke.pebble.extension.NodeVisitor;
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
		compileGetDefaultArgumentValuesMethod(compiler);
		compiler.outdent().write("});");

	}

	public void compileGetNameMethod(Compiler compiler) {
		compiler.write("public String getName() { return ").string(name).raw("; }").newline();
	}

	public void compileGetArgumentNamesMethod(Compiler compiler) {

		// @formatter:off
		/*
		 * GENERATES:
		 * 
		 * public List<String> getArgumentNames(){
		 * 		List<String> result = new Arraylist<>();
		 * 		result.add("argName1");
		 * 		result.add("argName2");
		 * 		...
		 * 		return result;
		 * }
		 */
		// @formatter:on
		compiler.write("public ").raw(List.class.getName()).raw("<String> getArgumentNames(){").indent().newline();
		compiler.write(List.class.getName()).raw("<String> result = new ").raw(ArrayList.class.getName()).raw("<>();")
				.newline();

		for (NodeExpressionNamedArgument arg : getArgs().getArgs()) {
			NodeExpressionNewVariableName variableName = arg.getName();
			compiler.write("result.add(").string(variableName.getName()).raw(");").newline();

		}

		compiler.write("return result;").newline();
		compiler.outdent().write("}").newline();

	}

	public void compileGetDefaultArgumentValuesMethod(Compiler compiler) {

		// @formatter:off
		/*
		 * GENERATES:
		 * 
		 * protected Map<String, Object> getDefaultArgumentValues(){
		 * 		Map<String,Object> result = new HashMap<>();
		 * 		result.put("argName1", argValue1);
		 * 		result.put("argName2", argValue2);
		 * 		...
		 * 		return result;
		 * }
		 */
		// @formatter:on
		compiler.write("protected ").raw(Map.class.getName()).raw("<String, Object> getDefaultArgumentValues(){")
				.indent().newline();
		compiler.write(Map.class.getName()).raw("<String, Object> result = new ").raw(HashMap.class.getName())
				.raw("<>();").newline();

		for (NodeExpressionNamedArgument arg : getArgs().getArgs()) {
			if (arg.getValue() != null) {
				NodeExpressionNewVariableName variableName = arg.getName();
				compiler.write("result.put(").string(variableName.getName()).raw(",").subcompile(arg.getValue())
						.raw(");").newline();
			}
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

	@Override
	public void accept(NodeVisitor visitor) {
		visitor.visit(this);
	}

	public NodeExpressionNamedArguments getArgs() {
		return args;
	}
}
