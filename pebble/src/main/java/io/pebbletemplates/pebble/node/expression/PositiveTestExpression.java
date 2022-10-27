/*
 * This file is part of Pebble.
 * <p>
 * Copyright (c) 2014 by Mitchell Bösecke
 * <p>
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package io.pebbletemplates.pebble.node.expression;

import io.pebbletemplates.pebble.error.AttributeNotFoundException;
import io.pebbletemplates.pebble.error.PebbleException;
import io.pebbletemplates.pebble.extension.Test;
import io.pebbletemplates.pebble.extension.core.DefinedTest;
import io.pebbletemplates.pebble.node.ArgumentsNode;
import io.pebbletemplates.pebble.node.TestInvocationExpression;
import io.pebbletemplates.pebble.template.EvaluationContextImpl;
import io.pebbletemplates.pebble.template.PebbleTemplateImpl;

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
