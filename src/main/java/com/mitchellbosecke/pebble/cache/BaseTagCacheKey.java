package com.mitchellbosecke.pebble.cache;

/**
 * Base class for the key of the tag cache
 *
 * @author Eric Bussieres
 */
public abstract class BaseTagCacheKey {
    private final String tagName;

    public BaseTagCacheKey(String tagName) {
        super();
        this.tagName = tagName;
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
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        BaseTagCacheKey other = (BaseTagCacheKey) obj;
        if (this.tagName == null) {
            if (other.tagName != null) {
                return false;
            }
        }
        else if (!this.tagName.equals(other.tagName)) {
            return false;
        }
        return true;
    }

    public String getTagName() {
        return this.tagName;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.tagName == null) ? 0 : this.tagName.hashCode());
        return result;
    }
}
