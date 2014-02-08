package com.mitchellbosecke.pebble.template;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Stack;

import com.mitchellbosecke.pebble.error.AttributeNotFoundException;

/**
 * An evaluation context will store all stateful data that is necessary for the
 * evaluation of a template. Passing the entire state around will assist with
 * thread safety.
 * 
 * @author Mitchell
 * 
 */
public class EvaluationContext {

	private final boolean strictVariables;

	/**
	 * A template will look to it's parent and children for overridden macros
	 * and other features; this inheritance chain will help the template keep
	 * track of where in the inheritance chain it currently is.
	 */
	private final Stack<PebbleTemplateImpl> inheritanceChain;

	/**
	 * A scope is a set of visible variables. A trivial template will only have
	 * one scope. New scopes are added with for loops and macros for example.
	 * 
	 * Most scopes will have a link to their parent scope which allow an
	 * evaluation to look up the scope chain for variables. A macro is an
	 * exception to this as it only has access to it's local variables.
	 */
	private final Stack<Scope> scopes;

	/**
	 * We cache the attributes of objects for performance purposes.
	 */
	private final Map<Class<?>, ClassAttributeCacheEntry> attributeCache;

	/**
	 * The locale of this template. Will be used by LocaleAware filters,
	 * functions, etc.
	 */
	private Locale locale;

	public EvaluationContext(boolean strictVariables) {
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

	/**
	 * Will look for a variable, traveling upwards through the scope chain until
	 * it is found.
	 * 
	 * @param key
	 * @return
	 * @throws AttributeNotFoundException
	 */
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

	public void pushInheritanceChain(PebbleTemplateImpl template) {
		this.inheritanceChain.push(template);
	}

	public void popInheritanceChain() {
		this.inheritanceChain.pop();
	}

	public PebbleTemplateImpl getChildTemplate() {
		if (inheritanceChain.isEmpty()) {
			return null;
		}
		return inheritanceChain.peek();
	}

	/**
	 * Creates a new scope that contains a reference to the current scope.
	 */
	public void pushScope() {
		scopes.push(new Scope(scopes.peek()));
	}

	/**
	 * Pushes a new scope that doesn't contain a reference to the current scope.
	 * This occurs for macros. Variable lookup will end at this scope.
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
