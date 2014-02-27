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

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.extension.NodeVisitor;
import com.mitchellbosecke.pebble.template.EvaluationContext;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;

public class BodyNode extends AbstractRenderableNode {

	private final List<RenderableNode> children;

	public BodyNode(int lineNumber, List<RenderableNode> children) {
		super(lineNumber);
		this.children = children;
	}

	@Override
	public void render(PebbleTemplateImpl self, Writer writer, EvaluationContext context) throws PebbleException,
			IOException {
		for (RenderableNode child : children) {
			if (self.getParent() != null) {
				if (!nodesAllowedInChildOutsideOfBlocks.contains(child.getClass())) {
					continue;
				}
			}
			child.render(self, writer, context);
		}
	}

	@Override
	public void accept(NodeVisitor visitor) {
		visitor.visit(this);
	}

	public List<RenderableNode> getChildren() {
		return children;
	}

	private static List<Class<? extends Node>> nodesAllowedInChildOutsideOfBlocks = new ArrayList<>();

	static {
		nodesAllowedInChildOutsideOfBlocks.add(SetNode.class);
		nodesAllowedInChildOutsideOfBlocks.add(ImportNode.class);
		nodesAllowedInChildOutsideOfBlocks.add(MacroNode.class);
		nodesAllowedInChildOutsideOfBlocks.add(BlockNode.class);
	}

}
