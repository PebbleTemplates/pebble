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

import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;

import com.mitchellbosecke.pebble.cache.DefaultTemplateLoadingCache;
import com.mitchellbosecke.pebble.cache.TemplateLoadingCache;
import com.mitchellbosecke.pebble.compiler.Compiler;
import com.mitchellbosecke.pebble.compiler.CompilerImpl;
import com.mitchellbosecke.pebble.error.LoaderException;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.extension.CoreExtension;
import com.mitchellbosecke.pebble.extension.EscaperExtension;
import com.mitchellbosecke.pebble.extension.Extension;
import com.mitchellbosecke.pebble.extension.Filter;
import com.mitchellbosecke.pebble.extension.I18nExtension;
import com.mitchellbosecke.pebble.extension.SimpleFunction;
import com.mitchellbosecke.pebble.extension.Test;
import com.mitchellbosecke.pebble.lexer.Lexer;
import com.mitchellbosecke.pebble.lexer.LexerImpl;
import com.mitchellbosecke.pebble.lexer.TokenStream;
import com.mitchellbosecke.pebble.loader.DefaultLoader;
import com.mitchellbosecke.pebble.loader.Loader;
import com.mitchellbosecke.pebble.node.NodeRoot;
import com.mitchellbosecke.pebble.operator.BinaryOperator;
import com.mitchellbosecke.pebble.operator.UnaryOperator;
import com.mitchellbosecke.pebble.parser.Parser;
import com.mitchellbosecke.pebble.parser.ParserImpl;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;
import com.mitchellbosecke.pebble.tokenParser.TokenParser;
import com.mitchellbosecke.pebble.tokenParser.TokenParserBroker;
import com.mitchellbosecke.pebble.tokenParser.TokenParserBrokerImpl;
import com.mitchellbosecke.pebble.utils.IOUtils;

public class PebbleEngine {

	/*
	 * Major components
	 */
	private Loader loader;
	private final Parser parser;
	private final Lexer lexer;
	private final Compiler compiler;

	/*
	 * Final Settings
	 */
	private final Class<?> templateParentClass = PebbleTemplateImpl.class;
	private final String templateClassPrefix = "PebbleTemplate";

	/*
	 * User Editable Settings
	 */
	private boolean strictVariables = false;
	private String charset = "UTF-8";
	private Locale defaultLocale = Locale.getDefault();
	private ExecutorService executorService;

	/**
	 * Template cache
	 */
	private TemplateLoadingCache loadingTemplateCache;

	/*
	 * Extensions
	 */
	private HashMap<Class<? extends Extension>, Extension> extensions = new HashMap<>();
	private boolean extensionsInitialized = false;

	/*
	 * Elements retrieved from extensions
	 */
	private TokenParserBroker tokenParserBroker;
	private Map<String, UnaryOperator> unaryOperators;
	private Map<String, BinaryOperator> binaryOperators;
	private Map<String, Filter> filters;
	private Map<String, Test> tests;
	private Map<String, Object> globalVariables;
	private Map<String, SimpleFunction> functions;

	/**
	 * compilationMutex ensures that only one template is being compiled at a
	 * time. Only concurrent evaluation is supported at this time.
	 */
	private final Semaphore compilationMutex = new Semaphore(1);

	public PebbleEngine() {
		this(new DefaultLoader());
	}

	/**
	 * Constructor for the Pebble Engine given an instantiated Loader.
	 * 
	 * @param loader
	 *            The template loader for this engine
	 */
	public PebbleEngine(Loader loader) {
		this.loader = loader;
		lexer = new LexerImpl(this);
		parser = new ParserImpl(this);
		compiler = new CompilerImpl(this);
		loadingTemplateCache = new DefaultTemplateLoadingCache();

		// register default extensions
		this.addExtension(new CoreExtension());
		this.addExtension(new EscaperExtension());
		this.addExtension(new I18nExtension());

	}

	/**
	 * 
	 * @param templateName
	 * 
	 * @return
	 * @throws PebbleException
	 */
	public PebbleTemplate compile(final String templateName) throws PebbleException {

		if (this.loader == null) {
			throw new LoaderException(null, "Loader has not yet been specified.");
		}

		final String className = this.getTemplateClassName(templateName);

		final PebbleEngine self = this;

		return loadingTemplateCache.get(className, new Callable<PebbleTemplate>() {

			public PebbleTemplateImpl call() throws InterruptedException, PebbleException {
				compilationMutex.acquire();
				PebbleTemplateImpl instance = null;

				// load it
				Reader templateReader = loader.getReader(templateName);

				/*
				 * load template into a String.
				 * 
				 * TODO: Pass the reader to the Lexer and just let the lexer
				 * iterate through the characters without having to use an
				 * intermediary string.
				 */
				String templateSource = null;
				try {
					templateSource = IOUtils.toString(templateReader);
				} catch (IOException e) {
					throw new LoaderException(e, "Could not load template");
				}

				TokenStream tokenStream = getLexer().tokenize(templateSource, templateName);
				NodeRoot root = getParser().parse(tokenStream);
				String javaSource = getCompiler().compile(root).getSource();

				// we are now done with the non-thread-safe objects, so release
				// the compilation mutex
				compilationMutex.release();

				PebbleTemplateImpl parent = null;
				if (root.hasParent()) {
					parent = (PebbleTemplateImpl) self.compile(root.getParentFileName());
				}
				instance = getCompiler().instantiateTemplate(javaSource, className, parent);

				// init blocks and macros
				instance.initBlocks();
				instance.initMacros();

				return instance;
			}
		});
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

	/**
	 * Retrieves all of the information/tools from the provided extensions. This
	 * includes unary operators, binary operations, and token parsers.
	 */
	private void initExtensions() {
		if (extensionsInitialized) {
			return;
		}
		this.extensionsInitialized = true;
		this.tokenParserBroker = new TokenParserBrokerImpl();
		this.unaryOperators = new HashMap<>();
		this.binaryOperators = new HashMap<>();
		this.filters = new HashMap<>();
		this.tests = new HashMap<>();
		this.functions = new HashMap<>();
		this.globalVariables = new HashMap<>();

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
				this.tokenParserBroker.addTokenParser(tokenParser);
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

	}

	public TokenParserBroker getTokenParserBroker() {
		if (!extensionsInitialized) {
			initExtensions();
		}
		return this.tokenParserBroker;
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

	public Map<String, SimpleFunction> getFunctions() {
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

	/**
	 * Gets the name that will be used for the final Java class when loading a
	 * particular template.
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
		return this.templateClassPrefix + classNameHash;
	}

	public Class<?> getTemplateParentClass() {
		return templateParentClass;
	}

	public TemplateLoadingCache getTemplateCache() {
		return loadingTemplateCache;
	}

	public void setTemplateCache(TemplateLoadingCache cache) {
		this.loadingTemplateCache = cache;
	}

	public boolean isStrictVariables() {
		return strictVariables;
	}

	public void setStrictVariables(boolean strictVariables) {
		this.strictVariables = strictVariables;
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public Locale getDefaultLocale() {
		return defaultLocale;
	}

	public void setDefaultLocale(Locale locale) {
		this.defaultLocale = locale;
	}

	public ExecutorService getExecutorService() {
		return executorService;
	}

	public void setExecutorService(ExecutorService executorService) {
		this.executorService = executorService;
	}
}
