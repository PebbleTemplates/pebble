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

	/**
	 * Store a reference to this to help with debugging.
	 */
	private final String generatedJavaCode;

	/**
	 * A template has to store a reference to the main engine so that it can
	 * compile other templates when using the "import" or "include" tags. It's
	 * important that the only method of the PebbleEngine that a template
	 * invokes during evaluation is the "getTemplate" method because this is the
	 * only one that I'm sure is thread-safe.
	 */
	protected final PebbleEngine engine;

	/**
	 * The parent template which will be used to look up blocks and macros.
	 * 
	 * It will be set at compile time if it declared using a string literal
	 * otherwise if it's an expression it will be resolved at runtime.
	 */
	private PebbleTemplateImpl parent;

	/**
	 * The imported templates are used to look up macros.
	 */
	private final List<PebbleTemplateImpl> importedTemplates = new ArrayList<>();

	/**
	 * Blocks defined inside this template.
	 */
	private final Map<String, Block> blocks = new HashMap<>();

	/**
	 * Macros defined inside this template.
	 */
	private final Map<String, Macro> macros = new HashMap<>();

	public PebbleTemplateImpl(String generatedJavaCode, PebbleEngine engine) throws PebbleException {
		this.generatedJavaCode = generatedJavaCode;
		this.engine = engine;
	}

	public abstract void buildContent(Writer writer, EvaluationContext context) throws IOException, PebbleException;

	public void evaluate(Writer writer) throws PebbleException, IOException {
		EvaluationContext context = initContext(null);
		evaluate(writer, context);
	}

	public void evaluate(Writer writer, Locale locale) throws PebbleException, IOException {
		EvaluationContext context = initContext(locale);
		evaluate(writer, context);
	}

	public void evaluate(Writer writer, Map<String, Object> map) throws PebbleException, IOException {
		EvaluationContext context = initContext(null);
		context.putAll(map);
		evaluate(writer, context);
	}

	public void evaluate(Writer writer, Map<String, Object> map, Locale locale) throws PebbleException, IOException {
		EvaluationContext context = initContext(locale);
		context.putAll(map);
		evaluate(writer, context);
	}

	/**
	 * This is the authoritative evaluate method. It should not be invoked by
	 * the end user and is therefore not included in the PebbleTemplate
	 * interface. I can't, however, make it "private" due to the fact that
	 * NodeInclude will call this method on a template other than itself.
	 * 
	 * 
	 * @param writer
	 * @param context
	 * @throws PebbleException
	 * @throws IOException
	 */
	public void evaluate(Writer writer, EvaluationContext context) throws PebbleException, IOException {
		if (context.getExecutorService() != null) {
			writer = new FutureWriter(writer, context.getExecutorService());
		}
		buildContent(writer, context);
		writer.flush();
	}

	/**
	 * The parallel tag will utilize this method to evaluate a section of the
	 * template in a new thread.
	 */
	protected void evaluateInParallel(Writer writer, final EvaluationContext context,
			final Evaluatable parallelEvaluation) throws PebbleException, IOException {
		ExecutorService es = context.getExecutorService();

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

	/**
	 * Initializes the evaluation context with settings from the engine.
	 * 
	 * @param locale
	 * @return
	 */
	private EvaluationContext initContext(Locale locale) {
		locale = locale == null ? engine.getDefaultLocale() : locale;
		EvaluationContext context = new EvaluationContext(engine.isStrictVariables(), locale, engine.getFilters(),
				engine.getTests(), engine.getFunctions(), engine.getExecutorService());
		context.putAll(engine.getGlobalVariables());
		return context;
	}

	/**
	 * Gets an attribute of a variable. Implementation is found in
	 * Reflectionutils.
	 * 
	 * @param context
	 * @param object
	 * @param attribute
	 * @return
	 * @throws PebbleException
	 */
	protected Object getAttribute(EvaluationContext context, Object object, String attribute) throws PebbleException {
		return ReflectionUtils.getAttribute(context, object, attribute);
	}

	/**
	 * Imports a template.
	 * 
	 * @param template
	 */
	protected void addImportedTemplate(PebbleTemplate template) {
		this.importedTemplates.add((PebbleTemplateImpl) template);
	}

	/**
	 * Registers a macro.
	 * 
	 * @param macro
	 */
	protected void registerMacro(Macro macro) {
		macros.put(macro.getName(), macro);
	}

	public boolean hasMacro(String macroName) {
		return macros.containsKey(macroName);
	}

	/**
	 * Registers a block.
	 * 
	 * @param block
	 */
	protected void registerBlock(Block block) {
		blocks.put(block.getName(), block);
	}

	public boolean hasBlock(String blockName) {
		return blocks.containsKey(blockName);
	}

	/**
	 * Evaluates a block using a local writer and returning a string. This is
	 * only invoked using the block function. It returns a string because it's
	 * output might be further modified (ex. with the use of filters) before
	 * it's supposed to be written to the regular user-provided writer.
	 * 
	 * @param blockName
	 * @param context
	 * @param ignoreOverriden
	 * @return
	 * @throws PebbleException
	 * @throws IOException
	 */
	public String block(String blockName, EvaluationContext context, boolean ignoreOverriden) throws PebbleException,
			IOException {
		StringWriter writer = new StringWriter();
		block(blockName, context, ignoreOverriden, writer);
		return writer.toString();
	}

	/**
	 * A typical block declaration will use this method which evaluates the
	 * block using the regular user-provided writer.
	 * 
	 * @param blockName
	 * @param context
	 * @param ignoreOverriden
	 * @param writer
	 * @throws PebbleException
	 * @throws IOException
	 */
	public void block(String blockName, EvaluationContext context, boolean ignoreOverriden, Writer writer)
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

	/**
	 * At runtime we do not know if a user is invoking a function or a macro
	 * because the syntax is exactly the same.
	 * 
	 * @param functionName
	 * @param context
	 * @param args
	 * @return
	 * @throws PebbleException
	 */
	protected Object applyFunctionOrMacro(String functionName, EvaluationContext context, ArgumentMap args)
			throws PebbleException {
		Map<String, Function> functions = context.getFunctions();
		if (functions.containsKey(functionName)) {
			return applyFunction(functions.get(functionName), context, args);
		}
		return macro(functionName, context, args);
	}

	private Object applyFunction(Function function, EvaluationContext context, ArgumentMap args) throws PebbleException {
		List<Object> arguments = new ArrayList<>();

		Collections.addAll(arguments, args);

		if (function instanceof LocaleAware) {
			((LocaleAware) function).setLocale(context.getLocale());
		}

		Map<String, Object> namedArguments = getNamedArguments((NamedArguments) function, args);
		return function.execute(namedArguments);
	}

	public String macro(String macroName, EvaluationContext context, ArgumentMap args) throws PebbleException {
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

	/**
	 * Applies a filter.
	 * 
	 * @param filterName
	 * @param context
	 * @param input
	 * @param args
	 * @return
	 * @throws PebbleException
	 */
	protected Object applyFilter(String filterName, EvaluationContext context, Object input, ArgumentMap args)
			throws PebbleException {

		Map<String, Filter> filters = context.getFilters();
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

	/**
	 * Applies a test.
	 * 
	 * @param testName
	 * @param input
	 * @param args
	 * @return
	 * @throws PebbleException
	 */
	protected boolean applyTest(String testName, Object input, EvaluationContext context, ArgumentMap args)
			throws PebbleException {
		Map<String, Test> tests = context.getTests();
		Test test = tests.get(testName);

		Map<String, Object> namedArguments = getNamedArguments((NamedArguments) test, args);
		return test.apply(input, namedArguments);
	}

	/**
	 * Using hints from the filter/function/test/macro it will convert an
	 * ArgumentMap (which holds both positional and named arguments) into a
	 * regular Map that the filter/function/test/macro is expecting.
	 * 
	 * @param invokableWithNamedArguments
	 * @param arguments
	 * @return
	 * @throws PebbleException
	 */
	private Map<String, Object> getNamedArguments(NamedArguments invokableWithNamedArguments, ArgumentMap arguments)
			throws PebbleException {
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

			for (Map.Entry<String, Object> arg : arguments.getNamedArguments().entrySet()) {
				// check if user used an incorrect name
				if (!argumentNames.contains(arg.getKey())) {
					throw new PebbleException(null, "The following named argument does not exist: " + arg.getKey());
				}
				namedArguments.put(arg.getKey(), arg.getValue());
			}
		}

		return namedArguments;
	}

	/**
	 * Prints a variable.
	 * 
	 * @param var
	 * @return
	 */
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

	public void setParent(PebbleTemplate parent) {
		this.parent = (PebbleTemplateImpl) parent;
	}

}
