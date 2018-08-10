/*
 * This file is part of Pebble.
 * <p>
 * Copyright (c) 2014 by Mitchell Bösecke
 * <p>
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.mitchellbosecke.pebble;


import com.mitchellbosecke.pebble.cache.CacheKey;
import com.mitchellbosecke.pebble.cache.PebbleCache;
import com.mitchellbosecke.pebble.cache.tag.ConcurrentMapTagCache;
import com.mitchellbosecke.pebble.cache.tag.NoOpTagCache;
import com.mitchellbosecke.pebble.cache.template.ConcurrentMapTemplateCache;
import com.mitchellbosecke.pebble.cache.template.NoOpTemplateCache;
import com.mitchellbosecke.pebble.error.LoaderException;
import com.mitchellbosecke.pebble.extension.Extension;
import com.mitchellbosecke.pebble.extension.ExtensionRegistry;
import com.mitchellbosecke.pebble.extension.NodeVisitorFactory;
import com.mitchellbosecke.pebble.extension.core.AttributeResolverExtension;
import com.mitchellbosecke.pebble.extension.core.CoreExtension;
import com.mitchellbosecke.pebble.extension.escaper.EscaperExtension;
import com.mitchellbosecke.pebble.extension.escaper.EscapingStrategy;
import com.mitchellbosecke.pebble.extension.i18n.I18nExtension;
import com.mitchellbosecke.pebble.lexer.LexerImpl;
import com.mitchellbosecke.pebble.lexer.Syntax;
import com.mitchellbosecke.pebble.lexer.TokenStream;
import com.mitchellbosecke.pebble.loader.ClasspathLoader;
import com.mitchellbosecke.pebble.loader.DelegatingLoader;
import com.mitchellbosecke.pebble.loader.FileLoader;
import com.mitchellbosecke.pebble.loader.Loader;
import com.mitchellbosecke.pebble.loader.StringLoader;
import com.mitchellbosecke.pebble.node.RootNode;
import com.mitchellbosecke.pebble.parser.Parser;
import com.mitchellbosecke.pebble.parser.ParserImpl;
import com.mitchellbosecke.pebble.parser.ParserOptions;
import com.mitchellbosecke.pebble.template.EvaluationOptions;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;

/**
 * The main class used for compiling templates. The PebbleEngine is responsible for delegating
 * responsibility to the lexer, parser, compiler, and template cache.
 *
 * @author Mitchell
 */
public class PebbleEngine {

  private final Loader<?> loader;

  private final Syntax syntax;

  private final boolean strictVariables;

  private final Locale defaultLocale;

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
   * @param loader     The template loader for this engine
   * @param syntax     the syntax to use for parsing the templates.
   * @param extensions The userProvidedExtensions which should be loaded.
   */
  private PebbleEngine(Loader<?> loader,
      Syntax syntax,
      boolean strictVariables,
      Locale defaultLocale,
      PebbleCache<CacheKey, Object> tagCache,
      PebbleCache<Object, PebbleTemplate> templateCache,
      ExecutorService executorService,
      Collection<? extends Extension> extensions,
      ParserOptions parserOptions,
      EvaluationOptions evaluationOptions) {

    this.loader = loader;
    this.syntax = syntax;
    this.strictVariables = strictVariables;
    this.defaultLocale = defaultLocale;
    this.tagCache = tagCache;
    this.executorService = executorService;
    this.templateCache = templateCache;
    this.extensionRegistry = new ExtensionRegistry(extensions);
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
      LexerImpl lexer = new LexerImpl(this.syntax,
          this.extensionRegistry.getUnaryOperators().values(),
          this.extensionRegistry.getBinaryOperators().values());
      TokenStream tokenStream = lexer.tokenize(templateReader, templateName);

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

    private List<Extension> userProvidedExtensions = new ArrayList<>();

    private Syntax syntax;

    private boolean strictVariables = false;

    private boolean enableNewLineTrimming = true;

    private Locale defaultLocale;

    private ExecutorService executorService;

    private PebbleCache<Object, PebbleTemplate> templateCache;

    private boolean cacheActive = true;

    private PebbleCache<CacheKey, Object> tagCache;

    private EscaperExtension escaperExtension = new EscaperExtension();

    private boolean allowGetClass;

    private boolean literalDecimalTreatedAsInteger = false;

    private boolean greedyMatchMethod = false;

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
      for (Extension extension : extensions) {
        this.userProvidedExtensions.add(extension);
      }
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
      this.escaperExtension.setAutoEscaping(autoEscaping);
      return this;
    }

    /**
     * Sets the default escaping strategy of the built-in escaper extension.
     *
     * @param strategy The name of the default escaping strategy
     * @return This builder object
     */
    public Builder defaultEscapingStrategy(String strategy) {
      this.escaperExtension.setDefaultStrategy(strategy);
      return this;
    }

    /**
     * Adds an escaping strategy to the built-in escaper extension.
     *
     * @param name     The name of the escaping strategy
     * @param strategy The strategy implementation
     * @return This builder object
     */
    public Builder addEscapingStrategy(String name, EscapingStrategy strategy) {
      this.escaperExtension.addEscapingStrategy(name, strategy);
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
     * Enable/disable getClass access for attributes
     *
     * @param allowGetClass toggle to enable/disable getClass access
     * @return This builder object
     */
    public Builder allowGetClass(boolean allowGetClass) {
      this.allowGetClass = allowGetClass;
      return this;
    }

    /**
     * Enable/disable treat literal decimal as Integer. Default is disabled, treated as Long.
     *
     * @param literalDecimalTreatedAsInteger toggle to enable/disable literal decimal treated as
     *                                       integer
     * @return This builder object
     */
    public Builder literalDecimalTreatedAsInteger(boolean literalDecimalTreatedAsInteger) {
      this.literalDecimalTreatedAsInteger = literalDecimalTreatedAsInteger;
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
     * @see com.mitchellbosecke.pebble.utils.TypeUtils#compatibleCast(Object, Class)
     */
    public Builder greedyMatchMethod(boolean greedyMatchMethod) {
      this.greedyMatchMethod = greedyMatchMethod;
      return this;
    }

    /**
     * Creates the PebbleEngine instance.
     *
     * @return A PebbleEngine object that can be used to create PebbleTemplate objects.
     */
    public PebbleEngine build() {

      // core extensions
      List<Extension> extensions = new ArrayList<>();
      extensions.add(new CoreExtension());
      extensions.add(this.escaperExtension);
      extensions.add(new I18nExtension());
      extensions.addAll(this.userProvidedExtensions);
      extensions.add(new AttributeResolverExtension());

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

      EvaluationOptions evaluationOptions = new EvaluationOptions();
      evaluationOptions.setAllowGetClass(this.allowGetClass);
      evaluationOptions.setGreedyMatchMethod(this.greedyMatchMethod);

      return new PebbleEngine(this.loader, this.syntax, this.strictVariables, this.defaultLocale,
          this.tagCache, this.templateCache,
          this.executorService, extensions, parserOptions, evaluationOptions);
    }
  }

  public EvaluationOptions getEvaluationOptions() {
    return this.evaluationOptions;
  }
}
