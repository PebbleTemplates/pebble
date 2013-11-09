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
	public Compiler compile(Node node);

	public String getSource();
	
	public Compiler write(String string);
	
	public Compiler raw(String string);
	
	public Compiler string(String string);
	
	public Compiler subcompile(Node node, boolean raw);
	
	public Compiler subcompile(Node node);
	
	public Compiler indent();
	
	public Compiler outdent();
	
	public PebbleEngine getEngine();
	
	public PebbleTemplate compileToJava(String javaSource, String className) throws PebbleException;
	
	
}
