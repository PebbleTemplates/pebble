/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Copyright (c) 2012 Mitchell Bosecke.
 * 
 * This work is licensed under the Creative Commons Attribution-ShareAlike 3.0 
 * Unported License. To view a copy of this license, visit 
 * http://creativecommons.org/licenses/by-sa/3.0/
 ******************************************************************************/
package com.mitchellbosecke.pebble.spring;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ServletContextAware;

import com.mitchellbosecke.pebble.error.LoaderException;
import com.mitchellbosecke.pebble.loader.Loader;

public class ServletContextResourceLoader implements Loader, ServletContextAware {

	private static final Logger logger = LoggerFactory.getLogger(ServletContextResourceLoader.class);

	private Map<String, URL> locationCache;

	private ServletContext servletContext;

	private String prefix = null;

	private String suffix = null;

	public ServletContextResourceLoader() {
		locationCache = new HashMap<>();
	}

	@Override
	public String getSource(String templateName) throws LoaderException {
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
	public boolean isFresh(String templateName, Date timestamp) throws LoaderException {
		URL location = findTemplateLocation(templateName);
		Date lastModified;
		try {
			lastModified = new Date(location.openConnection().getLastModified());
		} catch (IOException e) {
			throw new LoaderException("Could not determine last modified time of \"" + templateName + "\"");
		}
		return lastModified.before(timestamp);
	}

	private URL findTemplateLocation(String templateName) throws LoaderException {
		URL location = locationCache.containsKey(templateName) ? locationCache.get(templateName) : null;

		logger.info("Looking for template in {}.", prefix + templateName + suffix);

		try {
			location = servletContext.getResource(prefix + templateName + suffix);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (location != null) {
			locationCache.put(templateName, location);
		}

		if (location == null) {
			throw new LoaderException("Could not find template \"" + templateName + "\"");
		}
		return location;
	}

	public ServletContext getServletContext() {
		return servletContext;
	}

	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

}
