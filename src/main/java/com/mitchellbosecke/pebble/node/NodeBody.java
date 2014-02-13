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

import java.util.ArrayList;
import java.util.List;

import com.mitchellbosecke.pebble.compiler.Compiler;

public class NodeBody extends AbstractNode {

	private final List<Node> children;

	public NodeBody(int lineNumber, List<Node> children) {
		super(lineNumber);
		this.children = children;
	}

	@Override
	public void compile(Compiler compiler) {
		compile(compiler, false);
	}

	/**
	 * When compiling a template that extends another template the NodeRoot will
	 * compile it's NodeBody with only a select list of Node types.
	 * 
	 * @param compiler
	 * @param whitelistNodes
	 */
	public void compile(Compiler compiler, boolean whitelistNodes) {
		for (Node child : children) {
			if (whitelistNodes) {
				if (!nodesAllowedInChildOutsideOfBlocks.contains(child.getClass())) {
					continue;
				}
			}
			child.compile(compiler);
		}
	}

	@Override
	public List<Node> getChildren() {
		List<Node> children = new ArrayList<>();
		children.addAll(this.children);
		return children;
	}

	private static List<Class<? extends Node>> nodesAllowedInChildOutsideOfBlocks = new ArrayList<>();

	static {
		nodesAllowedInChildOutsideOfBlocks.add(NodeSet.class);
		nodesAllowedInChildOutsideOfBlocks.add(NodeImport.class);
		nodesAllowedInChildOutsideOfBlocks.add(NodeMacro.class);
		nodesAllowedInChildOutsideOfBlocks.add(NodeBlock.class);
	}

}
