/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Original work Copyright (c) 2009-2013 by the Twig Team
 * Modified work Copyright (c) 2013 by Mitchell Bösecke
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.template;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.extension.Filter;
import com.mitchellbosecke.pebble.extension.SimpleFunction;
import com.mitchellbosecke.pebble.extension.Test;
import com.mitchellbosecke.pebble.node.expression.NodeExpressionGetAttributeOrMethod;
import com.mitchellbosecke.pebble.utils.Context;
import com.mitchellbosecke.pebble.utils.FutureWriter;
import com.mitchellbosecke.pebble.utils.LocaleAware;
import com.mitchellbosecke.pebble.utils.ReflectionUtils;

public abstract class PebbleTemplate {

	public final static String COMPILED_PACKAGE_NAME = "com.mitchellbosecke.pebble.template.compiled";

	private final String generatedJavaCode;

	protected final PebbleEngine engine;

	private PebbleTemplate parent;
	private PebbleTemplate child;

	private final List<PebbleTemplate> importedTemplates = new ArrayList<>();
	private final Map<String, Block> blocks = new HashMap<>();
	private final Map<String, Map<Integer, Macro>> macros = new HashMap<>();

	public PebbleTemplate(String generatedJavaCode, PebbleEngine engine) {
		this.generatedJavaCode = generatedJavaCode;
		this.engine = engine;
	}

	public abstract void buildContent(Writer writer, Context context) throws IOException, PebbleException;

	public void evaluate(Writer writer) throws PebbleException, IOException {
		Context context = initContext(engine.getDefaultLocale());
		evaluate(writer, context);
	}

	public void evaluate(Writer writer, Locale locale) throws PebbleException, IOException {
		Context context = initContext(locale);
		evaluate(writer, context);
	}

	public void evaluate(Writer writer, Map<String, Object> map) throws PebbleException, IOException {
		Context context = initContext(engine.getDefaultLocale());
		context.putAll(map);
		evaluate(writer, context);
	}

	public void evaluate(Writer writer, Map<String, Object> map, Locale locale) throws PebbleException, IOException {
		Context context = initContext(locale);
		context.putAll(map);
		evaluate(writer, context);
	}

	private void evaluate(Writer writer, Context context) throws PebbleException, IOException {
		if (engine.getExecutorService() != null) {
			writer = new FutureWriter(writer, engine.getExecutorService());
		}
		buildContent(writer, context);
		writer.flush();
	}

	protected void evaluateInParallel(Writer writer, final Context context, final Evaluatable parallelEvaluation)
			throws PebbleException, IOException {
		ExecutorService es = engine.getExecutorService();

		if (es == null) {
			throw new PebbleException(
					"The parallel tag can not be used unless you provide an ExecutorService to the PebbleEngine.");
		}

		final Writer stringWriter = new StringWriter();
		Future<String> future = es.submit(new Callable<String>() {
			@Override
			public String call() throws PebbleException, IOException {
				parallelEvaluation.evaluate(stringWriter, context);
				return stringWriter.toString();
			}
		});
		((FutureWriter) writer).enqueue(future);
	}

	private Context initContext(Locale locale) {
		Context context = new Context(engine.isStrictVariables(), null);
		context.putAll(engine.getGlobalVariables());
		context.put("_locale", locale);
		return context;
	}

	protected Context pushContext(Context parentContext) {
		Context context = new Context(parentContext.isStrictVariables(), parentContext);
		return context;
	}

	protected Context popContext(Context currentContext) {
		return currentContext.getParent();
	}

	protected Object getAttribute(NodeExpressionGetAttributeOrMethod.Type type, Object object, String attribute)
			throws PebbleException {
		return getAttribute(type, object, attribute, new Object[0]);
	}

	protected Object getAttribute(NodeExpressionGetAttributeOrMethod.Type type, Object object, String attribute,
			Object... args) throws PebbleException {
		return ReflectionUtils.getAttribute(engine, type, object, attribute, args);
	}

	public void registerMacro(Macro macro) {
		Map<Integer, Macro> overloadedMacros = macros.get(macro.getName());

		if (overloadedMacros == null) {
			overloadedMacros = new HashMap<Integer, Macro>();
		}
		overloadedMacros.put(macro.getNumberOfArguments(), macro);
		macros.put(macro.getName(), overloadedMacros);
	}

	public boolean hasMacro(String macroName, int numOfArguments) {
		boolean result = false;
		Map<Integer, Macro> overloadedMacros = macros.get(macroName);
		if (overloadedMacros != null) {
			result = overloadedMacros.containsKey(numOfArguments);
		}
		return result;
	}

	public String macro(String macroName, Object... args) throws PebbleException {
		String result = null;
		boolean found = false;

		// check child template first
		if (this.getChild() != null && this.getChild().hasMacro(macroName, args.length)) {
			found = true;
			result = this.getChild().macro(macroName, args);

			// check current template
		} else if (hasMacro(macroName, args.length)) {
			found = true;
			Map<Integer, Macro> overloadedMacros = macros.get(macroName);
			Macro macro = overloadedMacros.get(args.length);

			macro.init();
			result = macro.call(args);
		}

		// check imported templates
		for (PebbleTemplate template : importedTemplates) {
			if (template.hasMacro(macroName, args.length)) {
				found = true;
				result = template.macro(macroName, args);
			}
		}

		// delegate to parent template
		if (!found) {
			if (this.getParent() != null) {
				result = this.getParent().macro(macroName, args);
			} else {
				throw new PebbleException(String.format("Function or Macro [%s] does not exist.", macroName));
			}
		}

		return result;
	}

	public void registerBlock(Block block) {
		blocks.put(block.getName(), block);
	}

	public boolean hasBlock(String blockName) {
		return blocks.containsKey(blockName);
	}

	public String block(String blockName, Context context, boolean ignoreOverriden) throws PebbleException, IOException {
		StringWriter writer = new StringWriter();
		block(blockName, context, ignoreOverriden, writer);
		return writer.toString();
	}

	public void block(String blockName, Context context, boolean ignoreOverriden, Writer writer)
			throws PebbleException, IOException {

		// check child
		if (!ignoreOverriden && this.getChild() != null && this.getChild().hasBlock(blockName)) {
			this.getChild().block(blockName, context, false, writer);

			// check this template
		} else if (blocks.containsKey(blockName)) {
			Block block = blocks.get(blockName);
			block.evaluate(writer, context);

			// delegate to parent
		} else {
			if (this.getParent() != null) {
				this.getParent().block(blockName, context, true, writer);
			}
		}

	}

	protected Object applyFilter(String filterName, Context context, Object... args) throws PebbleException {
		List<Object> arguments = new ArrayList<>();

		// extract input object
		Object input = args[0];

		// remove input from original args array
		args = Arrays.copyOfRange(args, 1, args.length);

		Collections.addAll(arguments, args);

		Map<String, Filter> filters = engine.getFilters();
		Filter filter = filters.get(filterName);

		if (filter instanceof LocaleAware) {
			((LocaleAware) filter).setLocale((Locale) context.get(Context.GLOBAL_VARIABLE_LOCALE));
		}

		if (filter == null) {
			throw new PebbleException(String.format("Filter [%s] does not exist.", filterName));
		}
		return filter.apply(input, arguments);
	}

	protected Object applyFunctionOrMacro(String functionName, Context context, Object... args) throws PebbleException {
		Map<String, SimpleFunction> functions = engine.getFunctions();
		if (functions.containsKey(functionName)) {
			return applyFunction(functions.get(functionName), context, args);
		}

		return macro(functionName, args);
	}

	private Object applyFunction(SimpleFunction function, Context context, Object... args) throws PebbleException {
		List<Object> arguments = new ArrayList<>();

		Collections.addAll(arguments, args);

		if (function instanceof LocaleAware) {
			((LocaleAware) function).setLocale((Locale) context.get(Context.GLOBAL_VARIABLE_LOCALE));
		}
		return function.execute(arguments);
	}

	protected String printVariable(Object var) {
		if (var == null) {
			return "";
		} else {
			return String.valueOf(var);
		}
	}

	protected boolean applyTest(String testName, Object... args) {
		ArrayList<Object> arguments = new ArrayList<>();
		Object input;

		// if args is null, it's because there was ONE argument and that
		// argument happens to be null
		if (args == null) {
			input = null;
		} else {
			input = args[0];

			// remove input from original args array
			args = Arrays.copyOfRange(args, 1, args.length);

			Collections.addAll(arguments, args);
		}

		Map<String, Test> tests = engine.getTests();
		Test test = tests.get(testName);
		return test.apply(input, arguments);
	}

	public String getGeneratedJavaCode() {
		return generatedJavaCode;
	}

	public PebbleTemplate getParent() {
		return parent;
	}

	public PebbleTemplate getChild() {
		return child;
	}

	protected void addImportedTemplate(PebbleTemplate template) {
		this.importedTemplates.add(template);
	}

	public abstract void initBlocks();

	public abstract void initMacros();

	public void setParent(PebbleTemplate parent) {
		this.parent = parent;
	}

	public void setChild(PebbleTemplate child) {
		this.child = child;
	}
}
