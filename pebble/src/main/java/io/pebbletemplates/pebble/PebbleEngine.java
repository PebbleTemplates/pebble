/*
 * This file is part of Pebble.
 * <p>
 * Copyright (c) 2014 by Mitchell Bösecke
 * <p>
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package io.pebbletemplates.pebble;


import io.pebbletemplates.pebble.cache.CacheKey;
import io.pebbletemplates.pebble.cache.PebbleCache;
import io.pebbletemplates.pebble.cache.tag.ConcurrentMapTagCache;
import io.pebbletemplates.pebble.cache.tag.NoOpTagCache;
import io.pebbletemplates.pebble.cache.template.ConcurrentMapTemplateCache;
import io.pebbletemplates.pebble.cache.template.NoOpTemplateCache;
import io.pebbletemplates.pebble.error.LoaderException;
import io.pebbletemplates.pebble.lexer.LexerImpl;
import io.pebbletemplates.pebble.lexer.Syntax;
import io.pebbletemplates.pebble.lexer.TokenStream;
import io.pebbletemplates.pebble.node.RootNode;
import io.pebbletemplates.pebble.parser.Parser;
import io.pebbletemplates.pebble.parser.ParserImpl;
import io.pebbletemplates.pebble.parser.ParserOptions;
import io.pebbletemplates.pebble.attributes.methodaccess.BlacklistMethodAccessValidator;
import io.pebbletemplates.pebble.attributes.methodaccess.MethodAccessValidator;
import io.pebbletemplates.pebble.extension.escaper.EscapingStrategy;
import io.pebbletemplates.pebble.loader.ClasspathLoader;
import io.pebbletemplates.pebble.loader.DelegatingLoader;
import io.pebbletemplates.pebble.loader.FileLoader;
import io.pebbletemplates.pebble.loader.Loader;
import io.pebbletemplates.pebble.loader.StringLoader;
import io.pebbletemplates.pebble.extension.*;
import io.pebbletemplates.pebble.template.EvaluationOptions;
import io.pebbletemplates.pebble.template.PebbleTemplate;
import io.pebbletemplates.pebble.template.PebbleTemplateImpl;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;

import io.pebbletemplates.pebble.utils.TypeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The main class used for compiling templates. The PebbleEngine is responsible for delegating
 * responsibility to the lexer, parser, compiler, and template cache.
 *
 * @author Mitchell
 */
public class PebbleEngine {

  private final Logger logger = LoggerFactory.getLogger(PebbleEngine.class);
	
  private final Loader<?> loader;

  private final Syntax syntax;

  private final boolean strictVariables;

  private final Locale defaultLocale;

  private final int maxRenderedSize;

  private final PebbleCache<CacheKey, Object> tagCache;

  private final ExecutorService executorService;

  private final PebbleCache<Object, PebbleTemplate> templateCache;

  private final ExtensionRegistry extensionRegistry;

  private final ParserOptions parserOptions;

  private final EvaluationOptions evaluationOptions;

  /**
   * Constructor for the Pebble Engine given an instantiated Loader. This method does only load
   * those userProvidedExtensions listed here.
   *
   * @param loader The template loader for this engine
   * @param syntax the syntax to use for parsing the templates.
   */
  private PebbleEngine(Loader<?> loader,
      Syntax syntax,
      boolean strictVariables,
      Locale defaultLocale,
      int maxRenderedSize,
      PebbleCache<CacheKey, Object> tagCache,
      PebbleCache<Object, PebbleTemplate> templateCache,
      ExecutorService executorService,
      ExtensionRegistry extensionRegistry,
      ParserOptions parserOptions,
      EvaluationOptions evaluationOptions) {

    this.loader = loader;
    this.syntax = syntax;
    this.strictVariables = strictVariables;
    this.defaultLocale = defaultLocale;
    this.maxRenderedSize = maxRenderedSize;
    this.tagCache = tagCache;
    this.executorService = executorService;
    this.templateCache = templateCache;
    this.extensionRegistry = extensionRegistry;
    this.parserOptions = parserOptions;
    this.evaluationOptions = evaluationOptions;
  }

  /**
   * Loads, parses, and compiles a template into an instance of PebbleTemplate and returns this
   * instance.
   *
   * @param templateName The name of the template
   * @return PebbleTemplate The compiled version of the template
   */
  public PebbleTemplate getTemplate(String templateName) {
    return this.getTemplate(templateName, this.loader);
  }

  /**
   * Loads, parses, and compiles a template using a StringLoader into an instance of PebbleTemplate
   * and returns this instance.
   *
   * @param templateName The name of the template
   * @return PebbleTemplate The compiled version of the template
   */
  public PebbleTemplate getLiteralTemplate(String templateName) {
    return this.getTemplate(templateName, new StringLoader());
  }

  private PebbleTemplate getTemplate(String templateName, Loader loader) {
    /*
     * template name will be null if user uses the extends tag with an
     * expression that evaluates to null
     */
    if (templateName == null) {
      return null;
    }

    if (loader == null) {
      throw new LoaderException(null, "Loader has not yet been specified.");
    }

    Object cacheKey = loader.createCacheKey(templateName);
    return this.templateCache
        .computeIfAbsent(cacheKey, k -> this.getPebbleTemplate(templateName, loader, cacheKey));
  }

  private PebbleTemplate getPebbleTemplate(String templateName, Loader loader, Object cacheKey) {

    Reader templateReader = loader.getReader(cacheKey);
    
    try {
      this.logger.trace("Tokenizing template named {}", templateName);
      LexerImpl lexer = new LexerImpl(this.syntax,
          this.extensionRegistry.getUnaryOperators().values(),
          this.extensionRegistry.getBinaryOperators().values());
      TokenStream tokenStream = lexer.tokenize(templateReader, templateName);
      this.logger.trace("TokenStream: {}", tokenStream);
      
      Parser parser = new ParserImpl(this.extensionRegistry.getUnaryOperators(),
          this.extensionRegistry.getBinaryOperators(), this.extensionRegistry.getTokenParsers(),
              this.parserOptions);
      RootNode root = parser.parse(tokenStream);

      PebbleTemplateImpl instance = new PebbleTemplateImpl(this, root, templateName);

      for (NodeVisitorFactory visitorFactory : this.extensionRegistry.getNodeVisitors()) {
        visitorFactory.createVisitor(instance).visit(root);
      }

      return instance;

    } finally {
      try {
        templateReader.close();
      } catch (IOException e) {
        // can't do much about it
      }
    }
  }

  /**
   * Returns the loader
   *
   * @return The loader
   */
  public Loader<?> getLoader() {
    return this.loader;
  }

  /**
   * Returns the template cache
   *
   * @return The template cache
   */
  public PebbleCache<Object, PebbleTemplate> getTemplateCache() {
    return this.templateCache;
  }

  /**
   * Returns the strict variables setting
   *
   * @return The strict variables setting
   */
  public boolean isStrictVariables() {
    return this.strictVariables;
  }

  /**
   * Returns the default locale
   *
   * @return The default locale
   */
  public Locale getDefaultLocale() {
    return this.defaultLocale;
  }

  /**
   * Returns the max rendered size.
   *
   *  @return The max rendered size.
   */
  public int getMaxRenderedSize() {
    return this.maxRenderedSize;
  }

  /**
   * Returns the executor service
   *
   * @return The executor service
   */
  public ExecutorService getExecutorService() {
    return this.executorService;
  }

  /**
   * Returns the syntax which is used by this PebbleEngine.
   *
   * @return the syntax used by the PebbleEngine.
   */
  public Syntax getSyntax() {
    return this.syntax;
  }

  /**
   * Returns the extension registry.
   *
   * @return The extension registry
   */
  public ExtensionRegistry getExtensionRegistry() {
    return this.extensionRegistry;
  }

  /**
   * Returns the tag cache
   *
   * @return The tag cache
   */
  public PebbleCache<CacheKey, Object> getTagCache() {
    return this.tagCache;
  }

  /**
   * A builder to configure and construct an instance of a PebbleEngine.
   */
  public static class Builder {

    private Loader<?> loader;

    private Syntax syntax;

    private boolean strictVariables = false;

    private boolean enableNewLineTrimming = true;

    private Locale defaultLocale;

    private int maxRenderedSize = -1;

    private ExecutorService executorService;

    private PebbleCache<Object, PebbleTemplate> templateCache;

    private boolean cacheActive = true;

    private PebbleCache<CacheKey, Object> tagCache;

    private boolean literalDecimalTreatedAsInteger = false;

    private boolean greedyMatchMethod = false;

    private boolean literalNumbersAsBigDecimals = false;

    private MethodAccessValidator methodAccessValidator = new BlacklistMethodAccessValidator();

    private final ExtensionRegistryFactory factory = new ExtensionRegistryFactory();

    /**
     * Creates the builder.
     */
    public Builder() {

    }

    /**
     * Sets the loader used to find templates.
     *
     * @param loader A template loader
     * @return This builder object
     */
    public Builder loader(Loader<?> loader) {
      this.loader = loader;
      return this;
    }

    /**
     * Adds an extension, can be safely invoked several times to add different extensions.
     *
     * @param extensions One or more extensions to add
     * @return This builder object
     */
    public Builder extension(Extension... extensions) {
      this.factory.extension(extensions);
      return this;
    }

    /**
     * Sets the syntax to be used.
     *
     * @param syntax The syntax to be used
     * @return This builder object
     */
    public Builder syntax(Syntax syntax) {
      this.syntax = syntax;
      return this;
    }

    /**
     * Changes the <code>strictVariables</code> setting of the PebbleEngine. The default value of
     * this setting is "false".
     * <p>
     * The following examples will all print empty strings if strictVariables is false but will
     * throw exceptions if it is true:
     * </p>
     * {{ nonExistingVariable }} {{ nonExistingVariable.attribute }} {{ nullVariable.attribute }} {{
     * existingVariable.nullAttribute.attribute }} {{ existingVariable.nonExistingAttribute }} {{
     * array[-1] }}
     *
     * @param strictVariables Whether or not strict variables is used
     * @return This builder object
     */
    public Builder strictVariables(boolean strictVariables) {
      this.strictVariables = strictVariables;
      return this;
    }

    /**
     * Changes the <code>newLineTrimming</code> setting of the PebbleEngine. The default value of
     * this setting is "true".
     * <p>
     * By default, Pebble will trim a newline that immediately follows a Pebble tag. For example,
     * <code>{{key1}}\n{{key2}}</code> will have the newline removed.
     * </p>
     * <p>
     * If <code>newLineTrimming</code> is set to false, then the first newline following a Pebble
     * tag won't be trimmed.  All newlines will be preserved
     * </p>
     *
     * @param enableNewLineTrimming Whether or not the newline should be trimmed.
     * @return This builder object
     */
    public Builder newLineTrimming(boolean enableNewLineTrimming) {
      this.enableNewLineTrimming = enableNewLineTrimming;
      return this;
    }

    /**
     * Sets the Locale passed to all templates constructed by this PebbleEngine.
     * <p>
     * An individual template can always be given a new locale during evaluation.
     *
     * @param defaultLocale The default locale
     * @return This builder object
     */
    public Builder defaultLocale(Locale defaultLocale) {
      this.defaultLocale = defaultLocale;
      return this;
    }

    /**
     * Sets the maximum size of the rendered template to protect against macro bombs.
     * See for example https://github.com/PebbleTemplates/pebble/issues/526.
     * If the rendered template exceeds this limit, then a PebbleException is thrown.
     * The default value is -1 and it means unlimited.
     * @param maxRenderedSize The maximum allowed size of the rendered template.
     * @return This builder object.
     */
    public Builder maxRenderedSize(int maxRenderedSize) {
      this.maxRenderedSize = maxRenderedSize;
      return this;
    }

    /**
     * Sets the executor service which is required if using one of Pebble's multithreading features
     * such as the "parallel" tag.
     *
     * @param executorService The executor service
     * @return This builder object
     */
    public Builder executorService(ExecutorService executorService) {
      this.executorService = executorService;
      return this;
    }

    /**
     * Sets the cache used by the engine to store compiled PebbleTemplate instances.
     *
     * @param templateCache The template cache
     * @return This builder object
     */
    public Builder templateCache(PebbleCache<Object, PebbleTemplate> templateCache) {
      this.templateCache = templateCache;
      return this;
    }

    /**
     * Sets the cache used by the "cache" tag.
     *
     * @param tagCache The tag cache
     * @return This builder object
     */
    public Builder tagCache(PebbleCache<CacheKey, Object> tagCache) {
      this.tagCache = tagCache;
      return this;
    }

    /**
     * Sets whether or not escaping should be performed automatically.
     *
     * @param autoEscaping The auto escaping setting
     * @return This builder object
     */
    public Builder autoEscaping(boolean autoEscaping) {
      this.factory.autoEscaping(autoEscaping);
      return this;
    }

    /**
     * Sets whether or not core operators overrides should be allowed.
     *
     * @param allowOverrideCoreOperators Whether or not core operators overrides should be allowed.
     * @return This builder object
     */
    public Builder allowOverrideCoreOperators(boolean allowOverrideCoreOperators) {
      this.factory.allowOverrideCoreOperators(allowOverrideCoreOperators);
      return this;
    }

    /**
     * Sets the default escaping strategy of the built-in escaper extension.
     *
     * @param strategy The name of the default escaping strategy
     * @return This builder object
     */
    public Builder defaultEscapingStrategy(String strategy) {
      this.factory.defaultEscapingStrategy(strategy);
      return this;
    }

    /**
     * Adds an escaping strategy to the built-in escaper extension.
     *
     * @param name The name of the escaping strategy
     * @param strategy The strategy implementation
     * @return This builder object
     */
    public Builder addEscapingStrategy(String name, EscapingStrategy strategy) {
      this.factory.addEscapingStrategy(name, strategy);
      return this;
    }

    /**
     * Enable/disable all caches, i.e. cache used by the engine to store compiled PebbleTemplate
     * instances and tags cache
     *
     * @param cacheActive toggle to enable/disable all caches
     * @return This builder object
     */
    public Builder cacheActive(boolean cacheActive) {
      this.cacheActive = cacheActive;
      return this;
    }

    /**
     * Validator that can be used to validate object/method access
     *
     * @param methodAccessValidator Validator that can be used to validate object/method access
     * @return This builder object
     */
    public Builder methodAccessValidator(MethodAccessValidator methodAccessValidator) {
      this.methodAccessValidator = methodAccessValidator;
      return this;
    }

    /**
     * Enable/disable treat literal decimal as Integer. Default is disabled, treated as Long.
     *
     * @param literalDecimalTreatedAsInteger toggle to enable/disable literal decimal treated as
     * integer
     * @return This builder object
     */
    public Builder literalDecimalTreatedAsInteger(boolean literalDecimalTreatedAsInteger) {
      this.literalDecimalTreatedAsInteger = literalDecimalTreatedAsInteger;
      return this;
    }

    /**
     * Enable/disable treat literal numbers as BigDecimals. Default is disabled, treated as Long/Double.
     *
     * @param literalNumbersAsBigDecimals toggle to enable/disable literal numbers treated as
     * BigDecimals
     * @return This builder object
     */
    public Builder literalNumbersAsBigDecimals(boolean literalNumbersAsBigDecimals) {
      this.literalNumbersAsBigDecimals = literalNumbersAsBigDecimals;
      return this;
    }

    /**
     * Enable/disable greedy matching mode for finding java method. Default is disabled. If enabled,
     * when can not find perfect method (method name, parameter length and parameter type are all
     * satisfied), reduce the limit of the parameter type, try to find other method which has
     * compatible parameter types.
     *
     * For example,
     * <pre> {{ obj.number(2) }} </pre>
     * the expression can match all of below methods.
     * <pre>
     *   public Long getNumber(Long v) {...}
     *   public Integer getNumber(Integer v) {...}
     *   public Short getNumber(Short v) {...}
     *   public Byte getNumber(Byte v) {...}
     *   ...
     * </pre>
     *
     * @param greedyMatchMethod toggle to enable/disable greedy match method
     * @return This builder object
     * @see TypeUtils#compatibleCast(Object, Class)
     */
    public Builder greedyMatchMethod(boolean greedyMatchMethod) {
      this.greedyMatchMethod = greedyMatchMethod;
      return this;
    }

    /**
     * Registers an implementation of {@link ExtensionCustomizer} to change runtime-behaviour of standard
     * functionality.
     *
     * @param customizer The customizer which wraps any non-user-provided extension
     * @return This build object
     */
    public Builder registerExtensionCustomizer(Function<Extension, ExtensionCustomizer> customizer) {
      this.factory.registerExtensionCustomizer(customizer);
      return this;
    }

    /**
     * Creates the PebbleEngine instance.
     *
     * @return A PebbleEngine object that can be used to create PebbleTemplate objects.
     */
    public PebbleEngine build() {

      ExtensionRegistry extensionRegistry = this.factory.buildExtensionRegistry();

      // default loader
      if (this.loader == null) {
        List<Loader<?>> defaultLoadingStrategies = new ArrayList<>();
        defaultLoadingStrategies.add(new ClasspathLoader());
        defaultLoadingStrategies.add(new FileLoader());
        this.loader = new DelegatingLoader(defaultLoadingStrategies);
      }

      // default locale
      if (this.defaultLocale == null) {
        this.defaultLocale = Locale.getDefault();
      }

      if (this.cacheActive) {
        // default caches
        if (this.templateCache == null) {
          this.templateCache = new ConcurrentMapTemplateCache();
        }

        if (this.tagCache == null) {
          this.tagCache = new ConcurrentMapTagCache();
        }
      } else {
        this.templateCache = new NoOpTemplateCache();
        this.tagCache = new NoOpTagCache();
      }

      if (this.syntax == null) {
        this.syntax = new Syntax.Builder().setEnableNewLineTrimming(this.enableNewLineTrimming)
            .build();
      }

      ParserOptions parserOptions = new ParserOptions();
      parserOptions.setLiteralDecimalTreatedAsInteger(this.literalDecimalTreatedAsInteger);
      parserOptions.setLiteralNumbersAsBigDecimals(this.literalNumbersAsBigDecimals);

      EvaluationOptions evaluationOptions = new EvaluationOptions(this.greedyMatchMethod,
          this.methodAccessValidator);
      return new PebbleEngine(this.loader, this.syntax, this.strictVariables, this.defaultLocale, this.maxRenderedSize,
          this.tagCache, this.templateCache,
          this.executorService, extensionRegistry, parserOptions, evaluationOptions);
    }
  }

  public EvaluationOptions getEvaluationOptions() {
    return this.evaluationOptions;
  }
}
