/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Original work Copyright (c) 2009-2013 by the Twig Team
 * Modified work Copyright (c) 2013 by Mitchell Bösecke
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.node.expression;

import com.mitchellbosecke.pebble.compiler.Compiler;
import com.mitchellbosecke.pebble.node.NodeExpression;

public abstract class NodeExpressionUnary extends NodeExpression {

	protected NodeExpression node;

	@Override
	public void compile(Compiler compiler) {
		compiler.raw("(");
		operator(compiler);
		compiler.subcompile(node).raw(")");
	}

	public abstract void operator(Compiler compiler);
	
	public void setNode(NodeExpression node){
		this.node = node;
	}

}
