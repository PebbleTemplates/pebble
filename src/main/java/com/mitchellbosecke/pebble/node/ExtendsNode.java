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

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.extension.NodeVisitor;
import com.mitchellbosecke.pebble.node.expression.Expression;
import com.mitchellbosecke.pebble.template.EvaluationContext;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;

public class ExtendsNode extends AbstractRenderableNode {

	Expression<?> parentExpression;

	public ExtendsNode(int lineNumber, Expression<?> parentExpression) {
		super(lineNumber);
		this.parentExpression = parentExpression;
	}

	@Override
	public void render(final PebbleTemplateImpl self, Writer writer, final EvaluationContext context)
			throws IOException, PebbleException {
		self.setParent(context, (String) parentExpression.evaluate(self, context));
	}

	@Override
	public void accept(NodeVisitor visitor) {
		visitor.visit(this);
	}
}
