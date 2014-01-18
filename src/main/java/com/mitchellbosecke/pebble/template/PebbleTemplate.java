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

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.extension.Filter;
import com.mitchellbosecke.pebble.extension.SimpleFunction;
import com.mitchellbosecke.pebble.extension.Test;
import com.mitchellbosecke.pebble.node.expression.NodeExpressionGetAttributeOrMethod;
import com.mitchellbosecke.pebble.utils.Context;
import com.mitchellbosecke.pebble.utils.ReflectionUtils;
import com.mitchellbosecke.pebble.utils.TemplateAware;

public abstract class PebbleTemplate {

	public final static String COMPILED_PACKAGE_NAME = "com.mitchellbosecke.pebble.template.compiled";

	private String generatedJavaCode;
	private String source;

	protected Writer writer;
	protected Context context;
	protected PebbleEngine engine;

	private PebbleTemplate parent;
	private PebbleTemplate child;
	private final List<PebbleTemplate> importedTemplates = new ArrayList<>();

	private final Map<String, Block> blocks = new HashMap<>();
	private final Map<String, Map<Integer, Macro>> macros = new HashMap<>();

	private Locale locale;

	public abstract void buildContent(Writer writer, Context context) throws IOException, PebbleException;

	public void evaluate(Writer writer) throws PebbleException, IOException {
		Context context = initContext();
		evaluate(writer, context);
	}

	public void evaluate(Writer writer, Map<String, Object> map) throws PebbleException, IOException {
		Context context = initContext();
		context.putAll(map);
		evaluate(writer, context);
	}

	private void evaluate(Writer writer, Context context) throws PebbleException, IOException {
		this.writer = writer;
		this.context = context;

		buildContent(writer, context);

		writer.flush();
	}

	private Context initContext() {
		Context context = new Context(engine.isStrictVariables());
		context.putAll(engine.getGlobalVariables());
		return context;
	}

	protected void pushContext() {
		Context context = new Context(this.context.isStrictVariables());
		context.setParent(this.context);
		this.context = context;
	}

	protected void popContext() {
		this.context = this.context.getParent();
	}

	public void setEngine(PebbleEngine engine) {
		this.engine = engine;
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
		if (this.child != null && this.child.hasMacro(macroName, args.length)) {
			found = true;
			result = this.child.macro(macroName, args);

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
			if (this.parent != null) {
				result = this.parent.macro(macroName, args);
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
		if (!ignoreOverriden && this.child != null && this.child.hasBlock(blockName)) {
			this.child.block(blockName, context, false, writer);
		} else if (blocks.containsKey(blockName)) {
			Block block = blocks.get(blockName);
			block.evaluate(writer, context);
		} else {
			if (this.parent != null) {
				this.parent.block(blockName, context, true, writer);
			}
		}

	}

	protected Object applyFilter(String filterName, Object... args) throws PebbleException {
		List<Object> arguments = new ArrayList<>();

		// extract input object
		Object input = args[0];

		// remove input from original args array
		args = Arrays.copyOfRange(args, 1, args.length);

		Collections.addAll(arguments, args);

		Map<String, Filter> filters = engine.getFilters();
		Filter filter = filters.get(filterName);

		if (filter instanceof TemplateAware) {
			((TemplateAware) filter).setTemplate(this);
		}

		if (filter == null) {
			throw new PebbleException(String.format("Filter [%s] does not exist.", filterName));
		}
		return filter.apply(input, arguments);
	}

	protected Object applyFunctionOrMacro(String functionName, Object... args) throws PebbleException {
		Map<String, SimpleFunction> functions = engine.getFunctions();
		if (functions.containsKey(functionName)) {
			return applyFunction(functions.get(functionName), args);
		}

		return macro(functionName, args);
	}

	private Object applyFunction(SimpleFunction function, Object... args) throws PebbleException {
		List<Object> arguments = new ArrayList<>();

		Collections.addAll(arguments, args);

		if (function instanceof TemplateAware) {
			((TemplateAware) function).setTemplate(this);
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

	public void setGeneratedJavaCode(String generatedJavaCode) {
		this.generatedJavaCode = generatedJavaCode;
	}

	public String getGeneratedJavaCode() {
		return generatedJavaCode;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getSource() {
		return this.source;
	}

	public Locale getLocale() {
		return this.locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public PebbleTemplate getParent() {
		return parent;
	}

	public void setParent(PebbleTemplate parent) {
		this.parent = parent;
	}

	public PebbleTemplate getChild() {
		return child;
	}

	public void setChild(PebbleTemplate child) {
		this.child = child;
	}

	protected void addImportedTemplate(PebbleTemplate template) {
		this.importedTemplates.add(template);
	}

	public abstract void initBlocks();

	public abstract void initMacros();
}
