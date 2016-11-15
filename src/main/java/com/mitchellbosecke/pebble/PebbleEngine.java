/*******************************************************************************
 * This file is part of Pebble.
 * <p>
 * Copyright (c) 2014 by Mitchell Bösecke
 * <p>
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble;


import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.mitchellbosecke.pebble.cache.BaseTagCacheKey;
import com.mitchellbosecke.pebble.error.LoaderException;
import com.mitchellbosecke.pebble.error.ParserException;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.error.RuntimePebbleException;
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
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;

import static java.util.Objects.isNull;

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
     * method does only load those userProvidedExtensions listed here.
     *
     * @param loader     The template loader for this engine
     * @param syntax     the syntax to use for parsing the templates.
     * @param extensions The userProvidedExtensions which should be loaded.
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

            if(isNull(templateCache)){
                result = getPebbleTemplate(self, templateName, cacheKey);
            }
            else {
                result = templateCache.get(cacheKey, k -> {
                    try {
                        return getPebbleTemplate(self, templateName, cacheKey);
                    } catch (PebbleException e) {
                        throw new RuntimePebbleException(e);
                    }
                });
            }
        } catch (CompletionException e) {
            /*
             * The completion exception is probably caused by a PebbleException
             * being thrown in the above function. We will unravel it and throw
             * the original PebbleException which is more helpful to the end
             * user.
             */
            if (e.getCause() != null && e.getCause() instanceof RuntimePebbleException) {
                RuntimePebbleException runtimePebbleException = (RuntimePebbleException) e.getCause();
                throw (PebbleException) runtimePebbleException.getCause();
            } else {
                throw new PebbleException(e, String.format("An error occurred while compiling %s", templateName));
            }
        }

        return result;
    }

    private PebbleTemplate getPebbleTemplate(final PebbleEngine self, final String templateName, final Object cacheKey) throws LoaderException, ParserException {
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
     * @return The loader
     */
    public Loader<?> getLoader() {
        return loader;
    }

    /**
     * Returns the template cache
     *
     * @return The template cache
     */
    public Cache<Object, PebbleTemplate> getTemplateCache() {
        return templateCache;
    }

    /**
     * Returns the strict variables setting
     *
     * @return The strict variables setting
     */
    public boolean isStrictVariables() {
        return strictVariables;
    }

    /**
     * Returns the default locale
     *
     * @return The default locale
     */
    public Locale getDefaultLocale() {
        return defaultLocale;
    }

    /**
     * Returns the executor service
     *
     * @return The executor service
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
     * @return The extension registry
     */
    public ExtensionRegistry getExtensionRegistry() {
        return extensionRegistry;
    }

    /**
     * Returns the tag cache
     *
     * @return The tag cache
     */
    public Cache<BaseTagCacheKey, Object> getTagCache() {
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

        private Cache<Object, PebbleTemplate> templateCache;

        private boolean cacheActive = true;

        private Cache<BaseTagCacheKey, Object> tagCache;

        private EscaperExtension escaperExtension = new EscaperExtension();

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
         * Changes the <code>strictVariables</code> setting of the PebbleEngine.
         * The default value of this setting is "false".
         * <p>
         * The following examples will all print empty strings if strictVariables
         * is false but will throw exceptions if it is true:
         * </p>
         * {{ nonExistingVariable }}
         * {{ nonExistingVariable.attribute }}
         * {{ nullVariable.attribute }}
         * {{ existingVariable.nullAttribute.attribute }}
         * {{ existingVariable.nonExistingAttribute }}
         * {{ array[-1] }}
         *
         * @param strictVariables Whether or not strict variables is used
         * @return This builder object
         */
        public Builder strictVariables(boolean strictVariables) {
            this.strictVariables = strictVariables;
            return this;
        }

        /**
         * Changes the <code>newLineTrimming</code> setting of the PebbleEngine.
         * The default value of this setting is "true".
         * <p>
         *      By default, Pebble will trim a newline that immediately follows
         *      a Pebble tag. For example, <code>{{key1}}\n{{key2}}</code> will
         *      have the newline removed.
         * </p>
         * <p>
         * If <code>newLineTrimming</code> is set to false, then the
         * first newline following a Pebble tag won't be trimmed.  All newlines
         * will be preserved
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
        public Builder templateCache(Cache<Object, PebbleTemplate> templateCache) {
            this.templateCache = templateCache;
            return this;
        }

        /**
         * Sets the cache used by the "cache" tag.
         *
         * @param tagCache The tag cache
         * @return This builder object
         */
        public Builder tagCache(Cache<BaseTagCacheKey, Object> tagCache) {
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
            escaperExtension.setAutoEscaping(autoEscaping);
            return this;
        }

        /**
         * Sets the default escaping strategy of the built-in escaper extension.
         *
         * @param strategy The name of the default escaping strategy
         * @return This builder object
         */
        public Builder defaultEscapingStrategy(String strategy) {
            escaperExtension.setDefaultStrategy(strategy);
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
            escaperExtension.addEscapingStrategy(name, strategy);
            return this;
        }

        /**
         * Enable/disable all caches, i.e. cache used by the engine to store compiled PebbleTemplate instances
         * and tags cache
         *
         * @param cacheActive toggle to enable/disable all caches
         * @return This builder object
         */
        public Builder cacheActive(boolean cacheActive) {
            this.cacheActive = cacheActive;
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
            extensions.add(escaperExtension);
            extensions.add(new I18nExtension());
            extensions.addAll(this.userProvidedExtensions);

            // default loader
            if (loader == null) {
                List<Loader<?>> defaultLoadingStrategies = new ArrayList<>();
                defaultLoadingStrategies.add(new ClasspathLoader());
                defaultLoadingStrategies.add(new FileLoader());
                loader = new DelegatingLoader(defaultLoadingStrategies);
            }

            // default locale
            if (defaultLocale == null) {
                defaultLocale = Locale.getDefault();
            }


            if (cacheActive) {
                // default caches
                if (templateCache == null) {
                    templateCache = Caffeine.newBuilder().maximumSize(200).build();
                }

                if (tagCache == null) {
                    tagCache = Caffeine.newBuilder().maximumSize(200).build();
                }
            } else {
                templateCache = null;
                tagCache = null;
            }

            if(syntax == null) {
                syntax = new Syntax.Builder().setEnableNewLineTrimming(enableNewLineTrimming).build();
            }

            return new PebbleEngine(loader, syntax, strictVariables, defaultLocale, tagCache, templateCache,
                    executorService, extensions);
        }
    }
}
