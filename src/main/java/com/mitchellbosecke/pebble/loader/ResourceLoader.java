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

import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.io.IOUtils;

import com.mitchellbosecke.pebble.error.LoaderException;

public class ResourceLoader implements Loader {

	private Collection<String> paths;

	public ResourceLoader(Collection<String> paths) {
		this.paths = paths;
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

	public void addPath(String path) {
		if(this.paths == null){
			this.paths = new ArrayList<>();
		}
		this.paths.add(path);
	}

	private URL findTemplateLocation(String name) {
		URL location = null;
		
		for (String path : paths) {
			location = ResourceLoader.class.getClassLoader().getResource(path + "/" + name);
			if(location != null){
				break;
			}
		}

		if (location == null){
			throw new LoaderException("Could not find template \"" + name + "\"");
		}
		return location;
	}

}
