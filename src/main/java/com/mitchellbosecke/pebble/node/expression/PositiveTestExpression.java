/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Copyright (c) 2014 by Mitchell Bösecke
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.node.expression;

import java.util.Map;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.extension.LocaleAware;
import com.mitchellbosecke.pebble.extension.Test;
import com.mitchellbosecke.pebble.node.ArgumentsNode;
import com.mitchellbosecke.pebble.node.TestInvocationExpression;
import com.mitchellbosecke.pebble.template.EvaluationContext;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;

public class PositiveTestExpression extends BinaryExpression<Object> {

    private Test cachedTest;

    @Override
    public Object evaluate(PebbleTemplateImpl self, EvaluationContext context) throws PebbleException {

        TestInvocationExpression testInvocation = (TestInvocationExpression) getRightExpression();
        ArgumentsNode args = testInvocation.getArgs();

        if(cachedTest == null) {
            String testName = testInvocation.getTestName();

            Map<String, Test> tests = context.getTests();
            cachedTest = tests.get(testInvocation.getTestName());

            if (cachedTest == null) {
                throw new PebbleException(null, String.format("Test [%s] does not exist.", testName));
            }

            if (cachedTest instanceof LocaleAware) {
                ((LocaleAware) cachedTest).setLocale(context.getLocale());
            }
        }
        Test test = cachedTest;

        Map<String, Object> namedArguments = args.getArgumentMap(self, context, test);

        return test.apply(getLeftExpression().evaluate(self, context), namedArguments);
    }
}
