package com.mitchellbosecke.pebble.template;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Stack;

import com.mitchellbosecke.pebble.error.AttributeNotFoundException;

public class Context {

	private final boolean strictVariables;

	private final Stack<PebbleTemplateImpl> inheritanceChain;
	
	private final Stack<Scope> scopes;
	
	private final Map<Class<?>, ClassAttributeCacheEntry> attributeCache;

	private Locale locale;

	public Context(boolean strictVariables) {
		this.strictVariables = strictVariables;
		
		this.inheritanceChain = new Stack<>();
		this.scopes = new Stack<>();
		this.attributeCache = new HashMap<>();

		// add an initial scope
		this.scopes.add(new Scope(null));
	}

	public void putAll(Map<String, Object> objects) {
		scopes.peek().putAll(objects);
	}

	public void put(String key, Object value) {
		scopes.peek().put(key, value);
	}

	public Object get(Object key) throws AttributeNotFoundException {

		Object result = null;
		boolean found = false;

		Scope scope = scopes.peek();
		while (scope != null) {
			if (scope.containsKey(key)) {
				found = true;
				result = scope.get(key);
			}
			scope = scope.getParent();
		}

		if (!found && isStrictVariables()) {
			throw new AttributeNotFoundException(null, String.format(
					"Variable [%s] does not exist and strict variables is set to true.", String.valueOf(key)));
		}
		return result;
	}
	
	public void pushInheritanceChain(PebbleTemplateImpl template){
		this.inheritanceChain.push(template);
	}
	
	public void popInheritanceChain(){
		this.inheritanceChain.pop();
	}
	
	public PebbleTemplateImpl getChildTemplate() {
		if(inheritanceChain.isEmpty()){
			return null;
		}
		return inheritanceChain.peek();
	}

	public void pushScope() {
		scopes.push(new Scope(scopes.peek()));
	}

	/**
	 * Pushes a scope that doesn't contain a reference to the previous scope.
	 * This occurs for macros.
	 */
	public void pushLocalScope() {
		scopes.push(new Scope(null));
	}

	public void popScope() {
		scopes.pop();
	}

	public boolean isStrictVariables() {
		return strictVariables;
	}

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public Map<Class<?>, ClassAttributeCacheEntry> getAttributeCache() {
		return attributeCache;
	}

}
