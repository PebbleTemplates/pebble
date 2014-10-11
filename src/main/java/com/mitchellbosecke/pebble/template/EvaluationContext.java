/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Copyright (c) 2014 by Mitchell Bösecke
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.template;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import com.mitchellbosecke.pebble.error.AttributeNotFoundException;
import com.mitchellbosecke.pebble.extension.Filter;
import com.mitchellbosecke.pebble.extension.Function;
import com.mitchellbosecke.pebble.extension.Test;

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
	private final InheritanceChain inheritanceChain;

	/**
	 * A scope is a set of visible variables. A trivial template will only have
	 * one scope. New scopes are added with for loops and macros for example.
	 * 
	 * Most scopes will have a link to their parent scope which allow an
	 * evaluation to look up the scope chain for variables. A macro is an
	 * exception to this as it only has access to it's local variables.
	 */
	private LinkedList<Scope> scopes;

	/**
	 * The locale of this template. Will be used by LocaleAware filters,
	 * functions, etc.
	 */
	private final Locale locale;

	/**
	 * All the available filters for this template.
	 */
	private final Map<String, Filter> filters;

	/**
	 * All the available tests for this template.
	 */
	private final Map<String, Test> tests;

	/**
	 * All the available functions for this template.
	 */
	private final Map<String, Function> functions;

	/**
	 * The user-provided ExecutorService (can be null).
	 */
	private final ExecutorService executorService;

	/**
	 * The imported templates are used to look up macros.
	 */
	private final List<PebbleTemplateImpl> importedTemplates = new ArrayList<>();

	/**
	 * Constructor
	 * 
	 * @param self
	 * @param strictVariables
	 * @param locale
	 * @param filters
	 * @param tests
	 * @param functions
	 * @param executorService
	 */
	public EvaluationContext(PebbleTemplateImpl self, boolean strictVariables,
			Locale locale, Map<String, Filter> filters,
			Map<String, Test> tests, Map<String, Function> functions,
			ExecutorService executorService) {
		this.strictVariables = strictVariables;
		this.locale = locale;
		this.filters = filters;
		this.tests = tests;
		this.functions = functions;
		this.executorService = executorService;
		this.inheritanceChain = new InheritanceChain(self);
		this.scopes = new LinkedList<>();

		// add an initial scope
		this.scopes.add(new Scope(null));
	}

	/**
	 * Makes an exact copy of the evaluation context EXCEPT for the inheritance
	 * chain. This is necessary for the "include" tag.
	 * 
	 * @return
	 */
	public EvaluationContext copyWithoutInheritanceChain(PebbleTemplateImpl self) {
		EvaluationContext result = new EvaluationContext(self, strictVariables,
				getLocale(), filters, tests, functions, executorService);
		result.setScopes(scopes);
		return result;
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
		while (scope != null && !found) {
			if (scope.containsKey(key)) {
				found = true;
				result = scope.get(key);
			}
			scope = scope.getParent();
		}

		if (!found && isStrictVariables()) {
			throw new AttributeNotFoundException(
					null,
					String.format(
							"Variable [%s] does not exist and strict variables is set to true.",
							String.valueOf(key)));
		}
		return result;
	}

	public void ascendInheritanceChain() {
		inheritanceChain.ascend();
	}

	public void descendInheritanceChain() {
		inheritanceChain.descend();
	}

	public PebbleTemplateImpl getParentTemplate() {
		return inheritanceChain.getParent();
	}

	public PebbleTemplateImpl getChildTemplate() {
		return inheritanceChain.getChild();
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

	public Map<String, Test> getTests() {
		return tests;
	}

	public Map<String, Filter> getFilters() {
		return filters;
	}

	public Map<String, Function> getFunctions() {
		return functions;
	}

	public ExecutorService getExecutorService() {
		return executorService;
	}

	public void addImportedTemplate(PebbleTemplateImpl template) {
		this.importedTemplates.add(template);
	}

	public List<PebbleTemplateImpl> getImportedTemplates() {
		return this.importedTemplates;
	}

	public void setParent(PebbleTemplateImpl parent) {
		inheritanceChain.pushAncestor(parent);
	}

	public LinkedList<Scope> getScopes() {
		return scopes;
	}

	public void setScopes(LinkedList<Scope> scopes) {
		this.scopes = scopes;
	}
}
