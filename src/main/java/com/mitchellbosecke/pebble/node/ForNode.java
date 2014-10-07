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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.extension.NodeVisitor;
import com.mitchellbosecke.pebble.node.expression.Expression;
import com.mitchellbosecke.pebble.template.EvaluationContext;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;

public class ForNode extends AbstractRenderableNode {

	private final String variableName;

	private final Expression<?> iterableExpression;

	private final BodyNode body;

	private final BodyNode elseBody;

	public ForNode(int lineNumber, String variableName, Expression<?> iterableExpression, BodyNode body,
			BodyNode elseBody) {
		super(lineNumber);
		this.variableName = variableName;
		this.iterableExpression = iterableExpression;
		this.body = body;
		this.elseBody = elseBody;
	}

	@Override
	public void render(PebbleTemplateImpl self, Writer writer, EvaluationContext context) throws PebbleException,
			IOException {
		Object iterableEvaluation = iterableExpression.evaluate(self, context);
		Iterable<?> iterable = null;

		if (iterableEvaluation == null) {
			return;
		}

		// if it's a primitive array we must convert to an iterable ArrayList
		if (iterableEvaluation.getClass().isArray()) {
			iterableEvaluation = Arrays.asList((Object[]) iterableEvaluation);
		}
		iterable = (Iterable<?>) iterableEvaluation;

		Iterator<?> iterator = iterable.iterator();

		context.pushScope();
		Map<String, Object> loop = new HashMap<>();
		int length = getIteratorSize(iterable);
		int index = 0;
		loop.put("index", index);
		loop.put("length", length);
		context.put("loop", loop);

		if (iterable != null && iterator.hasNext()) {

			while (iterator.hasNext()) {
				context.put(variableName, iterator.next());
				body.render(self, writer, context);
				loop.put("index", ++index);
			}

		} else if (elseBody != null) {
			elseBody.render(self, writer, context);
		}

		context.popScope();
	}

	private int getIteratorSize(Iterable<?> iterable) {
		if (iterable == null) {
			return 0;
		}
		Iterator<?> it = iterable.iterator();
		int size = 0;
		while (it.hasNext()) {
			size++;
			it.next();
		}
		return size;
	}

	@Override
	public void accept(NodeVisitor visitor) {
		visitor.visit(this);
	}

	public String getIterationVariable() {
		return variableName;
	}

	public Expression<?> getIterable() {
		return iterableExpression;
	}

	public BodyNode getBody() {
		return body;
	}

	public BodyNode getElseBody() {
		return elseBody;
	}
}
