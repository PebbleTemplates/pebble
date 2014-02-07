/*******************************************************************************
 * Copyright (c) 2013 by Mitchell Bösecke
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble;

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

		// main testing engine uses all default settings
		pebble = new PebbleEngine();
		pebble.getLoader().setPrefix("templates");

		loader = pebble.getLoader();
		lexer = pebble.getLexer();
		parser = pebble.getParser();
		compiler = pebble.getCompiler();
	}

}
