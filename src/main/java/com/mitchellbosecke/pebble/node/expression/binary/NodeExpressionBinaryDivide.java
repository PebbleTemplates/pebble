/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Original work Copyright (c) 2009-2013 by the Twig Team
 * Modified work Copyright (c) 2013 by Mitchell Bösecke
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.node.expression.binary;

import com.mitchellbosecke.pebble.compiler.Compiler;
import com.mitchellbosecke.pebble.node.expression.NodeExpressionBinary;
import com.mitchellbosecke.pebble.utils.MathUtils;

public class NodeExpressionBinaryDivide extends NodeExpressionBinary {

	@Override
	public void compile(Compiler compiler) {
		compiler.raw(MathUtils.class.getName()).raw(".divide(").subcompile(leftExpression, false).raw(",")
		.subcompile(rightExpression, false).raw(")");
	}
}
