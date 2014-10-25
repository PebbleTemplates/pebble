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
import java.io.StringWriter;
import java.io.Writer;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.extension.NodeVisitor;
import com.mitchellbosecke.pebble.template.EvaluationContext;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;
import com.mitchellbosecke.pebble.utils.FutureWriter;

public class ParallelNode extends AbstractRenderableNode {

    private final Logger logger = LoggerFactory.getLogger(ParallelNode.class);

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
            logger.info(String.format(
                    "The parallel tag was used [%s:%d] but no ExecutorService was provided. The parallel tag will be ignored "
                            + "and it's contents will be rendered in sequence with the rest of the template.",
                    self.getName(), getLineNumber()));
            /*
             * If user did not provide an ExecutorService, we simply ignore the
             * parallel tag and render it's contents like we normally would.
             */
            body.render(self, writer, context);
            return;
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
