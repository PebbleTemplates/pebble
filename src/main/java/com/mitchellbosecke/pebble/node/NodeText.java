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

import com.mitchellbosecke.pebble.compiler.Compiler;
import com.mitchellbosecke.pebble.extension.NodeVisitor;

public class NodeText extends AbstractNode {

	private final String data;

	public NodeText(String data, int lineNumber) {
		super(lineNumber);
		this.data = data;
	}

	@Override
	public void compile(Compiler compiler) {
		compiler.write("writer.write(").string(data).raw(");").newline();
	}

	@Override
	public void accept(NodeVisitor visitor) {
		visitor.visit(this);
	}

	public String getData() {
		return data;
	}

}
