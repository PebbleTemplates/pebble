/*
 * This file is part of Pebble.
 * <p>
 * Copyright (c) 2014 by Mitchell Bösecke
 * <p>
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.mitchellbosecke.pebble.template;

import com.mitchellbosecke.pebble.cache.CacheKey;
import com.mitchellbosecke.pebble.cache.PebbleCache;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.extension.ExtensionRegistry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * An evaluation context will store all stateful data that is necessary for the evaluation of a
 * template. Passing the entire state around will assist with thread safety.
 *
 * @author Mitchell
 */
public class EvaluationContextImpl implements EvaluationContext {

  private final boolean strictVariables;

  /**
   * A template will look to it's parent and children for overridden macros and other features; this
   * inheritance chain will help the template keep track of where in the inheritance chain it
   * currently is.
   */
  private final Hierarchy hierarchy;

  /**
   * A scope is a set of visible variables. A trivial template will only have one scope. New scopes
   * are added with for loops and macros for example.
   * <p>
   * Most scopes will have a link to their parent scope which allow an evaluation to look up the
   * scope chain for variables. A macro is an exception to this as it only has access to it's local
   * variables.
   */
  private final ScopeChain scopeChain;

  /**
   * The locale of this template.
   */
  private final Locale locale;

  /**
   * All the available filters/tests/functions for this template.
   */
  private final ExtensionRegistry extensionRegistry;

  /**
   * The tag cache
   */
  private final PebbleCache<CacheKey, Object> tagCache;

  /**
   * The user-provided ExecutorService (can be null).
   */
  private final ExecutorService executorService;

  /**
   * The imported templates are used to look up macros.
   */
  private final List<PebbleTemplateImpl> importedTemplates;

  /**
   * The named imported templates are used to look up macros.
   */
  private final Map<String, PebbleTemplateImpl> namedImportedTemplates;

  /**
   * evaluation options
   */
  private final EvaluationOptions evaluationOptions;

  /**
   * Constructor used to provide all final variables.
   *
   * @param self The template implementation
   * @param strictVariables Whether strict variables is to be used
   * @param locale The locale of the template
   * @param extensionRegistry The extension registry
   * @param executorService The optional executor service
   * @param scopeChain The scope chain
   * @param hierarchy The inheritance chain
   * @param tagCache The cache used by the "cache" tag
   */
  public EvaluationContextImpl(PebbleTemplateImpl self, boolean strictVariables, Locale locale,
      ExtensionRegistry extensionRegistry, PebbleCache<CacheKey, Object> tagCache,
      ExecutorService executorService, List<PebbleTemplateImpl> importedTemplates,
      Map<String, PebbleTemplateImpl> namedImportedTemplates, ScopeChain scopeChain,
      Hierarchy hierarchy, EvaluationOptions evaluationOptions) {

    if (hierarchy == null) {
      hierarchy = new Hierarchy(self);
    }

    this.strictVariables = strictVariables;
    this.locale = locale;
    this.extensionRegistry = extensionRegistry;
    this.tagCache = tagCache;
    this.executorService = executorService;
    this.importedTemplates = importedTemplates;
    this.namedImportedTemplates = namedImportedTemplates;
    this.scopeChain = scopeChain;
    this.hierarchy = hierarchy;
    this.evaluationOptions = evaluationOptions;
  }

  /**
   * Makes an exact copy of the evaluation context EXCEPT for the inheritance chain. This is
   * necessary for the "include" tag.
   *
   * @param self The template implementation
   * @return A copy of the evaluation context
   */
  public EvaluationContextImpl shallowCopyWithoutInheritanceChain(PebbleTemplateImpl self) {
    EvaluationContextImpl result = new EvaluationContextImpl(self, this.strictVariables,
        this.locale, this.extensionRegistry, this.tagCache,
        this.executorService, this.importedTemplates, this.namedImportedTemplates, this.scopeChain,
        null, this.evaluationOptions);
    return result;
  }

  /**
   * Makes a "snapshot" of the evaluation context. The scopeChain object will be a deep copy and the
   * imported templates will be a new list. This is used for the "parallel" tag.
   *
   * @param self The template implementation
   * @return A copy of the evaluation context
   */
  public EvaluationContextImpl threadSafeCopy(PebbleTemplateImpl self) {
    EvaluationContextImpl result = new EvaluationContextImpl(self, this.strictVariables,
        this.locale, this.extensionRegistry, this.tagCache,
        this.executorService, new ArrayList<>(this.importedTemplates),
        new HashMap<>(this.namedImportedTemplates), this.scopeChain.deepCopy(), this.hierarchy,
        this.evaluationOptions);
    return result;
  }

  /**
   * Returns the named imported template.
   *
   * @return the named imported template.
   */
  public PebbleTemplateImpl getNamedImportedTemplate(String alias) {
    return this.namedImportedTemplates.get(alias);
  }

  public void addNamedImportedTemplates(String alias, PebbleTemplateImpl template) {
    if (this.namedImportedTemplates.containsKey(alias)) {
      throw new PebbleException(null,
          "More than one named template can not share the same name: " + alias);
    }
    this.namedImportedTemplates.put(alias, template);
  }

  /**
   * Returns whether or not this template is being evaluated in "strict templates" mode
   *
   * @return Whether or not this template is being evaluated in "strict tempaltes" mode.
   */
  @Override
  public boolean isStrictVariables() {
    return this.strictVariables;
  }

  /**
   * Returns the locale
   *
   * @return The current locale
   */
  @Override
  public Locale getLocale() {
    return this.locale;
  }

  /**
   * Returns the extension registry used to access all of the tests/filters/functions
   *
   * @return The extension registry
   */
  public ExtensionRegistry getExtensionRegistry() {
    return this.extensionRegistry;
  }

  /**
   * Returns the executor service if exists or null
   *
   * @return The executor service if exists, or null
   */
  public ExecutorService getExecutorService() {
    return this.executorService;
  }

  /**
   * Returns a list of imported templates.
   *
   * @return A list of imported templates.
   */
  public List<PebbleTemplateImpl> getImportedTemplates() {
    return this.importedTemplates;
  }

  /**
   * Returns the cache used for the "cache" tag
   *
   * @return The cache used for the "cache" tag
   */
  public PebbleCache<CacheKey, Object> getTagCache() {
    return this.tagCache;
  }

  /**
   * Returns the scope chain data structure that allows variables to be added/removed from the
   * current scope and retrieved from the nearest visible scopes.
   *
   * @return The scope chain.
   */
  public ScopeChain getScopeChain() {
    return this.scopeChain;
  }

  /**
   * Returns the data structure representing the entire hierarchy of the template currently being
   * evaluated.
   *
   * @return The inheritance chain
   */
  public Hierarchy getHierarchy() {
    return this.hierarchy;
  }

  /**
   * Returns the evaluation options.
   *
   * @return the evaluation options
   */
  public EvaluationOptions getEvaluationOptions() {
    return this.evaluationOptions;
  }

  @Override
  public Object getVariable(String key) {
    return this.scopeChain.get(key);
  }
}
