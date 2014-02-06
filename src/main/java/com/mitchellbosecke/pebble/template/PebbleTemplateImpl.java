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
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.extension.Filter;
import com.mitchellbosecke.pebble.extension.Function;
import com.mitchellbosecke.pebble.extension.LocaleAware;
import com.mitchellbosecke.pebble.extension.NamedArguments;
import com.mitchellbosecke.pebble.extension.Test;
import com.mitchellbosecke.pebble.utils.FutureWriter;
import com.mitchellbosecke.pebble.utils.ReflectionUtils;

public abstract class PebbleTemplateImpl implements PebbleTemplate {

	public final static String COMPILED_PACKAGE_NAME = "com.mitchellbosecke.pebble.template.compiled";

	private final String generatedJavaCode;

	protected final PebbleEngine engine;

	private final PebbleTemplateImpl parent;

	private final List<PebbleTemplateImpl> importedTemplates = new ArrayList<>();
	private final Map<String, Block> blocks = new HashMap<>();
	private final Map<String, Macro> macros = new HashMap<>();

	public PebbleTemplateImpl(String generatedJavaCode, PebbleEngine engine, PebbleTemplateImpl parent) {
		this.generatedJavaCode = generatedJavaCode;
		this.engine = engine;
		this.parent = parent;
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
			throw new PebbleException(null,
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
		Context context = new Context(engine.isStrictVariables());
		context.putAll(engine.getGlobalVariables());
		context.setLocale(locale);
		return context;
	}

	protected Object getAttribute(Context context, Object object, String attribute) throws PebbleException {
		return ReflectionUtils.getAttribute(context, object, attribute);
	}

	public void registerMacro(Macro macro) {
		macros.put(macro.getName(), macro);
	}

	public boolean hasMacro(String macroName) {
		return macros.containsKey(macroName);
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

		PebbleTemplateImpl childTemplate = context.getChildTemplate();

		// check child
		if (!ignoreOverriden && childTemplate != null && childTemplate.hasBlock(blockName)) {
			context.popInheritanceChain();
			childTemplate.block(blockName, context, false, writer);
			context.pushInheritanceChain(childTemplate);

			// check this template
		} else if (blocks.containsKey(blockName)) {
			Block block = blocks.get(blockName);
			block.evaluate(writer, context);

			// delegate to parent
		} else {
			if (this.getParent() != null) {
				context.pushInheritanceChain(this);
				this.getParent().block(blockName, context, true, writer);
				context.popInheritanceChain();
			}
		}

	}

	protected Object applyFunctionOrMacro(String functionName, Context context, ArgumentMap args)
			throws PebbleException {
		Map<String, Function> functions = engine.getFunctions();
		if (functions.containsKey(functionName)) {
			return applyFunction(functions.get(functionName), context, args);
		}
		return macro(functionName, context, args);
	}

	private Object applyFunction(Function function, Context context, ArgumentMap args) throws PebbleException {
		List<Object> arguments = new ArrayList<>();

		Collections.addAll(arguments, args);

		if (function instanceof LocaleAware) {
			((LocaleAware) function).setLocale(context.getLocale());
		}

		Map<String, Object> namedArguments = getNamedArguments((NamedArguments) function, args);
		return function.execute(namedArguments);
	}

	public String macro(String macroName, Context context, ArgumentMap args) throws PebbleException {
		String result = null;
		boolean found = false;

		PebbleTemplateImpl childTemplate = context.getChildTemplate();

		// check child template first
		if (childTemplate != null && childTemplate.hasMacro(macroName)) {
			found = true;
			context.popInheritanceChain();
			result = childTemplate.macro(macroName, context, args);
			context.pushInheritanceChain(childTemplate);

			// check current template
		} else if (hasMacro(macroName)) {
			found = true;
			Macro macro = macros.get(macroName);

			Map<String, Object> namedArguments = getNamedArguments((NamedArguments) macro, args);
			result = macro.call(context, namedArguments);
		}

		// check imported templates
		if (!found) {
			for (PebbleTemplateImpl template : importedTemplates) {
				if (template.hasMacro(macroName)) {
					found = true;
					result = template.macro(macroName, context, args);
				}
			}
		}

		// delegate to parent template
		if (!found) {
			if (this.getParent() != null) {
				context.pushInheritanceChain(this);
				result = this.getParent().macro(macroName, context, args);
				context.popInheritanceChain();
			} else {
				throw new PebbleException(null, String.format("Function or Macro [%s] does not exist.", macroName));
			}
		}

		return result;
	}

	protected Object applyFilter(String filterName, Context context, Object input, ArgumentMap args)
			throws PebbleException {

		Map<String, Filter> filters = engine.getFilters();
		Filter filter = filters.get(filterName);

		if (filter == null) {
			throw new PebbleException(null, String.format("Filter [%s] does not exist.", filterName));
		}

		if (filter instanceof LocaleAware) {
			((LocaleAware) filter).setLocale(context.getLocale());
		}

		Map<String, Object> namedArguments = getNamedArguments((NamedArguments) filter, args);

		return filter.apply(input, namedArguments);
	}

	protected boolean applyTest(String testName, Object input, ArgumentMap args) throws PebbleException {
		Map<String, Test> tests = engine.getTests();
		Test test = tests.get(testName);

		Map<String, Object> namedArguments = getNamedArguments((NamedArguments) test, args);
		return test.apply(input, namedArguments);
	}

	private Map<String, Object> getNamedArguments(NamedArguments invokableWithNamedArguments, ArgumentMap arguments) throws PebbleException {
		Map<String, Object> namedArguments = new HashMap<>();
		List<String> argumentNames = invokableWithNamedArguments.getArgumentNames();
		
		if (argumentNames == null) {
			if (arguments.getPositionalArguments().isEmpty()) {
				return namedArguments;
			}

			/* Some functions such as min and max use un-named varags */
			else {
				for (int i = 0; i < arguments.getPositionalArguments().size(); i++) {
					namedArguments.put(String.valueOf(i), arguments.getPositionalArguments().get(i));
				}
			}
		} else {
			Iterator<String> nameIterator = argumentNames.iterator();

			for (Object value : arguments.getPositionalArguments()) {
				namedArguments.put(nameIterator.next(), value);
			}

			for(Map.Entry<String, Object> arg : arguments.getNamedArguments().entrySet()){
				// check if user used an incorrect name
				if(!argumentNames.contains(arg.getKey())){
					throw new PebbleException(null, "The following named argument does not exist: " + arg.getKey());
				}
				namedArguments.put(arg.getKey(), arg.getValue());
			}
		}

		return namedArguments;
	}

	protected String printVariable(Object var) {
		if (var == null) {
			return "";
		} else {
			return var.toString();
		}
	}

	public String getGeneratedJavaCode() {
		return generatedJavaCode;
	}

	public PebbleTemplateImpl getParent() {
		return parent;
	}

	protected void addImportedTemplate(PebbleTemplate template) {
		this.importedTemplates.add((PebbleTemplateImpl) template);
	}

	public abstract void initBlocks();

	public abstract void initMacros();
}
