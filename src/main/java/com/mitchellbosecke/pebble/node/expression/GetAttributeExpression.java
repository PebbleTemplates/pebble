/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Copyright (c) 2014 by Mitchell Bösecke
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.node.expression;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.mitchellbosecke.pebble.error.AttributeNotFoundException;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.extension.NodeVisitor;
import com.mitchellbosecke.pebble.node.ArgumentsNode;
import com.mitchellbosecke.pebble.node.PositionalArgumentNode;
import com.mitchellbosecke.pebble.template.ClassAttributeCache;
import com.mitchellbosecke.pebble.template.EvaluationContext;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;

/**
 * Used to get an attribute from an object. It will look up attributes in the
 * following order: map entry, get method, is method, has method, public method,
 * public field.
 * 
 * @author Mitchell
 * 
 */
public class GetAttributeExpression implements Expression<Object> {

	private final Expression<?> node;
	private final String attributeName;
	private final ArgumentsNode args;

	public GetAttributeExpression(Expression<?> node, String attributeName) {
		this(node, attributeName, null);
	}

	public GetAttributeExpression(Expression<?> node, String attributeName, ArgumentsNode args) {
		this.node = node;
		this.attributeName = attributeName;
		this.args = args;
	}

	@Override
	public Object evaluate(PebbleTemplateImpl self, EvaluationContext context) throws PebbleException {
		Object object = node.evaluate(self, context);

		Object result = null;
		boolean found = false;

		if (object != null) {

			// first we check maps, as they are a bit of an exception
			if (args == null) {
				if (object instanceof Map && ((Map<?, ?>) object).containsKey(attributeName)) {
					result = ((Map<?, ?>) object).get(attributeName);
					found = true;
				}
			}

			if (!found) {

				/*
				 * turn args into an array of types and an array of values in
				 * order to use them for our reflection calls
				 */
				List<Class<?>> parameterTypes = new ArrayList<>();
				List<Object> parameterValues = new ArrayList<>();
				if (this.args != null) {
					for (PositionalArgumentNode arg : this.args.getPositionalArgs()) {
						Object parameterValue = arg.getValueExpression().evaluate(self, context);
						parameterTypes.add(parameterValue.getClass());
						parameterValues.add(parameterValue);
					}
				}
				Class<?>[] parameterTypesArray = parameterTypes.toArray(new Class[parameterTypes.size()]);
				Object[] parameterValuesArray = parameterValues.toArray(new Object[parameterValues.size()]);

				Member member = null;
				try {
					
					ClassAttributeCache cache = context.getAttributeCache();
					if(cache.containsKey(object, attributeName, parameterTypesArray)){
						member = cache.get(object, attributeName, parameterTypesArray);
					}else{
						member = findMember(object, attributeName, parameterTypesArray);
						cache.put(object, attributeName, parameterTypesArray, member);
					}
					
					if (member != null) {

						if (member instanceof Method) {
							result = ((Method) member).invoke(object, parameterValuesArray);
						} else if (member instanceof Field) {
							result = ((Field) member).get(object);
						}
						found = true;
					}
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					e.printStackTrace();
					throw new PebbleException(e, "Could not access attribute [" + attributeName + "]");
				}

			}
		}

		if (!found && context.isStrictVariables()) {
			throw new AttributeNotFoundException(
					null,
					String.format(
							"Attribute [%s] of [%s] does not exist or can not be accessed and strict variables is set to true.",
							attributeName, object.getClass().getName()));
		}
		return result;

	}

	@Override
	public void accept(NodeVisitor visitor) {
		visitor.visit(this);
	}

	public Expression<?> getNode() {
		return node;
	}

	public String getAttribute() {
		return attributeName;
	}

	public ArgumentsNode getArgumentsNode() {
		return args;
	}

	private Member findMember(Object object, String attributeName, Class<?>[] parameterTypes)
			throws IllegalAccessException {

		Class<?> clazz = object.getClass();

		boolean found = false;
		Member result = null;

		// capitalize first letter of attribute for the following attempts
		String attributeCapitalized = Character.toUpperCase(attributeName.charAt(0)) + attributeName.substring(1);

		// check get method
		if (!found) {
			try {
				result = clazz.getMethod("get" + attributeCapitalized, parameterTypes);
				found = true;
			} catch (NoSuchMethodException | SecurityException e) {
			}
		}

		// check is method
		if (!found) {
			try {
				result = clazz.getMethod("is" + attributeCapitalized, parameterTypes);
				found = true;
			} catch (NoSuchMethodException | SecurityException e) {
			}
		}

		// check has method
		if (!found) {
			try {
				result = clazz.getMethod("has" + attributeCapitalized, parameterTypes);
				found = true;
			} catch (NoSuchMethodException | SecurityException e) {
			}
		}

		// check if attribute is a public method
		if (!found) {
			try {
				result = clazz.getMethod(attributeName, parameterTypes);
				found = true;
			} catch (NoSuchMethodException | SecurityException e) {
			}
		}

		// public field
		if (!found) {
			try {
				result = clazz.getField(attributeName);
				found = true;
			} catch (NoSuchFieldException | SecurityException e) {
			}
		}

		if (result != null) {
			((AccessibleObject) result).setAccessible(true);
		}
		return result;
	}

}
