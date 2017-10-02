package com.mitchellbosecke.pebble.cache;

import com.mitchellbosecke.pebble.node.CacheNode;

import java.util.Locale;

/**
 * Key to be used in the cache
 *
 * @author Eric Bussieres
 */
public final class CacheKey {
    private final CacheNode node;
    private final String name;
    private final Locale locale;

    public CacheKey(CacheNode node, String name, Locale locale) {
        this.node = node;
        this.name = name;
        this.locale = locale;
    }

    /**
     * {@inheritDoc}
     *
     * @see Object#equals(Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        CacheKey other = (CacheKey) obj;
        if (!this.node.equals(other.node)) {
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
     * @see Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = this.node.hashCode();
        result = prime * result + ((this.locale == null) ? 0 : this.locale.hashCode());
        result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
        return result;
    }
}
