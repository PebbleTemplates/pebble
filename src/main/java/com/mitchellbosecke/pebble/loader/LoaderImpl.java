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

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

import org.apache.commons.io.IOUtils;

import com.mitchellbosecke.pebble.error.LoaderException;

public class LoaderImpl implements Loader {

	private Collection<String> paths;
	private HashMap<String, URL> cache;

	public LoaderImpl(Collection<String> paths) {
		this.paths = paths;
		this.cache = new HashMap<>();
	}

	@Override
	public String getSource(String filename) {
		URL location = findTemplateLocation(filename);
		StringWriter writer;
		try {
			InputStream source = location.openStream();
			writer = new StringWriter();
			IOUtils.copy(source, writer, "UTF-8");
		} catch (Exception e) {
			throw new LoaderException("Template can not be found.");
		}
		return writer.toString();
	}

	@Override
	public String getCacheKey(String name) {
		return findTemplateLocation(name).toString();
	}

	@Override
	public boolean isFresh(String name, Date timestamp) {
		URL location = findTemplateLocation(name);
		Date lastModified;
		try {
			lastModified = new Date(location.openConnection().getLastModified());
		} catch (IOException e) {
			throw new LoaderException(
					"Could not determine last modified time of \"" + name
							+ "\"");
		}
		return lastModified.before(timestamp);
	}

	public Collection<String> getPaths() {
		return paths;
	}

	public void setPaths(Collection<String> paths) {
		// invalidate the cache
		this.cache.clear();
		this.paths = paths;
	}

	public void addPath(String path) {
		// invalidate the cache
		this.cache.clear();
		this.paths.add(path);
	}

	public URL findTemplateLocation(String name) {
		URL location = cache.get(name);
		if (location == null) {
			for (String path : paths) {
				location = LoaderImpl.class.getClassLoader().getResource(
						path + "/" + name);
				if (location != null) {
					cache.put(name, location);
					break;
				}
			}
		}
		if (location == null)
			throw new LoaderException("Could not find template \"" + name
					+ "\"");
		return location;
	}

}
