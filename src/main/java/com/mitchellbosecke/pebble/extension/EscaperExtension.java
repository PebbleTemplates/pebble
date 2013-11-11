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

import org.apache.commons.lang3.StringEscapeUtils;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.filter.Filter;
import com.mitchellbosecke.pebble.filter.FilterFunction;
import com.mitchellbosecke.pebble.utils.Command;

public class EscaperExtension extends AbstractExtension {

	@Override
	public void initRuntime(PebbleEngine engine) {
	}

	@Override
	public List<Filter> getFilters() {
		ArrayList<Filter> filters = new ArrayList<>();
		filters.add(new FilterFunction("escape", escapeFilter));
		return filters;
	}

	private Command<Object, List<Object>> escapeFilter = new Command<Object, List<Object>>() {
		@Override
		public Object execute(List<Object> data) {

			String input = (String) data.get(0);

			String strategy = "html";
			try {
				strategy = (String) data.get(1);
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
		return StringEscapeUtils.escapeHtml4(input);
	}

}
