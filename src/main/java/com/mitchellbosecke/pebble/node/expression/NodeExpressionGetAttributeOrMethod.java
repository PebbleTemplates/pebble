/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Copyright (c) 2012 Mitchell Bosecke.
 * 
 * This work is licensed under the Creative Commons Attribution-ShareAlike 3.0 
 * Unported License. To view a copy of this license, visit 
 * http://creativecommons.org/licenses/by-sa/3.0/
 ******************************************************************************/
package com.mitchellbosecke.pebble.node.expression;

import com.mitchellbosecke.pebble.compiler.Compiler;
import com.mitchellbosecke.pebble.node.NodeExpression;
import com.mitchellbosecke.pebble.utils.TreeWriter;

/**
 * This node can be either getting a field from a variable or calling a method
 * of that variable. Ex:
 * 
 * var.field var.getField() var.executeMethod() var.executeMethod(arg1, arg2)
 * 
 * @author Mitchell
 * 
 */
public class NodeExpressionGetAttributeOrMethod extends NodeExpression {

	public static enum Type {
		ANY, METHOD
	};

	private final Type type;
	private final NodeExpression node;
	private final NodeExpressionConstant attributeOrMethod;
	private final NodeExpressionArguments args;

	public NodeExpressionGetAttributeOrMethod(int lineNumber, Type type, NodeExpression node,
			NodeExpressionConstant attribute) {
		this(lineNumber, type, node, attribute, null);
	}

	public NodeExpressionGetAttributeOrMethod(int lineNumber, Type type, NodeExpression node,
			NodeExpressionConstant attribute, NodeExpressionArguments args) {
		super(lineNumber);
		this.node = node;
		this.attributeOrMethod = attribute;
		this.type = type;
		this.args = args;
	}

	@Override
	public void compile(Compiler compiler) {
		compiler.raw("getAttribute(").raw(NodeExpressionGetAttributeOrMethod.Type.class.getCanonicalName()).raw(".")
				.raw(type.toString()).raw(",").subcompile(node).raw(",\"").subcompile(attributeOrMethod).raw("\"");

		if (args != null) {
			for (NodeExpression arg : args.getArgs()) {
				compiler.raw(",").subcompile(arg);
			}
		}
		compiler.raw(") ");
	}

	public void tree(TreeWriter tree) {
		tree.write(String.format("get attribute or method [%s]", type.toString())).subtree(node)
				.subtree(attributeOrMethod).subtree(args, true);
	}

}
