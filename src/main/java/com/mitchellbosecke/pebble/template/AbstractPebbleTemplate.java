/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Copyright (c) 2012 Mitchell Bosecke.
 * 
 * This work is licensed under the Creative Commons Attribution-ShareAlike 3.0 
 * Unported License. To view a copy of this license, visit 
 * http://creativecommons.org/licenses/by-sa/3.0/
 ******************************************************************************/
package com.mitchellbosecke.pebble.template;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import com.mitchellbosecke.pebble.error.PebbleException;

public abstract class AbstractPebbleTemplate implements PebbleTemplate {

	private String sourceCode;
	private StringBuilder builder = new StringBuilder();
	protected Map<String, Object> context;

	public abstract void buildContent();

	@Override
	public String render(Map<String, Object> context) {
		this.context = context;
		this.builder = new StringBuilder();
		buildContent();
		return builder.toString();
	}

	protected Object getContextValue(String key) {
		if (context.containsKey(key)) {
			return context.get(key);
		} else {
			throw new PebbleException("Could not find variable in context: "
					+ key);
		}
	}

	protected Object getAttribute(Object object, String attribute) {
		
		if(object == null){
			throw new PebbleException(String.format("Can not get attribute [%s] of null object.", attribute));
		}

		Class<?> clazz = object.getClass();

		Object result = null;
		boolean found = false;
		
		// is object a hash map?
		if(object instanceof Map && ((Map<?,?>)object).containsKey(attribute)){
			result = ((Map<?,?>)object).get(attribute);
			found = true;
		}

		// check for public field
		try {
			Field field = clazz.getField(attribute);
			result = field.get(object);
			found = true;
		} catch (NoSuchFieldException | SecurityException
				| IllegalArgumentException | IllegalAccessException e) {
		}
		
		Method method;
		
		// check if attribute is a method
		try {
			method = clazz.getMethod(attribute);
			result = method.invoke(object);
			found = true;
		} catch (NoSuchMethodException | SecurityException
				| IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {

		}
		
		// capitalize first letter of attribute for the following attempts
		attribute = Character.toUpperCase(attribute.charAt(0)) + attribute.substring(1);


		// check get method
		try {
			method = clazz.getMethod("get" + attribute);
			result = method.invoke(object);
			found = true;
		} catch (NoSuchMethodException | SecurityException
				| IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
		}

		// check is method
		try {
			method = clazz.getMethod("is" + attribute);
			result = method.invoke(object);
			found = true;
		} catch (NoSuchMethodException | SecurityException
				| IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			
		}

		
		if(!found){
			throw new PebbleException(String.format("Attribute [%s] of [%s] does not exist or can not be accessed.", attribute, clazz));
		}
		return result;

	}

	public void append(String string) {
		builder.append(string);
	}
	
	public void setSourceCode(String source){
		this.sourceCode = source;
	}
	
	public String getSourceCode(){
		return sourceCode;
	}

}
