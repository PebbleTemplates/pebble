/*
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.mitchellbosecke.pebble.node.expression;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.template.EvaluationContextImpl;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;

import static com.mitchellbosecke.pebble.utils.TypeUtils.compatibleCast;

public class UnaryNotExpression extends UnaryExpression {

  @Override
  public Boolean evaluate(PebbleTemplateImpl self, EvaluationContextImpl context) {
    Object result = this.getChildExpression().evaluate(self, context);
    if (result != null) {
      if (result instanceof Number
              || result instanceof String
              || result instanceof Boolean) {
        return !compatibleCast(result, Boolean.class);
      } else {
        throw new PebbleException(
                null,
                String.format(
                        "Unsupported value type %s. Expected Boolean, String, Number in \"if\" statement",
                        result.getClass().getSimpleName()),
                this.getLineNumber(),
                self.getName());
      }
    } else { // input is null
      if (context.isStrictVariables()) {
        throw new PebbleException(null,
                  "null value given to not() and strict variables is set to true", this.getLineNumber(),
                  self.getName());
      } else {
        return true;
      }
    }
  }

}
