/*
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package io.pebbletemplates.pebble.extension;

import io.pebbletemplates.pebble.error.PebbleException;
import io.pebbletemplates.pebble.template.EvaluationContext;
import io.pebbletemplates.pebble.template.PebbleTemplate;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Map;

public interface Filter extends NamedArguments {

  @Nullable
  Object apply(@Nullable Object input, @Nullable Map<@Nullable String, @Nullable Object> args, @NonNull PebbleTemplate self,
               @NonNull EvaluationContext context, int
                 lineNumber) throws PebbleException;
}
