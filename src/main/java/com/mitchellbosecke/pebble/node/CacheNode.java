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
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import com.google.common.cache.Cache;
import com.mitchellbosecke.pebble.cache.BaseTagCacheKey;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.extension.NodeVisitor;
import com.mitchellbosecke.pebble.template.EvaluationContext;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;
import com.mitchellbosecke.pebble.tokenParser.CacheTokenParser;

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
            }
            else if (!this.locale.equals(other.locale)) {
                return false;
            }
            if (this.name == null) {
                if (other.name != null) {
                    return false;
                }
            }
            else if (!this.name.equals(other.name)) {
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
    private final Cache<BaseTagCacheKey, Object> cache;
    private final String name;

    public CacheNode(int lineNumber, String name, BodyNode body, Cache<BaseTagCacheKey, Object> cache) {
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
            CacheKey key = new CacheKey(this.name, context.getLocale());
            String body = (String) this.cache.get(key, new Callable<Object>() {
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
