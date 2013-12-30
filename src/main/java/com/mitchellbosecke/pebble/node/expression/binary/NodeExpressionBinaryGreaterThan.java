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
import com.mitchellbosecke.pebble.node.expression.NodeExpressionBinarySimple;
import com.mitchellbosecke.pebble.utils.TreeWriter;

public class NodeExpressionBinaryGreaterThan extends NodeExpressionBinarySimple {

	@Override
	public void operator(Compiler compiler) {
		compiler.raw(">");
	}

	@Override
	public void tree(TreeWriter tree) {
		tree.write(">").subtree(leftExpression).subtree(rightExpression, true);
	}
}
