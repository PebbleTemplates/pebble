/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Copyright (c) 2014 by Mitchell Bösecke
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.node.expression;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.extension.Function;
import com.mitchellbosecke.pebble.extension.LocaleAware;
import com.mitchellbosecke.pebble.extension.NodeVisitor;
import com.mitchellbosecke.pebble.node.ArgumentsNode;
import com.mitchellbosecke.pebble.template.EvaluationContext;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;

public class FunctionOrMacroInvocationExpression implements Expression<Object> {

	private final String functionName;
	private final ArgumentsNode args;

	public FunctionOrMacroInvocationExpression(String functionName,
			ArgumentsNode arguments) {
		this.functionName = functionName;
		this.args = arguments;
	}

	@Override
	public Object evaluate(PebbleTemplateImpl self, EvaluationContext context)
			throws PebbleException {
		Map<String, Function> functions = context.getFunctions();
		if (functions.containsKey(functionName)) {
			return applyFunction(self, context, functions.get(functionName),
					args);
		}
		return self.macro(context, functionName, args, false);
	}

	private Object applyFunction(PebbleTemplateImpl self,
			EvaluationContext context, Function function, ArgumentsNode args)
			throws PebbleException {
		List<Object> arguments = new ArrayList<>();

		Collections.addAll(arguments, args);

		
		if (function instanceof LocaleAware){
			((LocaleAware) function).setLocale(context.getLocale());
		}
		
		Map<String, Object> namedArguments = args.getArgumentMap(self, context,
				function);
		return function.execute(namedArguments);
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

}
