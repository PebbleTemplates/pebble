/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Original work Copyright (c) 2009-2013 by the Twig Team
 * Modified work Copyright (c) 2013 by Mitchell Bösecke
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.extension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mitchellbosecke.pebble.PebbleEngine;

public class EscaperExtension extends AbstractExtension {

	@Override
	public void initRuntime(PebbleEngine engine) {
	}

	@Override
	public Map<String, Filter> getFilters() {
		Map<String, Filter> filters = new HashMap<>();
		filters.put("escape", escapeFilter);
		return filters;
	}

	private static Filter escapeFilter = new Filter() {
		public List<String> getArgumentNames(){
			List<String> names = new ArrayList<>();
			names.add("strategy");
			return names;
		}
		public Object apply(Object inputObject, Map<String,Object> args) {
			String input = (String) inputObject;

			String strategy = "html";
			
			if(args.get("strategy") != null ){
				strategy = (String) args.get("strategy");
			}

			switch (strategy) {
				case "html":
					input = htmlEscape(input);
					break;
				default:
					throw new RuntimeException("Unknown escaping strategy");

			}
			return input;
		}
	};

	private static String htmlEscape(String input) {
		if (input == null) {
			return input;
		}
		StringBuilder result = new StringBuilder();
		char[] chars = input.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			char charac = chars[i];

			switch (charac) {
				case '&':
					result.append("&amp;");
					break;
				case '<':
					result.append("&lt;");
					break;
				case '>':
					result.append("&gt;");
					break;
				case '"':
					result.append("&quot;");
					break;
				case '\'':
					result.append("&#x27;");
					break;
				case '/':
					result.append("&#x2F;");
					break;
				default:
					result.append(charac);
					break;
			}
		}
		return result.toString();
	}

}
