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
import java.util.concurrent.ExecutionException;

import com.google.common.cache.Cache;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.extension.NodeVisitor;
import com.mitchellbosecke.pebble.template.EvaluationContext;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;

public class CacheNode extends AbstractRenderableNode {

    private final BodyNode body;
    private final Cache<String, String> cache;
    private final String name;

    public CacheNode(int lineNumber, String name, BodyNode body, Cache<String, String> cache) {
        super(lineNumber);
        this.body = body;
        this.name = name;
        this.cache = cache;
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public void render(final PebbleTemplateImpl self, Writer writer, final EvaluationContext context)
            throws PebbleException,
            IOException {
        try {
            String key = this.name + context.getLocale();
            String body = this.cache.get(key, new Callable<String>() {
                @Override
                public String call() throws Exception {
                    StringWriter tempWriter = new StringWriter();
                    CacheNode.this.body.render(self, tempWriter, context);

                    return tempWriter.toString();
                }
            });
            writer.write(body);
        }
        catch (ExecutionException e) {
            throw new PebbleException(e, "Could not render cache block [" + this.name + "]");
        }
    }
}
