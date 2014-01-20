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
import java.util.List;

import com.mitchellbosecke.pebble.PebbleEngine;

public class EscaperExtension extends AbstractExtension {

	@Override
	public void initRuntime(PebbleEngine engine) {
	}

	@Override
	public List<Filter> getFilters() {
		ArrayList<Filter> filters = new ArrayList<>();
		filters.add(escapeFilter);
		return filters;
	}

	private Filter escapeFilter = new Filter() {
		public String getName() {
			return "escape";
		}

		public Object apply(Object inputObject, List<Object> args) {
			String input = (String) inputObject;

			String strategy = "html";
			try {
				strategy = (String) args.get(0);
			} catch (IndexOutOfBoundsException e) {
				// user did not provide strategy which is okay.
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

	private String htmlEscape(String input) {
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
