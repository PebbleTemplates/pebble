/*
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.mitchellbosecke.pebble.extension.escaper;

import com.mitchellbosecke.pebble.extension.AbstractExtension;
import com.mitchellbosecke.pebble.extension.Filter;
import com.mitchellbosecke.pebble.extension.NodeVisitorFactory;
import com.mitchellbosecke.pebble.tokenParser.AutoEscapeTokenParser;
import com.mitchellbosecke.pebble.tokenParser.TokenParser;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EscaperExtension extends AbstractExtension {

  private final EscapeFilter filter;

  private final EscaperNodeVisitorFactory visitorFactory;

  public EscaperExtension() {
    this.filter = new EscapeFilter();
    this.visitorFactory = new EscaperNodeVisitorFactory();
  }

  @Override
  public Map<String, Filter> getFilters() {
    Map<String, Filter> filters = new HashMap<>();
    filters.put("escape", this.filter);
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
  public List<NodeVisitorFactory> getNodeVisitors() {
    List<NodeVisitorFactory> visitors = new ArrayList<>();
    visitors.add(this.visitorFactory);
    return visitors;
  }

  /**
   * Sets the default escaping strategy.
   *
   * @param strategy Escaping strategy
   */
  public void setDefaultStrategy(String strategy) {
    // TODO: This method is dangerous, because the state of the filter is
    // changed. When this is changed during the rendering of template this
    // can lead to unexpected results.
    this.filter.setDefaultStrategy(strategy);
  }

  public void setAutoEscaping(boolean auto) {
    this.visitorFactory.setAutoEscaping(auto);
  }

  /**
   * Adds a custom escaping strategy to the filter.
   *
   * @param name Name of the escaping strategy
   * @param strategy The implementation of the escaping strategy
   */
  public void addEscapingStrategy(String name, EscapingStrategy strategy) {
    // TODO: This method is dangerous, because the state of the filter is
    // changed. When this is changed during the rendering of template this
    // can lead to unexpected results.
    this.filter.addEscapingStrategy(name, strategy);
  }

}
