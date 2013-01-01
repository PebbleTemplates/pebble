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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import com.mitchellbosecke.pebble.error.LoaderException;

public class ResourceLoader implements Loader {

	private Collection<String> paths;

	private Map<String, URL> locationCache;

	public ResourceLoader(Collection<String> paths) {
		this.paths = paths;
		locationCache = new HashMap<>();
	}

	@Override
	public String getSource(String templateName) {
		URL location = findTemplateLocation(templateName);
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
	public boolean isFresh(String templateName, Date timestamp) {
		URL location = findTemplateLocation(templateName);
		Date lastModified;
		try {
			lastModified = new Date(location.openConnection().getLastModified());
		} catch (IOException e) {
			throw new LoaderException("Could not determine last modified time of \"" + templateName + "\"");
		}
		return lastModified.before(timestamp);
	}

	public void addPath(String path) {
		if (this.paths == null) {
			this.paths = new ArrayList<>();
		}
		this.paths.add(path);
	}

	private URL findTemplateLocation(String templateName) {
		URL location = locationCache.containsKey(templateName) ? locationCache.get(templateName) : null;

		for (String path : paths) {
			location = ResourceLoader.class.getClassLoader().getResource(path + "/" + templateName);
			if (location != null) {
				locationCache.put(templateName, location);
				break;
			}
		}

		if (location == null) {
			throw new LoaderException("Could not find template \"" + templateName + "\"");
		}
		return location;
	}

}
