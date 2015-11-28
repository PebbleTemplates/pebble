/*******************************************************************************
 * This file is part of Pebble.
 * <p>
 * Copyright (c) 2014 by Mitchell Bösecke
 * <p>
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.mitchellbosecke.pebble.cache.BaseTagCacheKey;
import com.mitchellbosecke.pebble.error.LoaderException;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.extension.Extension;
import com.mitchellbosecke.pebble.extension.ExtensionRegistry;
import com.mitchellbosecke.pebble.extension.NodeVisitorFactory;
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
import com.mitchellbosecke.pebble.node.RootNode;
import com.mitchellbosecke.pebble.parser.Parser;
import com.mitchellbosecke.pebble.parser.ParserImpl;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;

import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

/**
 * The main class used for compiling templates. The PebbleEngine is responsible
 * for delegating responsibility to the lexer, parser, compiler, and template
 * cache.
 *
 * @author Mitchell
 */
public class PebbleEngine {

    private final Loader<?> loader;

    private final Syntax syntax;

    private final boolean strictVariables;

    private final Locale defaultLocale;

    private final Cache<BaseTagCacheKey, Object> tagCache;

    private final ExecutorService executorService;

    private final Cache<Object, PebbleTemplate> templateCache;

    private final ExtensionRegistry extensionRegistry;

    /**
     * Constructor for the Pebble Engine given an instantiated Loader. This
     * method does only load those extensions listed here.
     *
     * @param loader     The template loader for this engine
     * @param syntax     the syntax to use for parsing the templates.
     * @param extensions The extensions which should be loaded.
     */
    private PebbleEngine(Loader<?> loader, Syntax syntax, boolean strictVariables, Locale defaultLocale,
            Cache<BaseTagCacheKey, Object> tagCache, Cache<Object, PebbleTemplate> templateCache,
            ExecutorService executorService, Collection<? extends Extension> extensions) {

        this.loader = loader;
        this.syntax = syntax;
        this.strictVariables = strictVariables;
        this.defaultLocale = defaultLocale;
        this.tagCache = tagCache;
        this.executorService = executorService;
        this.templateCache = templateCache;
        this.extensionRegistry = new ExtensionRegistry(extensions);
    }

    /**
     * Loads, parses, and compiles a template into an instance of PebbleTemplate
     * and returns this instance.
     *
     * @param templateName The name of the template
     * @return PebbleTemplate The compiled version of the template
     * @throws PebbleException Thrown if an error occurs while parsing the template.
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
        PebbleTemplate result;

        try {
            final Object cacheKey = this.loader.createCacheKey(templateName);

            result = templateCache.get(cacheKey, new Callable<PebbleTemplate>() {

                public PebbleTemplateImpl call() throws Exception {

                    LexerImpl lexer = new LexerImpl(syntax, extensionRegistry.getUnaryOperators().values(),
                            extensionRegistry.getBinaryOperators().values());
                    Reader templateReader = self.retrieveReaderFromLoader(self.loader, cacheKey);
                    TokenStream tokenStream = lexer.tokenize(templateReader, templateName);

                    Parser parser = new ParserImpl(extensionRegistry.getUnaryOperators(),
                            extensionRegistry.getBinaryOperators(), extensionRegistry.getTokenParsers());
                    RootNode root = parser.parse(tokenStream);

                    PebbleTemplateImpl instance = new PebbleTemplateImpl(self, root, templateName);

                    for (NodeVisitorFactory visitorFactory : extensionRegistry.getNodeVisitors()) {
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
     * @param loader   the loader to use fetch the reader.
     * @param cacheKey the cache key to use.
     * @return the reader object.
     * @throws LoaderException thrown when the template could not be loaded.
     */
    private <T> Reader retrieveReaderFromLoader(Loader<T> loader, Object cacheKey) throws LoaderException {
        // We make sure within getTemplate() that we use only the same key for
        // the same loader and hence we can be sure that the cast is safe.
        @SuppressWarnings("unchecked")
        T casted = (T) cacheKey;
        return loader.getReader(casted);
    }

    /**
     * Returns the loader
     *
     * @return
     */
    public Loader<?> getLoader() {
        return loader;
    }

    /**
     * Returns the template cache
     *
     * @return
     */
    public Cache<Object, PebbleTemplate> getTemplateCache() {
        return templateCache;
    }

    /**
     * Returns the strict variables setting
     *
     * @return
     */
    public boolean isStrictVariables() {
        return strictVariables;
    }

    /**
     * Returns the default locale
     *
     * @return
     */
    public Locale getDefaultLocale() {
        return defaultLocale;
    }

    /**
     * Returns the executor service
     *
     * @return
     */
    public ExecutorService getExecutorService() {
        return executorService;
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
     * @return
     */
    public ExtensionRegistry getExtensionRegistry() {
        return extensionRegistry;
    }

    /**
     * Returns the tag cache
     *
     * @return
     */
    public Cache<BaseTagCacheKey, Object> getTagCache() {
        return this.tagCache;
    }

    /**
     * A builder to configure and construct an instance of a PebbleEngine.
     */
    public static class Builder {

        private Loader<?> loader;

        private List<Extension> extensions = new ArrayList<>();

        private Syntax syntax = new Syntax.Builder().build();

        /**
         * Changes the <code>strictVariables</code> setting of the PebbleEngine. If
         * strictVariables is equal to false (which is the default) then expressions
         * become much more null-safe and type issues are handled in a much more
         * graceful manner.
         *
         * @param strictVariables Whether or not strict variables is used
         */
        private boolean strictVariables = false;

        /**
         * The default locale that will be passed to each template upon compilation.
         * An individual template can be given a new locale on evaluation.
         *
         * @param locale The default locale to pass to all newly compiled templates.
         */
        private Locale defaultLocale;

        /**
         * Providing an ExecutorService will enable some advanced multithreading
         * features such as the parallel tag.
         *
         * @param executorService The ExecutorService to enable multithreading features.
         */
        private ExecutorService executorService;

        /**
         * Sets the cache to be used for storing compiled PebbleTemplate instances.
         *
         * @param cache The cache to be used
         */
        private Cache<Object, PebbleTemplate> templateCache;

        private Cache<BaseTagCacheKey, Object> tagCache;

        private EscaperExtension escaperExtension = new EscaperExtension();

        public Builder() {

        }

        public Builder loader(Loader<?> loader) {
            this.loader = loader;
            return this;
        }

        public Builder extension(Extension... extensions) {
            for (Extension extension : extensions) {
                this.extensions.add(extension);
            }
            return this;
        }

        public Builder syntax(Syntax syntax) {
            this.syntax = syntax;
            return this;
        }

        public Builder strictVariables(boolean strictVariables) {
            this.strictVariables = strictVariables;
            return this;
        }

        public Builder defaultLocale(Locale defaultLocale) {
            this.defaultLocale = defaultLocale;
            return this;
        }

        public Builder executorService(ExecutorService executorService) {
            this.executorService = executorService;
            return this;
        }

        public Builder templateCache(Cache<Object, PebbleTemplate> templateCache) {
            if (templateCache == null) {
                this.templateCache = CacheBuilder.newBuilder().maximumSize(0).build();
            } else {
                this.templateCache = templateCache;
            }
            return this;
        }

        public Builder tagCache(Cache<BaseTagCacheKey, Object> tagCache) {
            if (tagCache == null) {
                this.tagCache = CacheBuilder.newBuilder().maximumSize(0).build();
            } else {
                this.tagCache = tagCache;
            }
            return this;
        }

        public Builder autoEscaping(boolean autoEscaping){
            escaperExtension.setAutoEscaping(autoEscaping);
            return this;
        }

        public Builder defaultEscapingStrategy(String strategy){
            escaperExtension.setDefaultStrategy(strategy);
            return this;
        }

        public Builder addEscaperSafeFilter(String filter){
            escaperExtension.addSafeFilter(filter);
            return this;
        }

        public Builder addEscapingStrategy(String name, EscapingStrategy strategy){
            escaperExtension.addEscapingStrategy(name, strategy);
            return this;
        }

        public PebbleEngine build() {

            // core extensions
            List<Extension> extensions = new ArrayList<>();
            extensions.add(new CoreExtension());
            extensions.add(escaperExtension);
            extensions.add(new I18nExtension());
            extensions.addAll(this.extensions);

            // default loader
            if(loader == null) {
                List<Loader<?>> defaultLoadingStrategies = new ArrayList<>();
                defaultLoadingStrategies.add(new ClasspathLoader());
                defaultLoadingStrategies.add(new FileLoader());
                loader = new DelegatingLoader(defaultLoadingStrategies);
            }

            // default locale
            if(defaultLocale == null) {
                defaultLocale = Locale.getDefault();
            }

            // default caches
            if(templateCache == null) {
                templateCache = CacheBuilder.newBuilder().maximumSize(200).build();
            }

            if(tagCache == null) {
                tagCache = CacheBuilder.newBuilder().maximumSize(200).build();
            }

            return new PebbleEngine(loader, syntax, strictVariables, defaultLocale, tagCache, templateCache,
                    executorService, extensions);
        }

    }
}
