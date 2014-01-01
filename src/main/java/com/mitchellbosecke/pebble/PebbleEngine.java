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

import org.apache.commons.io.IOUtils;

import com.mitchellbosecke.pebble.compiler.Compiler;
import com.mitchellbosecke.pebble.compiler.CompilerImpl;
import com.mitchellbosecke.pebble.compiler.InMemoryForwardingFileManager;
import com.mitchellbosecke.pebble.error.LoaderException;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.error.SyntaxException;
import com.mitchellbosecke.pebble.extension.CoreExtension;
import com.mitchellbosecke.pebble.extension.EscaperExtension;
import com.mitchellbosecke.pebble.extension.Extension;
import com.mitchellbosecke.pebble.extension.Filter;
import com.mitchellbosecke.pebble.extension.SimpleFunction;
import com.mitchellbosecke.pebble.extension.Test;
import com.mitchellbosecke.pebble.lexer.Lexer;
import com.mitchellbosecke.pebble.lexer.LexerImpl;
import com.mitchellbosecke.pebble.lexer.TokenStream;
import com.mitchellbosecke.pebble.loader.Loader;
import com.mitchellbosecke.pebble.loader.PebbleDefaultLoader;
import com.mitchellbosecke.pebble.node.NodeRoot;
import com.mitchellbosecke.pebble.operator.BinaryOperator;
import com.mitchellbosecke.pebble.operator.UnaryOperator;
import com.mitchellbosecke.pebble.parser.Parser;
import com.mitchellbosecke.pebble.parser.ParserImpl;
import com.mitchellbosecke.pebble.template.AbstractPebbleTemplate;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import com.mitchellbosecke.pebble.tokenParser.TokenParser;
import com.mitchellbosecke.pebble.tokenParser.TokenParserBroker;
import com.mitchellbosecke.pebble.tokenParser.TokenParserBrokerImpl;

public class PebbleEngine {

	/*
	 * Major components
	 */
	private Loader loader;
	private final Parser parser;
	private final Lexer lexer;
	private final Compiler compiler;
	private final InMemoryForwardingFileManager fileManager;

	/*
	 * Final Settings
	 */
	private final Class<?> templateInterfaceClass = PebbleTemplate.class;
	private final Class<?> templateAbstractClass = AbstractPebbleTemplate.class;
	private final String templateClassPrefix = "PebbleTemplate";

	/*
	 * User Editable Settings
	 */
	private boolean cacheTemplates = true;
	private boolean strictVariables = true;
	private String charset = "UTF-8";
	private Locale defaultLocale = Locale.getDefault();

	/*
	 * Templates that have already been compiled into Java
	 */
	private HashMap<String, PebbleTemplate> cachedTemplates = new HashMap<>();

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

	public PebbleEngine() {
		this(new PebbleDefaultLoader());
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
		fileManager = new InMemoryForwardingFileManager();

		// register default extensions
		this.addExtension(new CoreExtension());
		this.addExtension(new EscaperExtension());

	}

	/**
	 * This will perform the entire compilation process on a given template. It
	 * will load the template from the file system, tokenize it, parse it, and
	 * then compile it to Java.
	 * 
	 * @param templateName
	 *            The name of the template to load
	 * @return An instance of the template that has been compiled into Java
	 * @throws SyntaxException
	 * @throws LoaderException
	 */
	public PebbleTemplate compile(String templateName) throws SyntaxException, LoaderException, PebbleException {
		return compile(templateName, true);
	}

	/**
	 * 
	 * @param templateName
	 * @param isPrimary
	 *            Whether the template is the bottom template in a chain of
	 *            inheritance. If it isn't, i.e. it is a parent template, then
	 *            we do not check the cache and we avoid clearing the file
	 *            manager.
	 * @return
	 * @throws SyntaxException
	 * @throws LoaderException
	 * @throws PebbleException
	 */
	private PebbleTemplate compile(String templateName, boolean isPrimary) throws SyntaxException, LoaderException,
			PebbleException {
		if (this.loader == null) {
			throw new LoaderException("Loader has not yet been specified.");
		}

		String className = this.getTemplateClassName(templateName);
		PebbleTemplate instance;

		loader.setCharset(charset);

		if (isPrimary && cacheTemplates && cachedTemplates.containsKey(className)) {
			instance = cachedTemplates.get(className);
		} else {
			/* template has not been compiled, we must compile it */

			// load it
			Reader templateReader = loader.getReader(templateName);
			String templateSource = "";
			try {
				templateSource = IOUtils.toString(templateReader);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			TokenStream tokenStream = getLexer().tokenize(templateSource, templateName);
			NodeRoot root = getParser().parse(tokenStream);
			String javaSource = getCompiler().compile(root).getSource();

			// if this template has a parent, lets make sure the parent is
			// compiled first
			if (root.hasParent()) {
				this.compile(root.getParentFileName(), false);
			}

			instance = getCompiler().compileToJava(javaSource, className);
			
			// give the template some KNOWLEDGE
			instance.setEngine(this);
			instance.setGeneratedJavaCode(javaSource);
			instance.setSource(templateSource);
			instance.setLocale(defaultLocale);

			if (isPrimary) {
				cachedTemplates.put(className, instance);
			}
		}
		if (isPrimary) {
			fileManager.clear();
		}
		return instance;
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

	public InMemoryForwardingFileManager getFileManager() {
		return fileManager;
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
			for (Filter filter : extension.getFilters()) {
				this.filters.put(filter.getName(), filter);
			}
		}

		// tests
		if (extension.getTests() != null) {
			for (Test test : extension.getTests()) {
				this.tests.put(test.getName(), test);
			}
		}

		// tests
		if (extension.getFunctions() != null) {
			for (SimpleFunction function : extension.getFunctions()) {
				this.functions.put(function.getName(), function);
			}
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
			bytesOfName = templateName.getBytes(getCharset());
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

	public Class<?> getTemplateInterfaceClass() {
		return templateInterfaceClass;
	}

	public Class<?> getTemplateAbstractClass() {
		return templateAbstractClass;
	}

	public boolean isCacheTemplates() {
		return cacheTemplates;
	}

	public void setCacheTemplates(boolean cacheTemplates) {
		this.cacheTemplates = cacheTemplates;
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
	
	public Locale getDefaultLocale(){
		return defaultLocale;
	}
	
	public void setDefaultLocale(Locale locale){
		this.defaultLocale = locale;
	}
}
