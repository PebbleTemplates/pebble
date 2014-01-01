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

import java.util.List;
import java.util.Map;

import com.mitchellbosecke.pebble.compiler.Compiler;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import com.mitchellbosecke.pebble.utils.Context;

public class NodeRoot extends AbstractNode {

	private final String filename;

	private final NodeBody body;

	private final String parentClassName;

	private final String parentFileName;

	private final Map<String, NodeBlock> blocks;

	private final Map<String, List<NodeMacro>> macros;

	public NodeRoot(NodeBody body, String parentClassName, String parentFileName, Map<String, NodeBlock> blocks,
			Map<String, List<NodeMacro>> macros, String filename) {
		super(0);
		this.body = body;
		this.parentClassName = parentClassName;
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

		compiler.write(String.format("package %s;", PebbleTemplate.COMPILED_PACKAGE_NAME))
				.raw("\n\n")
				.write("import java.util.Map;")
				.raw("\n")
				.write("import java.util.HashMap;")
				.raw("\n")
				.write("import ").raw(Context.class.getName()).raw(";")
				.raw("\n")
				.raw("\n")
				.write(String.format("public class %s extends %s implements %s {", compiler.getEngine()
						.getTemplateClassName(filename), parentClass, compiler.getEngine().getTemplateInterfaceClass()
						.getName())).indent();
	}

	private void compileBuildContentFunction(Compiler compiler) {
		compiler.raw("\n\n").write("public void buildContent() throws com.mitchellbosecke.pebble.error.PebbleException {").raw("\n").indent();

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
		for (List<NodeMacro> overloadedMacros : macros.values()) {
			for(NodeMacro macro: overloadedMacros){
				compiler.raw("\n\n").subcompile(macro);
			}
		}
	}

	public boolean hasParent() {
		return parentClassName != null;
	}

	public String getParentFileName() {
		return parentFileName;
	}
}
