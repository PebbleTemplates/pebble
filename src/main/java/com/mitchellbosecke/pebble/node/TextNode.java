/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Copyright (c) 2014 by Mitchell Bösecke
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.node;

import java.io.IOException;
import java.io.Writer;

import com.mitchellbosecke.pebble.extension.NodeVisitor;
import com.mitchellbosecke.pebble.template.EvaluationContext;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;

public class TextNode extends AbstractRenderableNode {

	private final String data;

	public TextNode(String data, int lineNumber) {
		super(lineNumber);
		this.data = data;
	}

	@Override
	public void render(PebbleTemplateImpl self, Writer writer, EvaluationContext context) throws IOException {
		writer.write(data);
	}

	@Override
	public void accept(NodeVisitor visitor) {
		visitor.visit(this);
	}

	public String getData() {
		return data;
	}

}
