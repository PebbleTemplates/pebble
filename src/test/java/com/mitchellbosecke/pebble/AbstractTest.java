package com.mitchellbosecke.pebble;

import java.util.ArrayList;
import java.util.Collection;

import com.mitchellbosecke.pebble.compiler.Compiler;
import com.mitchellbosecke.pebble.lexer.Lexer;
import com.mitchellbosecke.pebble.loader.Loader;
import com.mitchellbosecke.pebble.parser.Parser;

public abstract class AbstractTest {

	protected final PebbleEngine pebble;
	protected final Lexer lexer;
	protected final Loader loader;
	protected final Parser parser;
	protected final Compiler compiler;

	public AbstractTest() {
		Collection<String> paths = new ArrayList<>();
		paths.add("templates/");
		paths.add("misc");
		pebble = new PebbleEngine(paths);
		loader = pebble.getLoader();
		lexer = pebble.getLexer();
		parser = pebble.getParser();
		compiler = pebble.getCompiler();
	}

}
