/*******************************************************************************
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble;

import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.mitchellbosecke.pebble.error.LoaderException;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.extension.Extension;
import com.mitchellbosecke.pebble.extension.Filter;
import com.mitchellbosecke.pebble.extension.Function;
import com.mitchellbosecke.pebble.extension.NodeVisitorFactory;
import com.mitchellbosecke.pebble.extension.Test;
import com.mitchellbosecke.pebble.extension.core.CoreExtension;
import com.mitchellbosecke.pebble.extension.escaper.EscaperExtension;
import com.mitchellbosecke.pebble.extension.i18n.I18nExtension;
import com.mitchellbosecke.pebble.lexer.LexerImpl;
import com.mitchellbosecke.pebble.lexer.Syntax;
import com.mitchellbosecke.pebble.lexer.TokenStream;
import com.mitchellbosecke.pebble.loader.ClasspathLoader;
import com.mitchellbosecke.pebble.loader.DelegatingLoader;
import com.mitchellbosecke.pebble.loader.FileLoader;
import com.mitchellbosecke.pebble.loader.Loader;
import com.mitchellbosecke.pebble.node.RootNode;
import com.mitchellbosecke.pebble.operator.BinaryOperator;
import com.mitchellbosecke.pebble.operator.UnaryOperator;
import com.mitchellbosecke.pebble.parser.Parser;
import com.mitchellbosecke.pebble.parser.ParserImpl;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;
import com.mitchellbosecke.pebble.tokenParser.TokenParser;

/**
 * The main class used for compiling templates. The PebbleEngine is responsible
 * for delegating responsibility to the lexer, parser, compiler, and template
 * cache.
 *
 * @author Mitchell
 *
 */
public class PebbleEngine {

    /*
     * Major components
     */
    private Loader<?> loader;

    private final Syntax syntax;

    /*
     * User Editable Settings
     */
    private boolean strictVariables = false;

    private Locale defaultLocale = Locale.getDefault();

    private ExecutorService executorService;

    /**
     * Template cache
     */
    private Cache<Object, PebbleTemplate> templateCache;

    /*
     * Extensions
     */
    private HashMap<Class<? extends Extension>, Extension> extensions = new HashMap<>();

    /*
     * Elements retrieved from extensions
     */
    private Map<String, TokenParser> tokenParsers = new HashMap<>();

    private Map<String, UnaryOperator> unaryOperators = new HashMap<>();

    private Map<String, BinaryOperator> binaryOperators = new HashMap<>();

    private Map<String, Filter> filters = new HashMap<>();

    private Map<String, Test> tests = new HashMap<>();

    private Map<String, Object> globalVariables = new HashMap<>();

    private Map<String, Function> functions = new HashMap<>();

    private List<NodeVisitorFactory> nodeVisitors = new ArrayList<>();

    public PebbleEngine() {
        this(null);
    }

    /**
     * Constructor for the Pebble Engine given an instantiated Loader with all
     * default extensions loaded.
     *
     * @param loader
     *            The template loader for this engine
     */
    public PebbleEngine(Loader<?> loader) {
        this(loader, new CoreExtension(), new EscaperExtension(), new I18nExtension());
    }

    /**
     * Constructor for the Pebble Engine given an instantiated Loader. This
     * method does only load those extensions listed here.
     *
     * @param loader
     *            The template loader for this engine
     * @param extensions
     *            The extensions which should be loaded.
     */
    public PebbleEngine(Loader<?> loader, Extension... extensions) {
        this(loader, Arrays.asList(extensions));
    }

    /**
     * Constructor for the Pebble Engine given an instantiated Loader. This
     * method does only load those extensions listed here.
     *
     * @param loader
     *            The template loader for this engine
     * @param extensions
     *            The extensions which should be loaded.
     * @param syntax
     *            the syntax to use for parsing the templates.
     */
    public PebbleEngine(Loader<?> loader, Syntax syntax, Extension... extensions) {
        this(loader, syntax, Arrays.asList(extensions));
    }

    /**
     * Constructor for the Pebble Engine given an instantiated Loader. This
     * method does only load those extensions listed here.
     *
     * @param loader
     *            The template loader for this engine
     * @param extensions
     *            The extensions which should be loaded.
     */
    public PebbleEngine(Loader<?> loader, Collection<? extends Extension> extensions) {
        this(loader, new Syntax.Builder().build(), extensions);
    }

    /**
     * Constructor for the Pebble Engine given an instantiated Loader. This
     * method does only load those extensions listed here.
     *
     * @param loader
     *            The template loader for this engine
     * @param syntax
     *            the syntax to use for parsing the templates.
     * @param extensions
     *            The extensions which should be loaded.
     */
    public PebbleEngine(Loader<?> loader, Syntax syntax, Collection<? extends Extension> extensions) {
        this.syntax = syntax;

        // set up a default loader if necessary
        if (loader == null) {
            List<Loader<?>> defaultLoadingStrategies = new ArrayList<>();
            defaultLoadingStrategies.add(new FileLoader());
            defaultLoadingStrategies.add(new ClasspathLoader());
            loader = new DelegatingLoader(defaultLoadingStrategies);
        }

        // set up a default cache
        templateCache = CacheBuilder.newBuilder().maximumSize(200).build();

        this.loader = loader;

        // register default extensions
        for (Extension extension : extensions) {
            this.addExtension(extension);
        }

    }

    /**
     *
     * Loads, parses, and compiles a template into an instance of PebbleTemplate
     * and returns this instance.
     *
     * @param templateName
     *            The name of the template
     * @return PebbleTemplate The compiled version of the template
     * @throws PebbleException
     *             Thrown if an error occurs while parsing the template.
     */
    public PebbleTemplate getTemplate(final String templateName) throws PebbleException {

        /*
         * template name will be null if user uses the extends tag with an
         * expression that evaluates to null
         */
        if (templateName == null) {
            return null;
        }

        if (this.loader == null) {
            throw new LoaderException(null, "Loader has not yet been specified.");
        }

        final PebbleEngine self = this;
        PebbleTemplate result = null;

        try {
            final Object cacheKey = this.loader.createCacheKey(templateName);

            result = templateCache.get(cacheKey, new Callable<PebbleTemplate>() {

                public PebbleTemplateImpl call() throws Exception {

                    LexerImpl lexer = new LexerImpl(self);
                    Reader templateReader = self.retrieveReaderFromLoader(self.loader, cacheKey);
                    TokenStream tokenStream = lexer.tokenize(templateReader, templateName);

                    Parser parser = new ParserImpl(self);
                    RootNode root = parser.parse(tokenStream);

                    PebbleTemplateImpl instance = new PebbleTemplateImpl(self, root, templateName);

                    for (NodeVisitorFactory visitorFactory : nodeVisitors) {
                        visitorFactory.createVisitor(instance).visit(root);
                    }

                    return instance;
                }
            });
        } catch (ExecutionException e) {
            /*
             * The execution exception is probably caused by a PebbleException
             * being thrown in the above Callable. We will unravel it and throw
             * the original PebbleException which is more helpful to the end
             * user.
             */
            if (e.getCause() != null && e.getCause() instanceof PebbleException) {
                throw (PebbleException) e.getCause();
            } else {
                throw new PebbleException(e, String.format("An error occurred while compiling %s", templateName));
            }
        }

        return result;
    }

    /**
     * This method calls the loader and fetches the reader. We use this method
     * to handle the generic cast.
     *
     * @param loader
     *            the loader to use fetch the reader.
     * @param cacheKey
     *            the cache key to use.
     * @return the reader object.
     * @throws LoaderException
     *             thrown when the template could not be loaded.
     */
    private <T> Reader retrieveReaderFromLoader(Loader<T> loader, Object cacheKey) throws LoaderException {
        // We make sure within getTemplate() that we use only the same key for
        // the same loader and hence we can be sure that the cast is safe.
        @SuppressWarnings("unchecked")
        T casted = (T) cacheKey;
        return loader.getReader(casted);
    }

    public void setLoader(Loader<?> loader) {

        if (this.loader != loader) {
            // When we change the loader we need to reset the cache otherwise we
            // keep eventually wrong templates in the cache.
            this.templateCache.invalidateAll();
        }

        this.loader = loader;
    }

    public Loader<?> getLoader() {
        return loader;
    }

    public void addExtension(Extension extension) {
        this.extensions.put(extension.getClass(), extension);

        // token parsers
        List<TokenParser> tokenParsers = extension.getTokenParsers();
        if (tokenParsers != null) {
            for (TokenParser tokenParser : tokenParsers) {
                this.tokenParsers.put(tokenParser.getTag(), tokenParser);
            }
        }

        // binary operators
        List<BinaryOperator> binaryOperators = extension.getBinaryOperators();
        if (binaryOperators != null) {
            for (BinaryOperator operator : binaryOperators) {
                if (!this.binaryOperators.containsKey(operator.getSymbol())) {
                    this.binaryOperators.put(operator.getSymbol(), operator);
                }
            }
        }

        // unary operators
        List<UnaryOperator> unaryOperators = extension.getUnaryOperators();
        if (unaryOperators != null) {
            for (UnaryOperator operator : unaryOperators) {
                if (!this.unaryOperators.containsKey(operator.getSymbol())) {
                    this.unaryOperators.put(operator.getSymbol(), operator);
                }
            }
        }

        // filters
        Map<String, Filter> filters = extension.getFilters();
        if (filters != null) {
            this.filters.putAll(filters);
        }

        // tests
        Map<String, Test> tests = extension.getTests();
        if (tests != null) {
            this.tests.putAll(tests);
        }

        // tests
        Map<String, Function> functions = extension.getFunctions();
        if (functions != null) {
            this.functions.putAll(functions);
        }

        // global variables
        Map<String, Object> globalVariables = extension.getGlobalVariables();
        if (globalVariables != null) {
            this.globalVariables.putAll(globalVariables);
        }

        // node visitors
        List<NodeVisitorFactory> nodeVisitors = extension.getNodeVisitors();
        if (nodeVisitors != null) {
            this.nodeVisitors.addAll(nodeVisitors);
        }

    }

    @SuppressWarnings("unchecked")
    public <T extends Extension> T getExtension(Class<T> clazz) {
        return (T) this.extensions.get(clazz);
    }

    public Map<String, TokenParser> getTokenParsers() {
        return this.tokenParsers;
    }

    public Map<String, BinaryOperator> getBinaryOperators() {
        return this.binaryOperators;
    }

    public Map<String, UnaryOperator> getUnaryOperators() {
        return this.unaryOperators;
    }

    public Map<String, Filter> getFilters() {
        return this.filters;
    }

    public Map<String, Test> getTests() {
        return this.tests;
    }

    public Map<String, Function> getFunctions() {
        return this.functions;
    }

    public Map<String, Object> getGlobalVariables() {
        return this.globalVariables;
    }

    public List<NodeVisitorFactory> getNodeVisitors() {
        return this.nodeVisitors;
    }

    public Cache<Object, PebbleTemplate> getTemplateCache() {
        return templateCache;
    }

    /**
     * Sets the cache to be used for storing compiled PebbleTemplate instances.
     *
     * @param cache
     *            The cache to be used
     */
    public void setTemplateCache(Cache<Object, PebbleTemplate> cache) {
        if (cache == null) {
            templateCache = CacheBuilder.newBuilder().maximumSize(0).build();
        } else {
            templateCache = cache;
        }
    }

    public boolean isStrictVariables() {
        return strictVariables;
    }

    /**
     * Changes the <code>strictVariables</code> setting of the PebbleEngine. If
     * strictVariables is equal to false (which is the default) then expressions
     * become much more null-safe and type issues are handled in a much more
     * graceful manner.
     *
     * @param strictVariables
     *            Whether or not strict variables is used
     */
    public void setStrictVariables(boolean strictVariables) {
        this.strictVariables = strictVariables;
    }

    public Locale getDefaultLocale() {
        return defaultLocale;
    }

    /**
     * The default locale that will be passed to each template upon compilation.
     * An individual template can be given a new locale on evaluation.
     *
     * @param locale
     *            The default locale to pass to all newly compiled templates.
     */
    public void setDefaultLocale(Locale locale) {
        this.defaultLocale = locale;
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    /**
     * Providing an ExecutorService will enable some advanced multithreading
     * features such as the parallel tag.
     *
     * @param executorService
     *            The ExecutorService to enable multithreading features.
     */
    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    /**
     * Returns the syntax which is used by this PebbleEngine.
     *
     * @return the syntax used by the PebbleEngine.
     */
    public Syntax getSyntax() {
        return this.syntax;
    }

}
