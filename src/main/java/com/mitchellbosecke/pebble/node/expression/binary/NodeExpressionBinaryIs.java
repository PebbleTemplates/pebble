/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Copyright (c) 2012 Mitchell Bosecke.
 * 
 * This work is licensed under the Creative Commons Attribution-ShareAlike 3.0 
 * Unported License. To view a copy of this license, visit 
 * http://creativecommons.org/licenses/by-sa/3.0/
 ******************************************************************************/
package com.mitchellbosecke.pebble.node.expression.binary;

import com.mitchellbosecke.pebble.compiler.Compiler;
import com.mitchellbosecke.pebble.node.NodeExpression;
import com.mitchellbosecke.pebble.node.expression.NodeExpressionArguments;
import com.mitchellbosecke.pebble.node.expression.NodeExpressionBinaryCallable;
import com.mitchellbosecke.pebble.node.expression.NodeExpressionConstant;
import com.mitchellbosecke.pebble.node.expression.NodeExpressionFunctionCall;
import com.mitchellbosecke.pebble.utils.TreeWriter;

public class NodeExpressionBinaryIs extends NodeExpressionBinaryCallable {

	@Override
	public void compile(Compiler compiler) {
		
		NodeExpressionConstant method;
		NodeExpressionArguments args = null;
		
		// right node can either be a function call or just a constant
		if(right instanceof NodeExpressionFunctionCall){
			
			method = ((NodeExpressionFunctionCall)right).getMethod();
			args = ((NodeExpressionFunctionCall)right).getArguments();
			
		}else {
			method = (NodeExpressionConstant)right;
		}

		compiler.raw("applyTest(").string(String.valueOf(method.getValue()));
		
		compiler.raw(",").subcompile(left);

		if (args != null) {
			for (NodeExpression arg : args.getArgs()) {
				compiler.raw(", ").subcompile(arg);
			}
		}

		compiler.raw(")");
	}

	@Override
	public void tree(TreeWriter tree) {
		tree.write("binary is").subtree(left).subtree(right, true);
		
	}

}
