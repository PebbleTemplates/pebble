package com.mitchellbosecke.pebble.attributes;

import com.google.common.base.Optional;
import com.mitchellbosecke.pebble.error.PebbleException;

public interface AttributeResolver {
    Optional<ResolvedAttribute> resolve(final Object instance, final Object attribute, final Object[] argumentValues, final boolean isStrictVariables, final String filename, final int lineNumber) throws PebbleException;
}
