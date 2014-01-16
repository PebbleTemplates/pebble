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

	private final String parentFileName;

	private final Map<String, NodeBlock> blocks;

	private final Map<String, List<NodeMacro>> macros;

	public NodeRoot(NodeBody body, String parentFileName, Map<String, NodeBlock> blocks,
			Map<String, List<NodeMacro>> macros, String filename) {
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
		compileClassHeader(compiler);
		compileBuildContentFunction(compiler);
		compileBlocks(compiler);
		compileMacros(compiler);
		compileClassFooter(compiler);
	}

	private void compileClassHeader(Compiler compiler) {
		String parentClass = compiler.getEngine().getTemplateAbstractClass().getName();

		compiler.write(String.format("package %s;", PebbleTemplate.COMPILED_PACKAGE_NAME))
				.raw("\n\n")
				.write("import java.util.Map;")
				.raw("\n")
				.write("import java.util.HashMap;")
				.raw("\n")
				.write("import ")
				.raw(Context.class.getName())
				.raw(";")
				.raw("\n")
				.raw("\n")
				.write(String.format("public class %s extends %s implements %s {", compiler.getEngine()
						.getTemplateClassName(filename), parentClass, compiler.getEngine().getTemplateInterfaceClass()
						.getName())).indent();
	}

	private void compileBuildContentFunction(Compiler compiler) {
		compiler.raw("\n\n")
				.write("public void buildContent(java.io.Writer writer, Context context) throws com.mitchellbosecke.pebble.error.PebbleException, java.io.IOException {")
				.raw("\n").indent();
		if(this.parentFileName != null){
			compiler.write("getParent().buildContent(writer, context);");
		}else{
			body.compile(compiler);
		}

		compiler.outdent().raw("\n").write("}");
	}

	private void compileClassFooter(Compiler compiler) {
		compiler.outdent().raw("\n\n").write("}");
	}

	private void compileBlocks(Compiler compiler) {
		compiler.raw("\n\n").write("public void initBlocks() {").raw("\n").indent();
		for (NodeBlock block : blocks.values()) {
			compiler.raw("\n").subcompile(block);
		}
		compiler.outdent().raw("\n").write("}");
	}
	
	private void compileMacros(Compiler compiler) {
		compiler.raw("\n\n").write("public void initMacros() {").raw("\n").indent();
		for (List<NodeMacro> overloadedMacros : macros.values()) {
			for (NodeMacro macro : overloadedMacros) {
				compiler.raw("\n\n").subcompile(macro);
			}
		}
		compiler.outdent().raw("\n").write("}");
	}

	public boolean hasParent() {
		return parentFileName != null;
	}

	public String getParentFileName() {
		return parentFileName;
	}
}
