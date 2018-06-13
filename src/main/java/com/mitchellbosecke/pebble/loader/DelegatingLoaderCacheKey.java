package com.mitchellbosecke.pebble.loader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The delegating loader cache key is used as the cache key for {@link DelegatingLoader}.
 *
 * <p>
 * The object stores all cache keys of all loaders. Those keys together builds the key for the
 * delegating loader.
 *
 * @author Thomas Hunziker
 */
public final class DelegatingLoaderCacheKey {

  private final List<Object> delegatingCacheKeys;

  private final String templateName;

  private final int hashCode;

  DelegatingLoaderCacheKey(final List<Object> delegatingCacheKeys, final String templateName) {
    this.delegatingCacheKeys = Collections.unmodifiableList(new ArrayList<>(delegatingCacheKeys));
    this.templateName = templateName;
    this.hashCode = this.caclulateHashCode();
  }

  public String getTemplateName() {
    return templateName;
  }

  public List<Object> getDelegatingCacheKeys() {
    return delegatingCacheKeys;
  }

  private int caclulateHashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((delegatingCacheKeys == null) ? 0 : delegatingCacheKeys.hashCode());
    result = prime * result + ((templateName == null) ? 0 : templateName.hashCode());
    return result;
  }

  @Override
  public int hashCode() {
    return this.hashCode;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    DelegatingLoaderCacheKey other = (DelegatingLoaderCacheKey) obj;
    if (delegatingCacheKeys == null) {
      if (other.delegatingCacheKeys != null) {
        return false;
      }
    } else if (!delegatingCacheKeys.equals(other.delegatingCacheKeys)) {
      return false;
    }
    if (templateName == null) {
      return other.templateName == null;
    } else {
      return templateName.equals(other.templateName);
    }
  }

}
