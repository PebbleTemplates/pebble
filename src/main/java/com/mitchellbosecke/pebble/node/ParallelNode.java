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
import java.io.StringWriter;
import java.io.Writer;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.extension.NodeVisitor;
import com.mitchellbosecke.pebble.template.EvaluationContext;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;
import com.mitchellbosecke.pebble.utils.FutureWriter;

public class ParallelNode extends AbstractRenderableNode {

	private final BodyNode body;

	public ParallelNode(int lineNumber, BodyNode body) {
		super(lineNumber);
		this.body = body;
	}

	@Override
	public void render(final PebbleTemplateImpl self, Writer writer, final EvaluationContext context)
			throws IOException, PebbleException {

		ExecutorService es = context.getExecutorService();

		if (es == null) {
			throw new PebbleException(null,
					"The parallel tag can not be used unless you provide an ExecutorService to the PebbleEngine.");
		}

		final Writer stringWriter = new StringWriter();
		Future<String> future = es.submit(new Callable<String>() {
			@Override
			public String call() throws PebbleException, IOException {
				body.render(self, stringWriter, context);
				return stringWriter.toString();
			}
		});
		((FutureWriter) writer).enqueue(future);
	}

	@Override
	public void accept(NodeVisitor visitor) {
		visitor.visit(this);
	}

	public BodyNode getBody() {
		return body;
	}
}
