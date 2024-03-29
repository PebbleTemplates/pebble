/*
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package io.pebbletemplates.pebble.node.expression;

import io.pebbletemplates.pebble.extension.NodeVisitor;
import io.pebbletemplates.pebble.template.EvaluationContextImpl;
import io.pebbletemplates.pebble.template.PebbleTemplateImpl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class MapExpression implements Expression<Map<?, ?>> {

  // FIXME should keys be of any type?
  private final Map<Expression<?>, Expression<?>> entries;
  private final int lineNumber;

  public MapExpression(int lineNumber) {
    this.entries = Collections.emptyMap();
    this.lineNumber = lineNumber;
  }

  public MapExpression(Map<Expression<?>, Expression<?>> entries, int lineNumber) {
    if (entries == null) {
      this.entries = Collections.emptyMap();
    } else {
      this.entries = entries;
    }
    this.lineNumber = lineNumber;
  }

  @Override
  public void accept(NodeVisitor visitor) {
    visitor.visit(this);
  }

  @Override
  public Map<?, ?> evaluate(PebbleTemplateImpl self, EvaluationContextImpl context) {
    Map<Object, Object> returnEntries = new HashMap<>(
        Long.valueOf(Math.round(Math.ceil(this.entries.size() / 0.75)))
            .intValue());
    for (Entry<Expression<?>, Expression<?>> entry: this.entries.entrySet()) {
      Expression<?> keyExpr = entry.getKey();
      Expression<?> valueExpr = entry.getValue();
      Object key = keyExpr == null ? null : keyExpr.evaluate(self, context);
      Object value = valueExpr == null ? null : valueExpr.evaluate(self, context);
      returnEntries.put(key, value);
    }
    return returnEntries;
  }

  public Map<Expression<?>, Expression<?>> getEntries() {
    return this.entries;
  }

  @Override
  public int getLineNumber() {
    return this.lineNumber;
  }

}
