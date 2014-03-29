/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Copyright (c) 2014 by Mitchell Bösecke
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.extension.escaper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.coverity.security.Escape;
import com.mitchellbosecke.pebble.extension.Filter;

public class EscapeFilter implements Filter {

	private String defaultStrategy = "html";

	public List<String> getArgumentNames() {
		List<String> names = new ArrayList<>();
		names.add("strategy");
		return names;
	}

	public Object apply(Object inputObject, Map<String, Object> args) {
		if (!(inputObject instanceof String)) {
			return inputObject;
		}
		String input = (String) inputObject;

		String strategy = defaultStrategy;

		if (args.get("strategy") != null) {
			strategy = (String) args.get("strategy");
		}

		switch (strategy) {
			case "html":
				input = Escape.htmlText(input);
				break;
			case "js":
				input = Escape.jsString(input);
				break;
			case "css":
				input = Escape.cssString(input);
				break;
			case "html_attr":
				input = Escape.html(input);
				break;
			case "url_param":
				input = Escape.uriParam(input);
			default:
				throw new RuntimeException("Unknown escaping strategy");

		}
		return input;
	}

	public String getDefaultStrategy() {
		return defaultStrategy;
	}

	public void setDefaultStrategy(String defaultStrategy) {
		this.defaultStrategy = defaultStrategy;
	}

}
