/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Original work Copyright (c) 2009-2013 by the Twig Team
 * Modified work Copyright (c) 2013 by Mitchell Bösecke
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.compiler;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.node.Node;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

public interface Compiler {

	/**
	 * Typically used just for debugging purposes.
	 * 
	 * @return The source code generated so far.
	 */
	public String getSource();

	/**
	 * Will write a line of code with indentation added to the beginning.
	 * 
	 * @param string
	 *            Java code which will be prefixed with indentation.
	 * @return this
	 */
	public Compiler write(String string);

	/**
	 * Will write a line of code without prefixing with indentation.
	 * 
	 * @param string
	 *            Java code which will be written as-is.
	 * @return this
	 */
	public Compiler raw(String string);

	/**
	 * This method will escape all necessary characters within the input text,
	 * surround the input text with double quotation marks and then write the
	 * final Java-safe String to the compilation output.
	 * 
	 * @param text
	 *            Text which will be converted to a proper Java string.
	 * @return this
	 */
	public Compiler string(String text);

	/**
	 * Increase the indentation level by one. The indentation level will stay
	 * increased until the outdent() method is called.
	 * 
	 * @return this
	 */
	public Compiler indent();

	/**
	 * Decrease the indentation level by one.
	 * 
	 * @return this
	 */
	public Compiler outdent();

	/**
	 * Starts a brand new compilation. This should ONLY be called by the main
	 * PebbleEngine. It should not be used by any TokenParsers, instead use
	 * subcompile(Node node).
	 * 
	 * @param node
	 *            Root node to start the compilation process.
	 * 
	 * @return this
	 */
	public Compiler compile(Node node);

	/**
	 * Compiles an individual node. It is the responsibility of the node to
	 * compile any children nodes it might have.
	 * 
	 * @param node
	 *            Node to be compiled
	 * @param raw
	 *            If false, compiler will add indentation.
	 * 
	 * @return this
	 */
	public Compiler subcompile(Node node, boolean raw);

	/**
	 * Compiles an individual node. It is the responsibility of the node to
	 * compile any children nodes it might have.
	 * 
	 * This method will add indentation to the compiled output.
	 * 
	 * @param node
	 *            Node to be compiled
	 * 
	 * @return this
	 */
	public Compiler subcompile(Node node);

	/**
	 * Returns the main PebbleEngine that this compiler has access to.
	 * 
	 * @return The main PebbleEngine.
	 */
	public PebbleEngine getEngine();

	/**
	 * This method should only called from the main PebbleEngine class. While
	 * the rest of the compiler converts Nodes into a StringBuilder, this method
	 * will convert that StringBuilder into an actual Java class instance.
	 * 
	 * @param javaSource
	 *            Java source code
	 * @param className
	 *            The name of the generated class name
	 * @return The final PebbleTemplate instance
	 * @throws PebbleException
	 */
	PebbleTemplate instantiateTemplate(String javaSource, String className, String templateSource)
			throws PebbleException;

}
