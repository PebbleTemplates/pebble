/*
 * This file is part of Pebble.
 * <p>
 * Copyright (c) 2014 by Mitchell Bösecke
 * <p>
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.mitchellbosecke.pebble.node.expression;

import com.mitchellbosecke.pebble.error.AttributeNotFoundException;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.extension.Test;
import com.mitchellbosecke.pebble.extension.core.DefinedTest;
import com.mitchellbosecke.pebble.node.ArgumentsNode;
import com.mitchellbosecke.pebble.node.TestInvocationExpression;
import com.mitchellbosecke.pebble.template.EvaluationContextImpl;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;
import java.util.Map;

public class PositiveTestExpression extends BinaryExpression<Object> {

  private Test cachedTest;

  @Override
  public Object evaluate(PebbleTemplateImpl self, EvaluationContextImpl context) {

    TestInvocationExpression testInvocation = (TestInvocationExpression) this.getRightExpression();
    ArgumentsNode args = testInvocation.getArgs();

    if (this.cachedTest == null) {
      String testName = testInvocation.getTestName();

      this.cachedTest = context.getExtensionRegistry().getTest(testInvocation.getTestName());

      if (this.cachedTest == null) {
        throw new PebbleException(null, String.format("Test [%s] does not exist.", testName),
            this.getLineNumber(), self.getName());
      }
    }
    Test test = this.cachedTest;

    Map<String, Object> namedArguments = args.getArgumentMap(self, context, test);

    // This check is not nice, because we use instanceof. However this is
    // the only test which should not fail in strict mode, when the variable
    // is not set, because this method should exactly test this. Hence a
    // generic solution to allow other tests to reuse this feature make no
    // sense.
    if (test instanceof DefinedTest) {
      Object input = null;
      try {
        input = this.getLeftExpression().evaluate(self, context);
      } catch (AttributeNotFoundException e) {
        input = null;
      }
      return test.apply(input, namedArguments, self, context, this.getLineNumber());
    } else {
      return test
          .apply(this.getLeftExpression().evaluate(self, context), namedArguments, self, context,
              this.getLineNumber());
    }

  }
}
