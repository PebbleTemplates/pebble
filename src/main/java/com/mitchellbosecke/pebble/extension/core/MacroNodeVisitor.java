package com.mitchellbosecke.pebble.extension.core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.mitchellbosecke.pebble.extension.AbstractNodeVisitor;
import com.mitchellbosecke.pebble.node.BodyNode;
import com.mitchellbosecke.pebble.node.MacroNode;
import com.mitchellbosecke.pebble.node.RenderableNode;
import com.mitchellbosecke.pebble.node.RootNode;

/**
 * This will go through the AST and move MacroNodes infront of their siblings so
 * that user's can put their macros at the bottom of their template and invoke
 * them near the top.
 * 
 * @author Mitchell
 * 
 */
public class MacroNodeVisitor extends AbstractNodeVisitor {

	@Override
	public void visit(RootNode node) {
		final Set<MacroNode> macros = new HashSet<>();
		final List<RenderableNode> nonMacroNodes = new ArrayList<>();

		BodyNode body = node.getBody();
		List<RenderableNode> bodyNodes = body.getChildren();

		for (RenderableNode bodyNode : bodyNodes) {
			if (bodyNode instanceof MacroNode) {
				macros.add((MacroNode) bodyNode);
			} else {
				nonMacroNodes.add(bodyNode);
			}
		}

		bodyNodes.clear();
		bodyNodes.addAll(macros);
		bodyNodes.addAll(nonMacroNodes);
	}
}
