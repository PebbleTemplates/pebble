/*******************************************************************************
 * This file is part of Pebble.
 * <p>
 * Copyright (c) 2014 by Mitchell Bösecke
 * <p>
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.node.expression;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.extension.Function;
import com.mitchellbosecke.pebble.extension.NodeVisitor;
import com.mitchellbosecke.pebble.node.ArgumentsNode;
import com.mitchellbosecke.pebble.template.EvaluationContext;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class  FunctionOrMacroInvocationExpression implements Expression<Object> {

    private final String functionName;

    private final ArgumentsNode args;

    private final int lineNumber;

    public FunctionOrMacroInvocationExpression(String functionName, ArgumentsNode arguments, int lineNumber) {
        this.functionName = functionName;
        this.args = arguments;
        this.lineNumber = lineNumber;
    }

    @Override
    public Object evaluate(PebbleTemplateImpl self, EvaluationContext context) throws PebbleException {
        Function function = context.getExtensionRegistry().getFunction(functionName);
        if (function != null) {
            return applyFunction(self, context, function, args);
        }
        return self.macro(context, functionName, args, false);
    }

    private Object applyFunction(PebbleTemplateImpl self, EvaluationContext context, Function function,
            ArgumentsNode args) throws PebbleException {
        List<Object> arguments = new ArrayList<>();

        Collections.addAll(arguments, args);

        Map<String, Object> namedArguments = args.getArgumentMap(self, context, function);
        return function.execute(namedArguments, self, this.getLineNumber());
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }

    public String getFunctionName() {
        return functionName;
    }

    public ArgumentsNode getArguments() {
        return args;
    }

    @Override
    public int getLineNumber() {
        return this.lineNumber;
    }

}
