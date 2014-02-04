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
import com.mitchellbosecke.pebble.node.expression.NodeExpressionArguments;
import com.mitchellbosecke.pebble.node.expression.NodeExpressionBinary;
import com.mitchellbosecke.pebble.node.expression.NodeExpressionFilterInvokation;

public class NodeExpressionBinaryFilter extends NodeExpressionBinary {

	@Override
	public void compile(Compiler compiler) {	
		
		NodeExpressionFilterInvokation filterInvokation = (NodeExpressionFilterInvokation) rightExpression;

		compiler.raw("applyFilter(").string(String.valueOf(filterInvokation.getFilterName().getValue())).raw(", context");

		compiler.raw(",").subcompile(leftExpression);

		NodeExpressionArguments args = filterInvokation.getArgs();
		if (args != null && !args.getArgs().isEmpty()) {
			compiler.raw(", ");
			compiler.subcompile(args);
		}

		compiler.raw(")");
	}

}
