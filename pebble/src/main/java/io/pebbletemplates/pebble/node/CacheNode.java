/*
 * This file is part of Pebble.
 * <p>
 * Copyright (c) 2014 by Mitchell Bösecke
 * <p>
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package io.pebbletemplates.pebble.node;

import io.pebbletemplates.pebble.cache.CacheKey;
import io.pebbletemplates.pebble.cache.PebbleCache;
import io.pebbletemplates.pebble.error.PebbleException;
import io.pebbletemplates.pebble.extension.NodeVisitor;
import io.pebbletemplates.pebble.node.expression.Expression;
import io.pebbletemplates.pebble.template.EvaluationContextImpl;
import io.pebbletemplates.pebble.template.PebbleTemplateImpl;
import io.pebbletemplates.pebble.utils.LimitedSizeWriter;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.concurrent.CompletionException;

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
  public void render(PebbleTemplateImpl self, Writer writer,
                     EvaluationContextImpl context) throws IOException {
    try {
      final String body;
      PebbleCache<CacheKey, Object> tagCache = context.getTagCache();
      CacheKey key = new CacheKey(this, (String) this.name.evaluate(self, context),
          context.getLocale());
      body = (String) context.getTagCache().computeIfAbsent(key, k -> {
        try {
          return this.render(self, context);
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      });
      writer.write(body);
    } catch (CompletionException e) {
      throw new PebbleException(e, "Could not render cache block [" + this.name + "]");
    }
  }

  private String render(final PebbleTemplateImpl self, final EvaluationContextImpl context)
      throws IOException {
    Writer tempWriter = LimitedSizeWriter.from(new StringWriter(), context);
    CacheNode.this.body.render(self, tempWriter, context);

    return tempWriter.toString();
  }
}
