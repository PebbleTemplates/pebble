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
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.error.AttributeNotFoundException;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.extension.Filter;
import com.mitchellbosecke.pebble.extension.SimpleFunction;
import com.mitchellbosecke.pebble.extension.Test;
import com.mitchellbosecke.pebble.node.NodeMacro;
import com.mitchellbosecke.pebble.node.expression.NodeExpressionGetAttributeOrMethod;
import com.mitchellbosecke.pebble.utils.Context;
import com.mitchellbosecke.pebble.utils.TemplateAware;

public abstract class AbstractPebbleTemplate implements PebbleTemplate {

	private String generatedJavaCode;
	private String source;

	protected Writer writer;
	protected StringBuilder builder = new StringBuilder();
	protected Context context;
	protected PebbleEngine engine;
	
	private Locale locale;

	public abstract void buildContent() throws PebbleException;

	@Override
	public void evaluate(Writer writer) throws PebbleException {
		Context context = initContext();
		evaluate(writer, context);
	}

	@Override
	public void evaluate(Writer writer, Map<String, Object> map) throws PebbleException {
		Context context = initContext();
		context.putAll(map);
		evaluate(writer, context);
	}

	private void evaluate(Writer writer, Context context) throws PebbleException {
		this.writer = writer;
		this.context = context;

		this.builder = new StringBuilder();
		buildContent();
		try {
			writer.write(builder.toString());

		} catch (IOException e) {
			e.printStackTrace();
			throw new PebbleException("Unable to write template output to writer.");
		} finally {
			try {
				writer.flush();
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
				throw new PebbleException("Unable to flush or close writer.");
			}
		}
	}

	private Context initContext() {
		Context context = new Context(engine.isStrictVariables());
		context.putAll(engine.getGlobalVariables());

		/*
		 * some global variables that have to be implemented here because they
		 * are unique to the particular template.
		 */
		context.put("_self", this);
		context.put("_context", context);
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

	protected void flush() throws PebbleException {
		try {
			writer.write(builder.toString());
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
			throw new PebbleException("Unable to write template output to writer.");
		}
		builder = new StringBuilder();
	}

	@Override
	public void setEngine(PebbleEngine engine) {
		this.engine = engine;
	}

	protected Object getAttribute(NodeExpressionGetAttributeOrMethod.Type type, Object object, String attribute)
			throws AttributeNotFoundException {
		return getAttribute(type, object, attribute, new Object[0]);
	}

	protected Object getAttribute(NodeExpressionGetAttributeOrMethod.Type type, Object object, String attribute,
			Object... args) throws AttributeNotFoundException {

		if (object == null) {
			throw new NullPointerException(String.format("Can not get attribute [%s] of null object.", attribute));
		}

		// hold onto original name for error reporting
		String originalAttributeName = attribute;

		Class<?> clazz = object.getClass();

		Object result = null;

		boolean found = false;

		Method method = null;

		boolean passContextAsArgument = false;

		// capitalize first letter of attribute for the following attempts
		String attributeCapitalized = Character.toUpperCase(attribute.charAt(0)) + attribute.substring(1);

		/*
		 * Entry in hash map.
		 * 
		 * Has priority because: - for loop stores variables in a hash map -
		 * doesn't require reflection and is therefore really fast
		 */
		if (!found && NodeExpressionGetAttributeOrMethod.Type.ANY.equals(type)) {

			if (object instanceof Map && ((Map<?, ?>) object).containsKey(attribute)) {
				result = ((Map<?, ?>) object).get(attribute);
				found = true;
			}
		}

		// check get method
		if (!found) {
			try {
				method = clazz.getMethod("get" + attributeCapitalized);
				found = true;
			} catch (NoSuchMethodException | SecurityException e) {
			}
		}

		// check is method
		if (!found) {
			try {
				method = clazz.getMethod("is" + attributeCapitalized);
				found = true;
			} catch (NoSuchMethodException | SecurityException e) {
			}
		}

		// check if attribute is a public method
		if (!found) {
			try {
				method = clazz.getMethod(attribute);
				found = true;
			} catch (NoSuchMethodException | SecurityException e) {
			}
		}

		// Check for macro
		if (!found) {
			try {

				// in order to use reflection we need to know the EXACT
				// number and types of arguments the intended method takes
				List<Class<?>> paramTypes = new ArrayList<>();

				for (@SuppressWarnings("unused")
				Object param : args) {
					paramTypes.add(Object.class);
				}

				/*
				 * Add an extra parameter to account for the secret _context
				 * object that we will pass
				 */
				paramTypes.add(Object.class);

				method = clazz.getMethod(NodeMacro.MACRO_PREFIX + attribute,
						paramTypes.toArray(new Class[paramTypes.size()]));
				found = true;
				passContextAsArgument = true;
			} catch (NoSuchMethodException | SecurityException e) {
			}
		}

		// public field
		if (!found && NodeExpressionGetAttributeOrMethod.Type.ANY.equals(type)) {

			try {
				Field field = clazz.getField(attribute);
				result = field.get(object);
				found = true;
			} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			}
		}

		if (method != null) {
			try {
				if (passContextAsArgument) {
					List<Object> arguments = new ArrayList<>(Arrays.asList(args));
					arguments.add(context);
					args = arguments.toArray();
				}
				if (args.length > 0) {
					result = method.invoke(object, args);
				} else {
					result = method.invoke(object);
				}
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				if (e instanceof InvocationTargetException) {
					((InvocationTargetException) e).getTargetException().printStackTrace();
				}
			}
		}

		if (!found) {
			if (engine.isStrictVariables()) {
				throw new AttributeNotFoundException(
						String.format(
								"Attribute [%s] of [%s] does not exist or can not be accessed and strict variables is set to true.",
								originalAttributeName, clazz));
			} else {
				result = null;
			}
		}
		return result;

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
		
		if(filter instanceof TemplateAware){
			((TemplateAware)filter).setTemplate(this);
		}

		if (filter == null) {
			throw new PebbleException(String.format("Filter [%s] does not exist.", filterName));
		}
		return filter.apply(input, arguments);
	}

	protected Object applyFunction(String functionName, Object... args) throws PebbleException {
		List<Object> arguments = new ArrayList<>();

		Collections.addAll(arguments, args);

		Map<String, SimpleFunction> functions = engine.getFunctions();
		SimpleFunction function = functions.get(functionName);

		if (function instanceof TemplateAware) {
			((TemplateAware) function).setTemplate(this);
		}

		if (function == null) {
			throw new PebbleException(String.format("Function [%s] does not exist.", functionName));
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

	@Override
	public void setGeneratedJavaCode(String generatedJavaCode) {
		this.generatedJavaCode = generatedJavaCode;
	}

	@Override
	public String getGeneratedJavaCode() {
		return generatedJavaCode;
	}

	@Override
	public void setSource(String source) {
		this.source = source;
	}

	@Override
	public String getSource() {
		return this.source;
	}
	
	@Override
	public Locale getLocale(){
		return this.locale;
	}

	@Override
	public void setLocale(Locale locale){
		this.locale = locale;
	}
}
