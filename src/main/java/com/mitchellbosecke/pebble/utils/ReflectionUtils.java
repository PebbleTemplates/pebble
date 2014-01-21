package com.mitchellbosecke.pebble.utils;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.mitchellbosecke.pebble.error.AttributeNotFoundException;
import com.mitchellbosecke.pebble.error.PebbleException;

public class ReflectionUtils {

	public static Object getAttribute(Context context, Object object, String attributeName, Object[] args)
			throws PebbleException {
		if (object == null) {
			throw new NullPointerException(String.format("Can not get attribute [%s] of null object.", attributeName));
		}

		// hold onto original name for error reporting
		String originalAttributeName = attributeName;

		Class<?> clazz = object.getClass();

		Object result = null;

		// first we check maps, as they are a bit of an exception
		if (args.length == 0 && object instanceof Map && ((Map<?, ?>) object).containsKey(attributeName)) {
			return ((Map<?, ?>) object).get(attributeName);
		}

		Member member = null;
		if (attributeName != null) {
			member = findMember(context, object, attributeName, args);
		}

		if ((attributeName == null || member == null) && context.isStrictVariables()) {
			throw new AttributeNotFoundException(
					String.format(
							"Attribute [%s] of [%s] does not exist or can not be accessed and strict variables is set to true.",
							originalAttributeName, clazz));
		}

		try {
			if (member instanceof Method) {
				result = ((Method) member).invoke(object, args);
			} else if (member instanceof Field) {
				result = ((Field) member).get(object);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return result;
	}

	private static Member findMember(Context context, Object object, String attributeName, Object[] args) {

		Class<?> clazz = object.getClass();

		Member member = null;

		// check if it's cached
		Map<String, Member> memberCache = context.getAttributeCache().get(clazz);
		if (memberCache != null) {
			if(memberCache.containsKey(attributeName)){
				// quick return
				return memberCache.get(attributeName);
			}
		} else {
			memberCache = new HashMap<>();
			context.getAttributeCache().put(clazz, memberCache);
		}

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
				Class<?>[] argClasses = new Class<?>[args.length];
				for (int i = 0; i < args.length; i++) {
					argClasses[i] = args[i].getClass();
				}
				member = clazz.getMethod(attributeName, argClasses);
			} catch (NoSuchMethodException | SecurityException e) {
			}
		}

		// public field
		if (member == null && args.length == 0) {
			try {
				member = clazz.getField(attributeName);
			} catch (NoSuchFieldException | SecurityException e) {
			}
		}

		if(member != null){
			((AccessibleObject) member).setAccessible(true);
		}

		memberCache.put(attributeName, member);
		
		return member;
	}
}
