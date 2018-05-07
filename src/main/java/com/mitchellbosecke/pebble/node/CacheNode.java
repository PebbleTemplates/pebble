/*******************************************************************************
 * This file is part of Pebble.
 * <p>
 * Copyright (c) 2014 by Mitchell Bösecke
 * <p>
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.node;

import com.mitchellbosecke.pebble.cache.CacheKey;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.extension.NodeVisitor;
import com.mitchellbosecke.pebble.node.expression.Expression;
import com.mitchellbosecke.pebble.template.EvaluationContext;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

/**
 * Node for the cache tag
 *
 * @author Eric Bussieres
 */
public class CacheNode extends AbstractRenderableNode {

    private final BodyNode body;

    private final Expression<?> name;

    public CacheNode(int lineNumber, Expression<?> name, BodyNode body) {
        super(lineNumber);
        this.body = body;
        this.name = name;
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public void render(final PebbleTemplateImpl self, Writer writer, final EvaluationContext context) throws IOException {
        try {
            CacheKey key = new CacheKey(this, (String) this.name.evaluate(self, context), context.getLocale());
            String body = (String) context.getTagCache().get(key, new Callable<Object>() {

                @Override
                public String call() throws Exception {
                    StringWriter tempWriter = new StringWriter();
                    CacheNode.this.body.render(self, tempWriter, context);

                    return tempWriter.toString();
                }
            });
            writer.write(body);
        } catch (ExecutionException e) {
            throw new PebbleException(e, "Could not render cache block [" + this.name + "]");
        }
    }
}
