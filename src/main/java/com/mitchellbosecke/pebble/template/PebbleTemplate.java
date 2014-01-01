/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Original work Copyright (c) 2009-2013 by the Twig Team
 * Modified work Copyright (c) 2013 by Mitchell Bösecke
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.template;

import java.io.Writer;
import java.util.Locale;
import java.util.Map;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.error.PebbleException;

public interface PebbleTemplate {
	
	public final static String COMPILED_PACKAGE_NAME = "com.mitchellbosecke.pebble.template.compiled";
	
	public void evaluate(Writer writer) throws PebbleException;

	public void evaluate(Writer writer, Map<String, Object> model) throws PebbleException;

	public void setEngine(PebbleEngine engine);
	
	public String getSource();
	
	public String getGeneratedJavaCode();

	public void setGeneratedJavaCode(String javaSource);
	
	public void setSource(String source);
	
	public void setLocale(Locale locale);
	
	public Locale getLocale();

}