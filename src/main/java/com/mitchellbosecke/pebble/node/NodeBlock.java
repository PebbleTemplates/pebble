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
import com.mitchellbosecke.pebble.template.Block;

public class NodeBlock extends AbstractNode {

	private NodeBody body;
	private String name;

	public NodeBlock(int lineNumber, String name) {
		this(lineNumber, name, null);
	}

	public NodeBlock(int lineNumber, String name, NodeBody body) {
		super(lineNumber);
		this.body = body;
		this.name = name;
	}

	public void setBody(NodeBody body) {
		this.body = body;
	}

	@Override
	public void compile(Compiler compiler) {
		compiler.write("this.registerBlock(new ").raw(Block.class.getName()).raw("(){").newline().indent();

		compileGetNameMethod(compiler);
		compileEvaluateMethod(compiler);
		compiler.outdent().write("});");
	}

	public void compileGetNameMethod(Compiler compiler) {
		compiler.write("public String getName() { return ").string(name).raw("; }").newline();
	}

	public void compileEvaluateMethod(Compiler compiler) {
		compiler.write(
				"public void evaluate(java.io.Writer writer, Context context) throws com.mitchellbosecke.pebble.error.PebbleException, java.io.IOException {")
				.newline().indent();
		compiler.subcompile(body);
		compiler.outdent().newline().write("}").newline();
	}

}
