/*******************************************************************************
 * This file is part of Pebble.
 * <p>
 * Copyright (c) 2014 by Mitchell Bösecke
 * <p>
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.template;

import com.google.common.cache.Cache;
import com.mitchellbosecke.pebble.cache.BaseTagCacheKey;
import com.mitchellbosecke.pebble.extension.ExtensionRegistry;

import java.util.*;
import java.util.concurrent.ExecutorService;

/**
 * An evaluation context will store all stateful data that is necessary for the
 * evaluation of a template. Passing the entire state around will assist with
 * thread safety.
 *
 * @author Mitchell
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
     * <p>
     * Most scopes will have a link to their parent scope which allow an
     * evaluation to look up the scope chain for variables. A macro is an
     * exception to this as it only has access to it's local variables.
     */
    private final ScopeChain scopeChain;

    /**
     * The locale of this template. Will be used by LocaleAware filters,
     * functions, etc.
     */
    private final Locale locale;

    /**
     * All the available filters for this template.
     */
    private final ExtensionRegistry extensionRegistry;

    /**
     * The tag cache
     */
    private Cache<BaseTagCacheKey, Object> tagCache;

    /**
     * The user-provided ExecutorService (can be null).
     */
    private final ExecutorService executorService;

    /**
     * The imported templates are used to look up macros.
     */
    private final List<PebbleTemplateImpl> importedTemplates = new ArrayList<>();

    /**
     * Constructor used to provide all final variables.
     *
     * @param self              The template implementation
     * @param strictVariables   Whether strict variables is to be used
     * @param locale            The locale of the template
     * @param extensionRegistry The extension registry
     * @param executorService   The optional executor service
     * @param scopeChain        The scope chain
     * @param inheritanceChain  The inheritance chain
     */
    public EvaluationContext(PebbleTemplateImpl self, boolean strictVariables, Locale locale,
            ExtensionRegistry extensionRegistry, Cache<BaseTagCacheKey, Object> tagCache,
            ExecutorService executorService, ScopeChain scopeChain, InheritanceChain inheritanceChain) {

        if (inheritanceChain == null) {
            inheritanceChain = new InheritanceChain(self);
        }

        this.strictVariables = strictVariables;
        this.locale = locale;
        this.extensionRegistry = extensionRegistry;
        this.tagCache = tagCache;
        this.executorService = executorService;
        this.scopeChain = scopeChain;
        this.inheritanceChain = inheritanceChain;
    }

    /**
     * Makes an exact copy of the evaluation context EXCEPT for the inheritance
     * chain. This is necessary for the "include" tag.
     *
     * @param self The template implementation
     * @return A copy of the evaluation context
     */
    public EvaluationContext shallowCopyWithoutInheritanceChain(PebbleTemplateImpl self) {
        EvaluationContext result = new EvaluationContext(self, strictVariables, locale, extensionRegistry, tagCache,
                executorService, scopeChain, null);
        return result;
    }

    /**
     * Makes an exact copy of the evaluation context except the "scopeChain"
     * object will be a deep copy without reference to the original. This is
     * used for the "parallel" tag.
     *
     * @param self The template implementation
     * @return A copy of the evaluation context
     */
    public EvaluationContext deepCopy(PebbleTemplateImpl self) {
        EvaluationContext result = new EvaluationContext(self, strictVariables, locale, extensionRegistry, tagCache,
                executorService, scopeChain.deepCopy(), inheritanceChain);
        return result;
    }

    /**
     * This method might be called DURING the evaluation of a template (ex. for
     * node, set node, and macro node) and must be thread safe in case there are
     * multiple threads evaluating the same template (via parallel tag).
     *
     * @param key   Key
     * @param value Value
     */
    public void put(String key, Object value) {
        scopeChain.put(key, value);
    }

    /**
     * Will look for a variable, traveling upwards through the scope chain until
     * it is found.
     *
     * @param key Key
     * @return The object, if found
     */
    public Object get(String key) {
        return scopeChain.get(key);
    }

    /**
     * Checks if the given key exists within the context.
     *
     * @param key the key for which the check should be executed for.
     * @return {@code true} when the key does exists or {@code false} when the
     * given key does not exists.
     */
    public boolean containsKey(String key) {
        return scopeChain.containsKey(key);
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
        pushScope(new HashMap<String, Object>());
    }

    public void pushScope(Map<String, Object> map) {
        scopeChain.pushScope(map);
    }

    public boolean currentScopeContainsVariable(String variableName) {
        return scopeChain.currentScopeContainsVariable(variableName);
    }

    /**
     * Pushes a new scope that doesn't contain a reference to the current scope.
     * This occurs for macros. Variable lookup will end at this scope.
     */
    public void pushLocalScope() {
        scopeChain.pushLocalScope();
    }

    public void popScope() {
        scopeChain.popScope();
    }

    public boolean isStrictVariables() {
        return strictVariables;
    }

    public Locale getLocale() {
        return locale;
    }

    public ExtensionRegistry getExtensionRegistry() {
        return extensionRegistry;
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

    public Cache<BaseTagCacheKey, Object> getTagCache() {
        return tagCache;
    }

}
