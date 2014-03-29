/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Copyright (c) 2014 by Mitchell Bösecke
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.template;

import java.io.IOException;
import java.io.Writer;
import java.util.Locale;
import java.util.Map;

import com.mitchellbosecke.pebble.error.PebbleException;

public interface PebbleTemplate {

	public void evaluate(Writer writer) throws PebbleException, IOException;

	public void evaluate(Writer writer, Locale locale) throws PebbleException, IOException;

	public void evaluate(Writer writer, Map<String, Object> map) throws PebbleException, IOException;

	public void evaluate(Writer writer, Map<String, Object> map, Locale locale) throws PebbleException, IOException;
}
