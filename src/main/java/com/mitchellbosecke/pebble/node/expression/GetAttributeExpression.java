/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Original work Copyright (c) 2009-2013 by the Twig Team
 * Modified work Copyright (c) 2013 by Mitchell Bösecke
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.node.expression;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Map;

import com.mitchellbosecke.pebble.error.AttributeNotFoundException;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.extension.NodeVisitor;
import com.mitchellbosecke.pebble.template.ClassAttributeCacheEntry;
import com.mitchellbosecke.pebble.template.EvaluationContext;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;

/**
 * Used to get an attribute from an object. It will look up attributes in the
 * following order: map entry, get method, is method, has method, public method,
 * public field. It current only supports zero-argument methods.
 * 
 * @author Mitchell
 * 
 */
public class GetAttributeExpression implements Expression<Object> {

	private final Expression<?> node;
	private final String attributeName;

	public GetAttributeExpression(Expression<?> node, String attributeName) {
		this.node = node;
		this.attributeName = attributeName;
	}

	@Override
	public Object evaluate(PebbleTemplateImpl self, EvaluationContext context) throws PebbleException {
		Object object = node.evaluate(self, context);

		if (object == null) {
			if (context.isStrictVariables()) {
				throw new NullPointerException(String.format("Can not get attribute [%s] of null object.",
						attributeName));
			} else {
				return null;
			}
		}

		// hold onto original name for error reporting
		String originalAttributeName = attributeName;

		Class<?> clazz = object.getClass();

		Object result = null;

		// first we check maps, as they are a bit of an exception
		if (object instanceof Map && ((Map<?, ?>) object).containsKey(attributeName)) {
			return ((Map<?, ?>) object).get(attributeName);
		}

		Member member = null;
		if (attributeName != null) {
			// check if it's cached
			ClassAttributeCacheEntry cacheEntry = context.getAttributeCache().get(clazz);
			if (cacheEntry == null) {
				cacheEntry = new ClassAttributeCacheEntry();
				context.getAttributeCache().put(clazz, cacheEntry);
			}

			if (cacheEntry.hasAttribute(attributeName)) {
				member = cacheEntry.getAttribute(attributeName);
			} else {
				member = findMember(object, attributeName);
				cacheEntry.putAttribute(attributeName, member);
			}

		}

		if (member == null && context.isStrictVariables()) {
			throw new AttributeNotFoundException(
					null,
					String.format(
							"Attribute [%s] of [%s] does not exist or can not be accessed and strict variables is set to true.",
							originalAttributeName, clazz));
		}

		try {
			if (member instanceof Method) {
				result = ((Method) member).invoke(object);
			} else if (member instanceof Field) {
				result = ((Field) member).get(object);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
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

	private Member findMember(Object object, String attributeName) {

		Class<?> clazz = object.getClass();

		Member member = null;

		// capitalize first letter of attribute for the following attempts
		String attributeCapitalized = Character.toUpperCase(attributeName.charAt(0)) + attributeName.substring(1);

		// check get method
		if (member == null) {
			try {
				member = clazz.getMethod("get" + attributeCapitalized);
			} catch (NoSuchMethodException | SecurityException e) {
			}
		}

		// check is method
		if (member == null) {
			try {
				member = clazz.getMethod("is" + attributeCapitalized);
			} catch (NoSuchMethodException | SecurityException e) {
			}
		}

		// check has method
		if (member == null) {
			try {
				member = clazz.getMethod("has" + attributeCapitalized);
			} catch (NoSuchMethodException | SecurityException e) {
			}
		}

		// check if attribute is a public method
		if (member == null) {
			try {
				member = clazz.getMethod(attributeName);
			} catch (NoSuchMethodException | SecurityException e) {
			}
		}

		// public field
		if (member == null) {
			try {
				member = clazz.getField(attributeName);
			} catch (NoSuchFieldException | SecurityException e) {
			}
		}

		if (member != null) {
			((AccessibleObject) member).setAccessible(true);
		}

		return member;
	}

}
