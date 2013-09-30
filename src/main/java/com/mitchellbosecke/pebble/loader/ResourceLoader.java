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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mitchellbosecke.pebble.error.LoaderException;

public class ResourceLoader implements Loader {

	private static final Logger logger = LoggerFactory.getLogger(ResourceLoader.class);

	private Collection<String> paths;

	private Map<String, Reader> readerCache;

	public ResourceLoader(Collection<String> paths) {
		this.paths = paths;
		readerCache = new HashMap<>();
	}

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

	/*
	 * @Override public boolean isFresh(String templateName, Date timestamp)
	 * throws LoaderException { URL location =
	 * findTemplateLocation(templateName); Date lastModified; try { lastModified
	 * = new Date(location.openConnection().getLastModified()); } catch
	 * (IOException e) { throw new
	 * LoaderException("Could not determine last modified time of \"" +
	 * templateName + "\""); } return lastModified.before(timestamp); }
	 */

	public void addPath(String path) {
		if (this.paths == null) {
			this.paths = new ArrayList<>();
		}
		this.paths.add(path);
	}

	private Reader getReader(String templateName) throws LoaderException {

		Reader reader = readerCache.containsKey(templateName) ? readerCache.get(templateName) : null;

		if (reader == null) {
			InputStream is = null;
			
			ClassLoader ccl = Thread.currentThread().getContextClassLoader();
			ClassLoader rcl = ResourceLoader.class.getClassLoader();
			
			for (String path : paths) {
				
				path = path.endsWith(String.valueOf(File.separatorChar)) ? path : path + File.separatorChar;
				logger.info("Looking for template in {}.", path + templateName);

				// try ContextClassLoader
				is = ccl.getResourceAsStream(path + templateName);
				
				// try ResourceLoader's class loader
				if(is == null){
					is = rcl.getResourceAsStream(path + templateName);
				}
				
				// try to load File
				if( is == null){
					File file = new File(path, templateName);
					if(file.exists() && file.isFile()){
						try {
							is = new FileInputStream(file);
						} catch (FileNotFoundException e) {
							//TODO: throw exception?
						}
					}
				}
				
				if( is != null){
					break;
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

}
