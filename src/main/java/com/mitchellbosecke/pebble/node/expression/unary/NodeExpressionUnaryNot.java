/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Original work Copyright (c) 2009-2013 by the Twig Team
 * Modified work Copyright (c) 2013 by Mitchell Bösecke
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.node.expression.unary;

import com.mitchellbosecke.pebble.compiler.Compiler;
import com.mitchellbosecke.pebble.node.expression.NodeExpressionUnary;

public class NodeExpressionUnaryNot extends NodeExpressionUnary {

	@Override
	public void compile(Compiler compiler) {
		compiler.raw("((Boolean)").subcompile(childExpression).raw(" == false)");
	}

}
