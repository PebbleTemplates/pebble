/*
 * This file is part of Pebble.
 * <p>
 * Copyright (c) 2014 by Mitchell Bösecke
 * <p>
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package io.pebbletemplates.node.expression;

import io.pebbletemplates.extension.Filter;
import io.pebbletemplates.extension.core.DefaultFilter;
import io.pebbletemplates.error.AttributeNotFoundException;
import io.pebbletemplates.error.PebbleException;
import io.pebbletemplates.extension.escaper.EscapeFilter;
import io.pebbletemplates.extension.escaper.SafeString;
import io.pebbletemplates.node.ArgumentsNode;
import io.pebbletemplates.template.EvaluationContextImpl;
import io.pebbletemplates.template.PebbleTemplateImpl;

import java.util.Map;

public class FilterExpression extends BinaryExpression<Object> {

  /**
   * Save the filter instance on the first evaluation.
   */
  private Filter filter = null;

  public FilterExpression() {
    super();

  }

  @Override
  public Object evaluate(PebbleTemplateImpl self, EvaluationContextImpl context) {

    FilterInvocationExpression filterInvocation = (FilterInvocationExpression) this
        .getRightExpression();
    ArgumentsNode args = filterInvocation.getArgs();
    String filterName = filterInvocation.getFilterName();

    if (this.filter == null) {
      this.filter = context.getExtensionRegistry().getFilter(filterInvocation.getFilterName());
    }

    if (this.filter == null) {
      throw new PebbleException(null, String.format("Filter [%s] does not exist.", filterName),
          this.getLineNumber(), self.getName());
    }

    Map<String, Object> namedArguments = args.getArgumentMap(self, context, this.filter);

    // This check is not nice, because we use instanceof. However this is
    // the only filter which should not fail in strict mode, when the variable
    // is not set, because this method should exactly test this. Hence a
    // generic solution to allow other tests to reuse this feature make no sense
    Object input;
    if (this.filter instanceof DefaultFilter) {
      try {
        input = this.getLeftExpression().evaluate(self, context);
      } catch (AttributeNotFoundException ex) {
        input = null;
      }
    } else {
      input = this.getLeftExpression().evaluate(self, context);
    }

    if (input instanceof SafeString && !(this.filter instanceof EscapeFilter)) {
      input = input.toString();
    }

    return this.filter.apply(input, namedArguments, self, context, this.getLineNumber());
  }
}
