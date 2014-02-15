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
import com.mitchellbosecke.pebble.compiler.NodeVisitor;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.node.expression.NodeExpressionString;
import com.mitchellbosecke.pebble.template.EvaluationContext;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;

public class NodeRoot extends AbstractNode {

	private final String filename;

	private final NodeBody body;

	private final NodeExpression parentTemplateExpression;

	private final Map<String, NodeBlock> blocks;

	private final Map<String, NodeMacro> macros;

	public NodeRoot(NodeBody body, NodeExpression parentTemplateExpression, Map<String, NodeBlock> blocks,
			Map<String, NodeMacro> macros, String filename) {
		super(0);
		this.body = body;
		this.parentTemplateExpression = parentTemplateExpression;
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

	/**
	 * Builds the template constructor.
	 * 
	 * @param compiler
	 * @param className
	 */
	private void compileConstructor(Compiler compiler, String className) {
		compiler.newline(2).write("public ").raw(className).raw(" (String javaCode, ")
				.raw(PebbleEngine.class.getName()).raw(" engine) throws ").raw(PebbleException.class.getName())
				.raw("{ ").newline();

		compiler.indent().write("super(javaCode, engine);").newline();

		/*
		 * If parent expression was just a literal string we can compile it in
		 * the constructor which ensures that the parent template is compiled at
		 * the same time the child template is compiled. If it's not a string
		 * literal it will be compiled in the buildContent() method which occurs
		 * at runtime.
		 */
		if (getParentTemplateExpression() != null && getParentTemplateExpression() instanceof NodeExpressionString) {
			compiler.write("setParent(engine.compile(").subcompile(getParentTemplateExpression()).raw("));").newline();
		}

		// private method that registers all the blocks
		compiler.write("initBlocks();").newline();

		// private method that registers all the macros
		compiler.write("initMacros();").newline();
		compiler.outdent().write("}").newline(2);
	}

	/**
	 * Creates the buildContent() method which is responsible for the entire
	 * evaluation of the template.
	 * 
	 * @param compiler
	 */
	private void compileBuildContentFunction(Compiler compiler) {
		compiler.write("public void buildContent(java.io.Writer writer, ").raw(EvaluationContext.class.getName())
				.raw(" context) throws com.mitchellbosecke.pebble.error.PebbleException, java.io.IOException {")
				.newline().indent();

		if (this.getParentTemplateExpression() != null) {

			/*
			 * if parent expression was a string literal, the parent is compiled
			 * in the constructor, otherwise it's compiled here at runtime.
			 */
			if (!(this.getParentTemplateExpression() instanceof NodeExpressionString)) {
				compiler.write("setParent(engine.compile(").subcompile(getParentTemplateExpression()).raw("));").newline();
			}

			// parent can still be null if "extends" expression evaluated to
			// null
			compiler.write("if (getParent() != null ) {").newline().indent();
			getBody().compile(compiler, true);
			compiler.write("context.pushInheritanceChain(this);").newline();
			compiler.write("getParent().buildContent(writer, context);").newline().outdent();
			compiler.write("} else {").newline();
			compiler.subcompile(getBody()).newline();
			compiler.write("}").newline();

		} else {
			compiler.subcompile(getBody());
		}

		compiler.outdent().write("}").newline(2);
	}

	private void compileBlocks(Compiler compiler) {
		compiler.write("private void initBlocks() {").newline().indent();
		for (NodeBlock block : getBlocks().values()) {
			compiler.subcompile(block).newline();
		}
		compiler.outdent().newline().write("}").newline(2);
	}

	private void compileMacros(Compiler compiler) {
		compiler.write("private void initMacros() {").newline().indent();
		for (NodeMacro macro : getMacros().values()) {
			compiler.subcompile(macro).newline();
		}
		compiler.outdent().newline().write("}").newline(2);
	}

	private void compileClassFooter(Compiler compiler) {
		compiler.outdent().write("}");
	}

	public boolean hasParent() {
		return getParentTemplateExpression() != null;
	}

	public NodeExpression getParentTemplateExpression() {
		return parentTemplateExpression;
	}

	@Override
	public void accept(NodeVisitor visitor) {
		visitor.visit(this);
	}

	public NodeBody getBody() {
		return body;
	}

	public Map<String, NodeBlock> getBlocks() {
		return blocks;
	}

	public Map<String, NodeMacro> getMacros() {
		return macros;
	}
}
