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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mitchellbosecke.pebble.error.LoaderException;

public class PebbleDefaultLoader implements Loader {

	private static final Logger logger = LoggerFactory.getLogger(PebbleDefaultLoader.class);

	private String prefix;

	private String suffix;

	private Map<String, Reader> readerCache = new HashMap<>();

	@Override
	public String getSource(String templateName) throws LoaderException {
		Reader location = getReader(templateName);
		String source = null;

		try {
			source = IOUtils.toString(location);
		} catch (IOException e) {
			throw new LoaderException("Template can not be found.");
		}
		return source;
	}

	protected Reader getReader(String templateName) throws LoaderException {

		Reader reader = readerCache.containsKey(templateName) ? readerCache.get(templateName) : null;

		if (reader == null) {
			InputStream is = null;

			String path = getPrefix().endsWith(String.valueOf(File.separatorChar)) ? getPrefix() : getPrefix()
					+ File.separatorChar;

			String location = path + templateName + (getSuffix() == null ? "" : getSuffix());
			logger.info("Looking for template in {}.", location);

			// try ContextClassLoader
			ClassLoader ccl = Thread.currentThread().getContextClassLoader();
			is = ccl.getResourceAsStream(location);

			// try ResourceLoader's class loader
			ClassLoader rcl = PebbleDefaultLoader.class.getClassLoader();
			if (is == null) {
				is = rcl.getResourceAsStream(location);
			}

			// try to load File
			if (is == null) {
				File file = new File(path, templateName);
				if (file.exists() && file.isFile()) {
					try {
						is = new FileInputStream(file);
					} catch (FileNotFoundException e) {
						// TODO: throw exception?
					}
				}
			}

			if (is == null) {
				throw new LoaderException("Could not find template \"" + templateName + "\"");
			}

			try {
				reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			} catch (UnsupportedEncodingException e) {
			}
		}

		readerCache.put(templateName, reader);

		return reader;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

}
