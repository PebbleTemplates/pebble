/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Original work Copyright (c) 2009-2013 by the Twig Team
 * Modified work Copyright (c) 2013 by Mitchell Bösecke
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.loader;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import com.mitchellbosecke.pebble.error.LoaderException;

public class DelegatingLoader implements Loader {

	private String prefix;

	private String suffix;

	private String charset = "UTF-8";

	private final List<Loader> loaders = new ArrayList<>();

	public DelegatingLoader(List<Loader> loaders) {
		this.loaders.addAll(loaders);
	}

	@Override
	public Reader getReader(String templateName) throws LoaderException {

		Reader reader = null;

		for (Loader loader : this.loaders) {
			try {
				reader = loader.getReader(templateName);
			} catch (LoaderException e) {
				// do nothing
			}
		}
		if (reader == null) {
			throw new LoaderException(null, "Could not find template \"" + templateName + "\"");
		}

		return reader;
	}

	public String getSuffix() {
		return suffix;
	}

	@Override
	public void setSuffix(String suffix) {
		this.suffix = suffix;
		for (Loader loader : loaders) {
			loader.setSuffix(suffix);
		}
	}

	public String getPrefix() {
		return prefix;
	}

	@Override
	public void setPrefix(String prefix) {
		this.prefix = prefix;
		for (Loader loader : loaders) {
			loader.setPrefix(prefix);
		}
	}

	public String getCharset() {
		return charset;
	}

	@Override
	public void setCharset(String charset) {
		this.charset = charset;
		for (Loader loader : loaders) {
			loader.setCharset(charset);
		}
	}
}