/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Copyright (c) 2014 by Mitchell Bösecke
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.node;

import com.mitchellbosecke.pebble.extension.NodeVisitor;
import com.mitchellbosecke.pebble.template.EvaluationContext;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;
import com.mitchellbosecke.pebble.utils.FutureWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class ParallelNode extends AbstractRenderableNode {

    private final Logger logger = LoggerFactory.getLogger(ParallelNode.class);

    private final BodyNode body;

    /**
     * If the user is using the parallel tag but doesn't provide an
     * ExecutorService we will warn them that this tag will essentially be
     * ignored but it's important that we only warn them once because this tag
     * may show up in a loop.
     */
    private boolean hasWarnedAboutNonExistingExecutorService = false;

    public ParallelNode(int lineNumber, BodyNode body) {
        super(lineNumber);
        this.body = body;
    }

    @Override
    public void render(final PebbleTemplateImpl self, Writer writer, final EvaluationContext context) throws IOException {

        ExecutorService es = context.getExecutorService();

        if (es == null) {

            if (!hasWarnedAboutNonExistingExecutorService) {
                logger.info(String.format(
                        "The parallel tag was used [%s:%d] but no ExecutorService was provided. The parallel tag will be ignored "
                                + "and it's contents will be rendered in sequence with the rest of the template.",
                        self.getName(), getLineNumber()));
                hasWarnedAboutNonExistingExecutorService = true;
            }

            /*
             * If user did not provide an ExecutorService, we simply ignore the
             * parallel tag and render it's contents like we normally would.
             */
            body.render(self, writer, context);
            
        } else {

            final EvaluationContext contextCopy = context.threadSafeCopy(self);

            final StringWriter newStringWriter = new StringWriter();
            final Writer newFutureWriter = new FutureWriter(newStringWriter);

            Future<String> future = es.submit(new Callable<String>() {

                @Override
                public String call() throws IOException {
                    body.render(self, newFutureWriter, contextCopy);
                    newFutureWriter.flush();
                    newFutureWriter.close();
                    return newStringWriter.toString();
                }
            });
            ((FutureWriter) writer).enqueue(future);
        }
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }

    public BodyNode getBody() {
        return body;
    }
}
