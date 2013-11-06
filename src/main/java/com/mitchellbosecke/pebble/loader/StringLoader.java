/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Copyright (c) 2012 Mitchell Bosecke.
 * 
 * This work is licensed under the Creative Commons Attribution-ShareAlike 3.0 
 * Unported License. To view a copy of this license, visit 
 * http://creativecommons.org/licenses/by-sa/3.0/
 ******************************************************************************/
package com.mitchellbosecke.pebble.loader;

import java.io.Reader;
import java.io.StringReader;

import com.mitchellbosecke.pebble.error.LoaderException;

public class StringLoader implements Loader {

	@Override
	public String getSource(String templateName) throws LoaderException {
		return templateName;
	}

	/**
	 * Template name is actually the source
	 * 
	 */
	protected Reader getReader(String templateName) throws LoaderException {

		Reader reader = new StringReader(templateName);

		return reader;
	}

	@Override
	public void setPrefix(String prefix) {

	}

	@Override
	public void setSuffix(String suffix) {

	}

}
