/*******************************************************************************
 * This file is part of Pebble.
 * <p>
 * Copyright (c) 2014 by Mitchell Bösecke
 * <p>
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.node.expression;

import com.mitchellbosecke.pebble.error.AttributeNotFoundException;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.extension.Filter;
import com.mitchellbosecke.pebble.extension.core.DefaultFilter;
import com.mitchellbosecke.pebble.extension.escaper.EscapeFilter;
import com.mitchellbosecke.pebble.extension.escaper.SafeString;
import com.mitchellbosecke.pebble.node.ArgumentsNode;
import com.mitchellbosecke.pebble.template.EvaluationContext;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;

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
    public Object evaluate(PebbleTemplateImpl self, EvaluationContext context) throws PebbleException {

        FilterInvocationExpression filterInvocation = (FilterInvocationExpression) getRightExpression();
        ArgumentsNode args = filterInvocation.getArgs();
        String filterName = filterInvocation.getFilterName();

        if (this.filter == null) {
            this.filter = context.getExtensionRegistry().getFilter(filterInvocation.getFilterName());
        }

        if (filter == null) {
            throw new PebbleException(null, String.format("Filter [%s] does not exist.", filterName),
                    this.getLineNumber(), self.getName());
        }

        Map<String, Object> namedArguments = args.getArgumentMap(self, context, filter);

        // This check is not nice, because we use instanceof. However this is
        // the only filter which should not fail in strict mode, when the variable
        // is not set, because this method should exactly test this. Hence a
        // generic solution to allow other tests to reuse this feature make no sense
        Object input;
        if (filter instanceof DefaultFilter) {
            try {
                input = getLeftExpression().evaluate(self, context);
            } catch (AttributeNotFoundException ex) {
                input = null;
            }
        } else {
            input = getLeftExpression().evaluate(self, context);
        }

        if (input instanceof SafeString && !(filter instanceof EscapeFilter)) {
            input = input.toString();
        }

        return filter.apply(input, namedArguments);
    }
}
