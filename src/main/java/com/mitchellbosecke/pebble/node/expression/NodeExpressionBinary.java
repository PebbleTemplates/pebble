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

import com.mitchellbosecke.pebble.node.NodeExpression;

public abstract class NodeExpressionBinary extends NodeExpressionOperator {

	protected NodeExpression left;
	protected NodeExpression right;
	
	public void setLeft(NodeExpression left){
		this.left = left;
	}
	
	public void setRight(NodeExpression right){
		this.right = right;
	}

}
