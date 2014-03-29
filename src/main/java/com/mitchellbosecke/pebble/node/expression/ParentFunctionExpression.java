/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Copyright (c) 2014 by Mitchell Bösecke
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.node.expression;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.extension.NodeVisitor;
import com.mitchellbosecke.pebble.template.EvaluationContext;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;

public class ParentFunctionExpression implements Expression<String> {

	private final String blockName;

	public ParentFunctionExpression(String blockName) {
		this.blockName = blockName;
	}

	@Override
	public String evaluate(PebbleTemplateImpl self, EvaluationContext context) throws PebbleException {
		Writer writer = new StringWriter();
		try {
			context.getParentTemplate().block(writer, context, blockName, true);
		} catch (IOException e) {
			throw new PebbleException(e, "Could not render block [" + blockName + "]");
		}
		return writer.toString();
	}

	@Override
	public void accept(NodeVisitor visitor) {
		visitor.visit(this);
	}

}
