/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Original work Copyright (c) 2009-2013 by the Twig Team
 * Modified work Copyright (c) 2013 by Mitchell Bösecke
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.node;

import java.util.List;

import com.mitchellbosecke.pebble.compiler.Compiler;
import com.mitchellbosecke.pebble.utils.TreeWriter;

public class NodeBody extends AbstractNode {

	private final List<Node> children;

	public NodeBody(int lineNumber, List<Node> children) {
		super(lineNumber);
		this.children = children;
	}

	@Override
	public void compile(Compiler compiler) {
		for (Node child : children) {
			child.compile(compiler);
		}
	}

	@Override
	public void tree(TreeWriter tree) {
		tree.write("body");

		for (int i = 0; i < children.size(); ++i) {
			if (i == (children.size() - 1)) {
				tree.subtree(children.get(i), true);
			} else {
				tree.subtree(children.get(i));
			}
		}
	}
}
