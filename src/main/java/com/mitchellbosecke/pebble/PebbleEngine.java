/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Copyright (c) 2012 Mitchell Bosecke.
 * 
 * This work is licensed under the Creative Commons Attribution-ShareAlike 3.0 
 * Unported License. To view a copy of this license, visit 
 * http://creativecommons.org/licenses/by-sa/3.0/
 ******************************************************************************/
package com.mitchellbosecke.pebble;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.mitchellbosecke.pebble.compiler.Compiler;
import com.mitchellbosecke.pebble.compiler.CompilerImpl;
import com.mitchellbosecke.pebble.extension.CoreExtension;
import com.mitchellbosecke.pebble.extension.Extension;
import com.mitchellbosecke.pebble.filter.Filter;
import com.mitchellbosecke.pebble.lexer.Lexer;
import com.mitchellbosecke.pebble.lexer.LexerImpl;
import com.mitchellbosecke.pebble.lexer.TokenStream;
import com.mitchellbosecke.pebble.loader.Loader;
import com.mitchellbosecke.pebble.loader.ResourceLoader;
import com.mitchellbosecke.pebble.node.Node;
import com.mitchellbosecke.pebble.node.NodeRoot;
import com.mitchellbosecke.pebble.parser.Operator;
import com.mitchellbosecke.pebble.parser.Parser;
import com.mitchellbosecke.pebble.parser.ParserImpl;
import com.mitchellbosecke.pebble.template.AbstractPebbleTemplate;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import com.mitchellbosecke.pebble.test.Test;
import com.mitchellbosecke.pebble.tokenParser.TokenParser;
import com.mitchellbosecke.pebble.tokenParser.TokenParserBroker;
import com.mitchellbosecke.pebble.tokenParser.TokenParserBrokerImpl;

public class PebbleEngine {

	/*
	 * Major components
	 */
	private Loader loader;
	private Parser parser;
	private Lexer lexer;
	private Compiler compiler;

	/*
	 * Settings
	 */
	private final Class<?> templateInterfaceClass;
	private final Class<?> templateAbstractClass;
	private final String templateClassPrefix;

	/*
	 * Templates that have already been compiled into Java
	 */
	private HashMap<String, PebbleTemplate> loadedTemplates = new HashMap<>();

	/*
	 * Extensions
	 */
	private HashMap<Class<? extends Extension>, Extension> extensions = new HashMap<>();
	private boolean extensionsInitialized = false;

	/*
	 * Elements retrieved from extensions
	 */
	private TokenParserBroker tokenParserBroker;
	private Map<String, Operator> unaryOperators;
	private Map<String, Operator> binaryOperators;
	private Map<String, Filter> filters;
	private Map<String, Test> tests;

	/**
	 * Constructor for the Pebble Engine give file system paths to templates.
	 * 
	 * @param paths
	 *            File system paths where the templates are being stored
	 */
	public PebbleEngine(Collection<String> paths) {
		this(new ResourceLoader(paths));
	}

	/**
	 * Constructor for the Pebble Engine given an instantiated Loader.
	 * 
	 * @param loader
	 *            The template loader for this engine
	 */
	public PebbleEngine(Loader loader) {
		this.setLoader(loader);
		lexer = new LexerImpl(this);
		parser = new ParserImpl(this);
		compiler = new CompilerImpl(this);

		this.addExtension(new CoreExtension());

		this.templateInterfaceClass = PebbleTemplate.class;
		this.templateAbstractClass = AbstractPebbleTemplate.class;
		this.templateClassPrefix = "PebbleTemplate";
	}

	/**
	 * This will perform the entire compilation process on a given template. It
	 * will load the template from the file system, tokenize it, parse it, and
	 * then compile it to Java.
	 * 
	 * @param templateName
	 *            The name of the template to load
	 * @return An instance of the template that has been compiled into Java
	 */
	public PebbleTemplate loadTemplate(String templateName) {
		String className = this.getTemplateClassName(templateName);
		PebbleTemplate instance;
		if (loadedTemplates.containsKey(className)) {
			instance = loadedTemplates.get(className);
		} else if (!requiresCompilation(templateName)) {
			
			/* try to load class file if it has already been compiled */
			// TODO
			instance = null;
			
		} else {
			/* template has not been compiled, we must compile it */
			String templateSource = loader.getSource(templateName);
			NodeRoot root = parse(tokenize(templateSource, templateName));
			
			String javaSource = compile(root);
			
			// if this template has a parent, lets make sure the parent is compiled first
			if(root.hasParent()){
				this.loadTemplate(root.getParentFileName());
			}
			
			instance = getCompiler().compileToJava(javaSource);
			instance.setEngine(this);
			loadedTemplates.put(className, instance);
		}
		return instance;
	}

	private boolean requiresCompilation(String templateName) {
		return true;
	}

	/**
	 * Tokenizes the raw contents of a template. This is the first phase in the
	 * entire compilation process.
	 * 
	 * @param source
	 *            The raw content of the template
	 * @param filename
	 *            The name of the template (used for meaningful error messages)
	 * @return The TokenStream which is ready for parsing
	 */
	private TokenStream tokenize(String source, String filename) {
		return getLexer().tokenize(source, filename);
	}

	/**
	 * Parses a TokenStream object into an Abstract Syntax Tree (AST). This is
	 * the second phase in the entire compilation process.
	 * 
	 * @param stream
	 *            The TokenStream which is ready for parsing
	 * @return The root Node of the AST
	 */
	private NodeRoot parse(TokenStream stream) {
		return getParser().parse(stream);
	}

	/**
	 * Compiles an Abstract Syntax Tree (AST) into a Java class and returns the
	 * source code of the new Java class.
	 * 
	 * @param node
	 *            The root Node of the AST
	 * @return The source code of the new Java class
	 */
	private String compile(Node node) {
		return getCompiler().compile(node).getSource();
	}

	public Loader getLoader() {
		return loader;
	}

	public void setLoader(Loader loader) {
		this.loader = loader;
	}

	public Parser getParser() {
		return parser;
	}

	public void setParser(Parser parser) {
		this.parser = parser;
	}

	public Lexer getLexer() {
		return lexer;
	}

	public void setLexer(Lexer lexer) {
		this.lexer = lexer;
	}

	public Compiler getCompiler() {
		return compiler;
	}

	public void setCompiler(Compiler compiler) {
		this.compiler = compiler;
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

		// token parsers
		if (extension.getTokenParsers() != null) {
			for (TokenParser tokenParser : extension.getTokenParsers()) {
				this.tokenParserBroker.addTokenParser(tokenParser);
			}
		}

		// binary operators
		if (extension.getBinaryOperators() != null) {
			for (Operator operator : extension.getBinaryOperators()) {
				if (!this.binaryOperators.containsKey(operator.getSymbol())) {
					this.binaryOperators.put(operator.getSymbol(), operator);
				}
			}
		}

		// unary operators
		if (extension.getUnaryOperators() != null) {
			for (Operator operator : extension.getUnaryOperators()) {
				if (!this.unaryOperators.containsKey(operator.getSymbol())) {
					this.unaryOperators.put(operator.getSymbol(), operator);
				}
			}
		}

		// filters
		if (extension.getFilters() != null) {
			for (Filter filter : extension.getFilters()) {
				this.filters.put(filter.getTag(), filter);
			}
		}

		// tests
		if (extension.getTests() != null) {
			for (Test test : extension.getTests()) {
				this.tests.put(test.getTag(), test);
			}
		}

	}

	public TokenParserBroker getTokenParserBroker() {
		if (!extensionsInitialized) {
			initExtensions();
		}
		return this.tokenParserBroker;
	}

	public Map<String, Operator> getBinaryOperators() {
		if (!this.extensionsInitialized) {
			initExtensions();
		}
		return this.binaryOperators;
	}

	public Map<String, Operator> getUnaryOperators() {
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

	/**
	 * Gets the name that will be used for the final Java class when loading a
	 * particular template.
	 * 
	 * @param templateName
	 *            The template that we need a name for
	 * @return The final name that would be used for creating a Java class
	 */
	public String getTemplateClassName(String templateName) {
		
		// if tempalteName is part of a directory path, get just the last segment
		templateName = templateName.replaceFirst(".*/([^/]+).*", "$1");
		
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

	public Class<?> getTemplateInterfaceClass() {
		return templateInterfaceClass;
	}

	public Class<?> getTemplateAbstractClass() {
		return templateAbstractClass;
	}
}
