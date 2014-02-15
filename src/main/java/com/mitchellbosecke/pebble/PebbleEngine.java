/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Original work Copyright (c) 2009-2013 by the Twig Team
 * Modified work Copyright (c) 2013 by Mitchell Bösecke
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble;

import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.mitchellbosecke.pebble.compiler.Compiler;
import com.mitchellbosecke.pebble.compiler.CompilerImpl;
import com.mitchellbosecke.pebble.compiler.JavaCompiler;
import com.mitchellbosecke.pebble.error.LoaderException;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.extension.Extension;
import com.mitchellbosecke.pebble.extension.Filter;
import com.mitchellbosecke.pebble.extension.Function;
import com.mitchellbosecke.pebble.extension.NodeVisitor;
import com.mitchellbosecke.pebble.extension.Test;
import com.mitchellbosecke.pebble.extension.core.CoreExtension;
import com.mitchellbosecke.pebble.extension.escaper.EscaperExtension;
import com.mitchellbosecke.pebble.extension.i18n.I18nExtension;
import com.mitchellbosecke.pebble.lexer.Lexer;
import com.mitchellbosecke.pebble.lexer.LexerImpl;
import com.mitchellbosecke.pebble.lexer.TokenStream;
import com.mitchellbosecke.pebble.loader.ClasspathLoader;
import com.mitchellbosecke.pebble.loader.DelegatingLoader;
import com.mitchellbosecke.pebble.loader.FileLoader;
import com.mitchellbosecke.pebble.loader.Loader;
import com.mitchellbosecke.pebble.node.NodeRoot;
import com.mitchellbosecke.pebble.operator.BinaryOperator;
import com.mitchellbosecke.pebble.operator.UnaryOperator;
import com.mitchellbosecke.pebble.parser.Parser;
import com.mitchellbosecke.pebble.parser.ParserImpl;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;
import com.mitchellbosecke.pebble.tokenParser.TokenParser;
import com.mitchellbosecke.pebble.utils.IOUtils;

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
	private Loader loader;
	private final Parser parser;
	private final Lexer lexer;
	private final Compiler compiler;

	/*
	 * User Editable Settings
	 */
	private boolean strictVariables = false;
	private Locale defaultLocale = Locale.getDefault();
	private ExecutorService executorService;

	/**
	 * Template cache
	 */
	private Cache<String, PebbleTemplate> templateCache;

	/*
	 * Extensions
	 */
	private HashMap<Class<? extends Extension>, Extension> extensions = new HashMap<>();
	private boolean extensionsInitialized = false;

	/*
	 * Elements retrieved from extensions
	 */
	private Map<String, TokenParser> tokenParsers;
	private Map<String, UnaryOperator> unaryOperators;
	private Map<String, BinaryOperator> binaryOperators;
	private Map<String, Filter> filters;
	private Map<String, Test> tests;
	private Map<String, Object> globalVariables;
	private Map<String, Function> functions;
	private List<NodeVisitor> nodeVisitors;

	/**
	 * compilationMutex ensures that only one template is being compiled at a
	 * time. Only concurrent evaluation is supported at this time.
	 */
	private final Semaphore compilationMutex = new Semaphore(1);

	public PebbleEngine() {
		this(null);
	}

	/**
	 * Constructor for the Pebble Engine given an instantiated Loader.
	 * 
	 * @param loader
	 *            The template loader for this engine
	 */
	public PebbleEngine(Loader loader) {

		// set up a default loader if necessary
		if (loader == null) {
			List<Loader> defaultLoadingStrategies = new ArrayList<>();
			defaultLoadingStrategies.add(new ClasspathLoader());
			defaultLoadingStrategies.add(new FileLoader());
			loader = new DelegatingLoader(defaultLoadingStrategies);
		}

		// set up a default cache
		templateCache = CacheBuilder.newBuilder().maximumSize(200).build();

		this.loader = loader;
		lexer = new LexerImpl(this);
		parser = new ParserImpl(this);
		compiler = new CompilerImpl(this);

		// register default extensions
		this.addExtension(new CoreExtension());
		this.addExtension(new EscaperExtension());
		this.addExtension(new I18nExtension());

	}

	/**
	 * 
	 * Loads, parses, and compiles a template into an instance of PebbleTemplate
	 * and returns this instance.
	 * 
	 * @param templateName
	 * @return PebbleTemplate
	 * @throws PebbleException
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
		final String className = this.getTemplateClassName(templateName);
		PebbleTemplate result = null;

		try {

			result = templateCache.get(className, new Callable<PebbleTemplate>() {

				public PebbleTemplateImpl call() throws Exception {

					compilationMutex.acquire();

					PebbleTemplateImpl instance = null;
					String javaSource = null;

					try {

						Reader templateReader = loader.getReader(templateName);

						/*
						 * TODO: Pass the reader to the Lexer and just let the
						 * lexer iterate through the characters without having
						 * to use an intermediary string.
						 */
						String templateSource = IOUtils.toString(templateReader);

						TokenStream tokenStream = getLexer().tokenize(templateSource, templateName);
						NodeRoot root = getParser().parse(tokenStream);
						javaSource = getCompiler().compile(root).getSource();

					} finally {
						compilationMutex.release();
					}

					instance = JavaCompiler.compile(self, javaSource, className);
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

		// init blocks and macros
		PebbleTemplateImpl initializedTemplate = (PebbleTemplateImpl) result;

		return initializedTemplate;
	}

	public void setLoader(Loader loader) {
		this.loader = loader;
	}

	public Loader getLoader() {
		return loader;
	}

	public Parser getParser() {
		return parser;
	}

	public Lexer getLexer() {
		return lexer;
	}

	public Compiler getCompiler() {
		return compiler;
	}

	public void addExtension(Extension extension) {
		this.extensions.put(extension.getClass(), extension);
	}

	@SuppressWarnings("unchecked")
	public <T extends Extension> T getExtension(Class<T> clazz) {
		return (T) this.extensions.get(clazz);
	}

	/**
	 * Retrieves all of the information/tools from the provided extensions. This
	 * includes unary operators, binary operations, token parsers, etc
	 */
	private void initExtensions() {
		if (extensionsInitialized) {
			return;
		}
		this.extensionsInitialized = true;
		this.tokenParsers = new HashMap<>();
		this.unaryOperators = new HashMap<>();
		this.binaryOperators = new HashMap<>();
		this.filters = new HashMap<>();
		this.tests = new HashMap<>();
		this.functions = new HashMap<>();
		this.globalVariables = new HashMap<>();
		this.nodeVisitors = new ArrayList<>();

		for (Extension extension : this.extensions.values()) {
			initExtension(extension);
		}
	}

	/**
	 * Initializes a particular extension and retrieves all valuable information
	 * from it.
	 * 
	 * @param extension
	 *            The extension to initialize
	 */
	private void initExtension(Extension extension) {

		extension.initRuntime(this);

		// token parsers
		if (extension.getTokenParsers() != null) {
			for (TokenParser tokenParser : extension.getTokenParsers()) {
				this.tokenParsers.put(tokenParser.getTag(), tokenParser);
			}
		}

		// binary operators
		if (extension.getBinaryOperators() != null) {
			for (BinaryOperator operator : extension.getBinaryOperators()) {
				if (!this.binaryOperators.containsKey(operator.getSymbol())) {
					this.binaryOperators.put(operator.getSymbol(), operator);
				}
			}
		}

		// unary operators
		if (extension.getUnaryOperators() != null) {
			for (UnaryOperator operator : extension.getUnaryOperators()) {
				if (!this.unaryOperators.containsKey(operator.getSymbol())) {
					this.unaryOperators.put(operator.getSymbol(), operator);
				}
			}
		}

		// filters
		if (extension.getFilters() != null) {
			this.filters.putAll(extension.getFilters());
		}

		// tests
		if (extension.getTests() != null) {
			this.tests.putAll(extension.getTests());
		}

		// tests
		if (extension.getFunctions() != null) {
			this.functions.putAll(extension.getFunctions());
		}

		// global variables
		if (extension.getGlobalVariables() != null) {
			this.globalVariables.putAll(extension.getGlobalVariables());
		}

		// node visitors
		if (extension.getNodeVisitors() != null) {
			this.nodeVisitors.addAll(extension.getNodeVisitors());
		}

	}

	public Map<String, TokenParser> getTokenParsers() {
		if (!extensionsInitialized) {
			initExtensions();
		}
		return this.tokenParsers;
	}

	public Map<String, BinaryOperator> getBinaryOperators() {
		if (!this.extensionsInitialized) {
			initExtensions();
		}
		return this.binaryOperators;
	}

	public Map<String, UnaryOperator> getUnaryOperators() {
		if (!this.extensionsInitialized) {
			initExtensions();
		}
		return this.unaryOperators;
	}

	public Map<String, Filter> getFilters() {
		if (!this.extensionsInitialized) {
			initExtensions();
		}
		return this.filters;
	}

	public Map<String, Test> getTests() {
		if (!this.extensionsInitialized) {
			initExtensions();
		}
		return this.tests;
	}

	public Map<String, Function> getFunctions() {
		if (!this.extensionsInitialized) {
			initExtensions();
		}
		return this.functions;
	}

	public Map<String, Object> getGlobalVariables() {
		if (!this.extensionsInitialized) {
			initExtensions();
		}
		return this.globalVariables;
	}

	public List<NodeVisitor> getNodeVisitors() {
		if (!this.extensionsInitialized) {
			initExtensions();
		}
		return this.nodeVisitors;
	}

	/**
	 * Gets the name that will be used for the final compiled Java class.
	 * 
	 * @param templateName
	 *            The template that we need a name for
	 * @return The final name that would be used for creating a Java class
	 */
	public String getTemplateClassName(String templateName) {

		String classNameHash = "";
		byte[] bytesOfName;
		MessageDigest md;
		try {
			bytesOfName = templateName.getBytes("UTF-8");
			md = MessageDigest.getInstance("MD5");
			md.update(bytesOfName);
			byte[] hash = md.digest();

			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < hash.length; i++) {
				sb.append(Integer.toString((hash[i] & 0xff) + 0x100, 16).substring(1));
			}

			classNameHash = sb.toString();
		} catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
			// should not be here
			e.printStackTrace();
		}
		return "PebbleTemplate" + classNameHash;
	}

	public Cache<String, PebbleTemplate> getTemplateCache() {
		return templateCache;
	}

	/**
	 * Sets the cache to be used for storing compiled PebbleTemplate instances.
	 * 
	 * @param cache
	 *            The cache to be used
	 */
	public void setTemplateCache(Cache<String, PebbleTemplate> cache) {
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
}
