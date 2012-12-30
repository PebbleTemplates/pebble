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

import java.util.Map;

import com.mitchellbosecke.pebble.compiler.Compiler;
import com.mitchellbosecke.pebble.utils.TreeWriter;

public class NodeRoot extends AbstractNode {

	private final String filename;

	private final NodeBody body;

	private final String parentClassName;

	private final Map<String, NodeBlock> blocks;

	private final Map<String, NodeMacro> macros;

	public NodeRoot(NodeBody body, String parent, Map<String, NodeBlock> blocks, Map<String, NodeMacro> macros,
			String filename) {
		super(0);
		this.body = body;
		this.parentClassName = parent;
		this.blocks = blocks;
		this.macros = macros;
		this.filename = filename;
	}

	public String getFilename() {
		return filename;
	}

	@Override
	public void compile(Compiler compiler) {
		compileClassHeader(compiler);
		if (this.parentClassName == null) {
			compileBuildContentFunction(compiler);
		}
		compileBlockMethods(compiler);
		compileMacroMethods(compiler);
		compileClassFooter(compiler);
	}

	private void compileClassHeader(Compiler compiler) {
		String parentClass = this.parentClassName == null ? compiler.getEngine().getTemplateAbstractClass().getName()
				: parentClassName;

		compiler.write("package com.mitchellbosecke.pebble.template;")
				.raw("\n\n")
				.write("import java.util.Map;")
				.raw("\n")
				.write("import java.util.HashMap;")
				.raw("\n")
				.raw("\n")
				.write(String.format("public class %s extends %s implements %s {", compiler.getEngine()
						.getTemplateClassName(filename), parentClass, compiler.getEngine().getTemplateInterfaceClass()
						.getName())).indent();
	}

	private void compileBuildContentFunction(Compiler compiler) {
		compiler.raw("\n\n").write("public void buildContent() {").raw("\n").indent();

		body.compile(compiler);

		compiler.outdent().raw("\n").write("}");
	}

	private void compileClassFooter(Compiler compiler) {
		compiler.outdent().raw("\n\n").write("}");
	}

	private void compileBlockMethods(Compiler compiler) {
		for (NodeBlock block : blocks.values()) {
			compiler.raw("\n\n").subcompile(block);
		}
	}

	private void compileMacroMethods(Compiler compiler) {
		for (NodeMacro macro : macros.values()) {
			compiler.raw("\n\n").subcompile(macro);
		}
	}
	
	public void tree(TreeWriter tree){
		tree.write("root").subtree(body, true);
	}
}
