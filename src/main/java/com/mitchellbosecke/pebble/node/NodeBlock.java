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

public class NodeBlock extends AbstractNode {

	public static final String BLOCK_PREFIX = "block_";

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
		compileMainBlockMethod(compiler);
		compileBlockMethodReturningString(compiler);
	}

	/*
	 * Block methods only require the context just in case a child is accessing
	 * a parent block with the use of the parent() function, it's important the
	 * parent is using the correct context.
	 */

	private void compileMainBlockMethod(Compiler compiler) {
		compiler.write(
				String.format(
						"public void %s%s(PebbleWrappedWriter writer, Context context) throws com.mitchellbosecke.pebble.error.PebbleException {\n",
						BLOCK_PREFIX, this.name)).indent();

		compiler.subcompile(body);

		compiler.raw("\n").outdent().write("}\n");
	}

	private void compileBlockMethodReturningString(Compiler compiler) {
		compiler.write(
				String.format(
						"public String %s%s(Context context) throws com.mitchellbosecke.pebble.error.PebbleException {\n",
						BLOCK_PREFIX, this.name)).indent();

		compiler.write("java.io.StringWriter stringWriter = new java.io.StringWriter();\n");
		compiler.write("PebbleWrappedWriter writer = new PebbleWrappedWriter(stringWriter);\n");
		compiler.write(String.format("%s%s(writer, context);\n", BLOCK_PREFIX, this.name));
		compiler.write("return stringWriter.toString();\n");

		compiler.raw("\n").outdent().write("}\n");
	}

}
