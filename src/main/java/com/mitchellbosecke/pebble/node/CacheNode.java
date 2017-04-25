/*******************************************************************************
 * This file is part of Pebble.
 * <p>
 * Copyright (c) 2014 by Mitchell Bösecke
 * <p>
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.node;

import com.github.benmanes.caffeine.cache.Cache;
import com.mitchellbosecke.pebble.cache.BaseTagCacheKey;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.error.RuntimePebbleException;
import com.mitchellbosecke.pebble.extension.NodeVisitor;
import com.mitchellbosecke.pebble.node.expression.Expression;
import com.mitchellbosecke.pebble.template.EvaluationContextImpl;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;
import com.mitchellbosecke.pebble.tokenParser.CacheTokenParser;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Locale;
import java.util.concurrent.CompletionException;

import static java.util.Objects.isNull;

/**
 * Node for the cache tag
 *
 * @author Eric Bussieres
 */
public class CacheNode extends AbstractRenderableNode {

    /**
     * Key to be used in the cache
     *
     * @author Eric Bussieres
     */
    private class CacheKey extends BaseTagCacheKey {

        private final Locale locale;

        private final String name;

        public CacheKey(String name, Locale locale) {
            super(CacheTokenParser.TAG_NAME);
            this.name = name;
            this.locale = locale;
        }

        /**
         * {@inheritDoc}
         *
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!super.equals(obj)) {
                return false;
            }
            if (this.getClass() != obj.getClass()) {
                return false;
            }
            CacheKey other = (CacheKey) obj;
            if (!this.getOuterType().equals(other.getOuterType())) {
                return false;
            }
            if (this.locale == null) {
                if (other.locale != null) {
                    return false;
                }
            } else if (!this.locale.equals(other.locale)) {
                return false;
            }
            if (this.name == null) {
                if (other.name != null) {
                    return false;
                }
            } else if (!this.name.equals(other.name)) {
                return false;
            }
            return true;
        }

        /**
         * {@inheritDoc}
         *
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = super.hashCode();
            result = prime * result + this.getOuterType().hashCode();
            result = prime * result + ((this.locale == null) ? 0 : this.locale.hashCode());
            result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
            return result;
        }

        private CacheNode getOuterType() {
            return CacheNode.this;
        }
    }

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
    public void render(final PebbleTemplateImpl self, Writer writer, final EvaluationContextImpl context)
            throws PebbleException, IOException {
        try {
            final String body;
            Cache tagCache = context.getTagCache();
            if(isNull(tagCache)){ //No cache
                body = render(self, context);
            }
            else {
                CacheKey key = new CacheKey((String) this.name.evaluate(self, context), context.getLocale());
                body = (String) context.getTagCache().get(key, k -> {
                    try {
                        return render(self, context);
                    } catch (PebbleException e) {
                        throw new RuntimePebbleException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
            writer.write(body);
        } catch (CompletionException e) {
            throw new PebbleException(e, "Could not render cache block [" + this.name + "]");
        }
    }

    private String render(final PebbleTemplateImpl self, final EvaluationContextImpl context) throws PebbleException, IOException {
        StringWriter tempWriter = new StringWriter();
        CacheNode.this.body.render(self, tempWriter, context);

        return tempWriter.toString();
    }

}
