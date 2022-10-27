/*
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package io.pebbletemplates.node;

import io.pebbletemplates.template.EvaluationContextImpl;
import io.pebbletemplates.template.PebbleTemplateImpl;

import java.io.IOException;
import java.io.Writer;

public interface RenderableNode extends Node {

  void render(PebbleTemplateImpl self, Writer writer, EvaluationContextImpl context)
      throws IOException;
}
