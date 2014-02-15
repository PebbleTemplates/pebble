/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Original work Copyright (c) 2009-2013 by the Twig Team
 * Modified work Copyright (c) 2013 by Mitchell Bösecke
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.extension.escaper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.extension.AbstractExtension;
import com.mitchellbosecke.pebble.extension.Filter;
import com.mitchellbosecke.pebble.extension.NodeVisitor;
import com.mitchellbosecke.pebble.tokenParser.AutoEscapeTokenParser;
import com.mitchellbosecke.pebble.tokenParser.TokenParser;

public class EscaperExtension extends AbstractExtension {

	private final EscapeFilter filter;

	private final EscaperNodeVisitor visitor;

	public EscaperExtension() {
		this.filter = new EscapeFilter();
		this.visitor = new EscaperNodeVisitor();
	}

	@Override
	public void initRuntime(PebbleEngine engine) {
	}

	@Override
	public Map<String, Filter> getFilters() {
		Map<String, Filter> filters = new HashMap<>();
		filters.put("escape", filter);
		filters.put("raw", new RawFilter());
		return filters;
	}

	@Override
	public List<TokenParser> getTokenParsers() {
		List<TokenParser> parsers = new ArrayList<>();
		parsers.add(new AutoEscapeTokenParser());
		return parsers;
	}

	@Override
	public List<NodeVisitor> getNodeVisitors() {
		List<NodeVisitor> visitors = new ArrayList<>();
		visitors.add(visitor);
		return visitors;
	}

	/**
	 * Sets the default escaping strategy.
	 * 
	 * @param strategy
	 */
	public void setDefaultStrategy(String strategy) {
		filter.setDefaultStrategy(strategy);
	}

	public void setAutoEscaping(boolean auto) {
		visitor.pushAutoEscapeState(auto);
	}

	public void addSafeFilter(String filter) {
		visitor.addSafeFilter(filter);
	}

}
