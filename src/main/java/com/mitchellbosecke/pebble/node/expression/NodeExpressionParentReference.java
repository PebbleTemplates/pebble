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
import com.mitchellbosecke.pebble.node.DisplayableNode;
import com.mitchellbosecke.pebble.node.NodeBlock;
import com.mitchellbosecke.pebble.node.NodeExpression;

public class NodeExpressionParentReference extends NodeExpression implements DisplayableNode {

	private final String blockName;
	private final String parentClassName;

	public NodeExpressionParentReference(int lineNumber, String parentClassName, String blockName) {
		super(lineNumber);
		this.blockName = blockName;
		this.parentClassName = parentClassName;
	}

	/*
	 * 
	 * 
	 * Every block has has two methods, one that writes to a Writer and one that
	 * returns a String. It's important that the parent() function returns a
	 * string as it's part of an expression. Unfortunately we can't call
	 * super.block_blockname() which is the version that returns a String
	 * because THAT version actually calls the block_blockname(Writer writer)
	 * version which will end up being the overridden by the child class.
	 * Therefore we must instantiate an instance of the parent class which has
	 * no overridden block methods.
	 * 
	 * So instead of: 
	 * 	String result = super.block_blockName();
	 * 
	 * We have: 
	 * 	String result = (new ParentClass()).block_blockName();
	 * 
	 * Which is definitely a bit of a shame.
	 * 
	 */

	@Override
	public void compile(Compiler compiler) {
		compiler.raw(String.format("(new %s()).%s%s(context)", parentClassName, NodeBlock.BLOCK_PREFIX, this.blockName));
	}

}
