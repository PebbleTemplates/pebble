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
import com.mitchellbosecke.pebble.node.expression.NodeExpressionDeclaration;
import com.mitchellbosecke.pebble.node.expression.NodeExpressionVariableName;
import com.mitchellbosecke.pebble.utils.TreeWriter;

public class NodeFor extends AbstractNode {

	private final NodeExpressionDeclaration iterationVariable;

	private final NodeExpressionVariableName iterable;

	private final NodeBody body;

	private final NodeBody elseBody;

	public NodeFor(int lineNumber, NodeExpressionDeclaration iterationVariable, NodeExpressionVariableName iterable,
			NodeBody body, NodeBody elseBody) {
		super(lineNumber);
		this.iterationVariable = iterationVariable;
		this.iterable = iterable;
		this.body = body;
		this.elseBody = elseBody;
	}

	@Override
	public void compile(Compiler compiler) {

		
		if(elseBody != null){
			compiler.raw("\n").write("if (((Iterable)").subcompile(iterable).raw(").iterator().hasNext()){\n").indent();
			
			compileForLoop(compiler);
	
			compiler.raw("\n").outdent().write("} else {\n").indent().subcompile(elseBody);

	
			compiler.raw("\n").outdent().write("}\n");
		}else{
			compileForLoop(compiler);
		}
	}

	private void compileForLoop(Compiler compiler) {
		compiler.raw("\n").write("for(").subcompile(iterationVariable).raw(" : (Iterable)").subcompile(iterable)
				.raw("){\n");

		compiler.indent().write("context.put(").string(iterationVariable.getName()).raw(",")
				.raw(iterationVariable.getName()).raw(");\n").subcompile(body);

		compiler.outdent().raw("\n").write("}\n");

		// remove iteration variable from context as it is no longer in the
		// scope once we leave the for loop.
		compiler.write("context.remove(").string(iterationVariable.getName()).raw(");").raw("\n");
	}

	@Override
	public void tree(TreeWriter tree) {
		tree.write("for").subtree(iterationVariable).subtree(iterable).subtree(body, true);
	}
}
