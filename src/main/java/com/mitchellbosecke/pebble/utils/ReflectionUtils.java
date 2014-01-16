package com.mitchellbosecke.pebble.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.error.AttributeNotFoundException;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.node.expression.NodeExpressionGetAttributeOrMethod;

public class ReflectionUtils {

	public static Object getAttribute(PebbleEngine engine, NodeExpressionGetAttributeOrMethod.Type type, Object object,
			String attribute, Object... args) throws PebbleException {
		if (object == null) {
			throw new NullPointerException(String.format("Can not get attribute [%s] of null object.", attribute));
		}

		// hold onto original name for error reporting
		String originalAttributeName = attribute;

		Class<?> clazz = object.getClass();

		Object result = null;

		boolean found = false;

		Method method = null;

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
}
