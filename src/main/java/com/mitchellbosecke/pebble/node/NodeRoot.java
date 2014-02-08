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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.compiler.Compiler;
import com.mitchellbosecke.pebble.template.EvaluationContext;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;

public class NodeRoot extends AbstractNode {

	private final String filename;

	private final NodeBody body;

	private final String parentFileName;

	private final Map<String, NodeBlock> blocks;

	private final Map<String, NodeMacro> macros;

	public NodeRoot(NodeBody body, String parentFileName, Map<String, NodeBlock> blocks, Map<String, NodeMacro> macros,
			String filename) {
		super(0);
		this.body = body;
		this.parentFileName = parentFileName;
		this.blocks = blocks;
		this.macros = macros;
		this.filename = filename;
	}

	public String getFilename() {
		return filename;
	}

	@Override
	public void compile(Compiler compiler) {
		String className = compiler.getEngine().getTemplateClassName(filename);

		compileMetaInformationInComments(compiler);
		compileClassHeader(compiler, className);
		compileConstructor(compiler, className);
		compileBuildContentFunction(compiler);
		compileBlocks(compiler);
		compileMacros(compiler);
		compileClassFooter(compiler);
	}

	private void compileMetaInformationInComments(Compiler compiler) {
		compiler.write("/*").newline();
		compiler.write(" * Filename: ").raw(filename).newline();
		compiler.write(" * Parent filename: ").raw(parentFileName).newline();
		compiler.write(" * Compiled on: ").raw(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(new Date()))
				.newline();
		compiler.write(" */").newline();
	}

	private void compileClassHeader(Compiler compiler, String className) {
		String parentClass = PebbleTemplateImpl.class.getName();

		compiler.write(String.format("package %s;", PebbleTemplateImpl.COMPILED_PACKAGE_NAME)).newline(2)
				.write("import java.util.Map;").newline().write("import java.util.HashMap;").newline().write("import ")
				.raw(EvaluationContext.class.getName()).raw(";").newline(2)
				.write(String.format("public class %s extends %s {", className, parentClass)).indent();
	}

	private void compileConstructor(Compiler compiler, String className) {
		compiler.newline(2).write("public ").raw(className).raw(" (String javaCode, ")
				.raw(PebbleEngine.class.getName()).raw(" engine, ").raw(PebbleTemplateImpl.class.getName())
				.raw(" parent) {").newline();

		compiler.indent().write("super(javaCode, engine, parent);").newline();

		compiler.outdent().write("}").newline(2);
	}

	private void compileBuildContentFunction(Compiler compiler) {
		compiler.write("public void buildContent(java.io.Writer writer, ").raw(EvaluationContext.class.getName())
				.raw(" context) throws com.mitchellbosecke.pebble.error.PebbleException, java.io.IOException {")
				.newline().indent();
		if (this.parentFileName != null) {
			compiler.write("context.pushInheritanceChain(this);").newline();
			compiler.write("getParent().buildContent(writer, context);").newline();
		} else {
			body.compile(compiler);
		}

		compiler.outdent().write("}").newline(2);
	}

	private void compileBlocks(Compiler compiler) {
		compiler.write("public void initBlocks() {").newline().indent();
		for (NodeBlock block : blocks.values()) {
			compiler.subcompile(block).newline();
		}
		compiler.outdent().newline().write("}").newline(2);
	}

	private void compileMacros(Compiler compiler) {
		compiler.write("public void initMacros() {").newline().indent();
		for (NodeMacro macro : macros.values()) {
			compiler.subcompile(macro).newline();
		}
		compiler.outdent().newline().write("}").newline(2);
	}

	private void compileClassFooter(Compiler compiler) {
		compiler.outdent().write("}");
	}

	public boolean hasParent() {
		return parentFileName != null;
	}

	public String getParentFileName() {
		return parentFileName;
	}
}
