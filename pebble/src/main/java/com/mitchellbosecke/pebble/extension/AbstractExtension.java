/*
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.mitchellbosecke.pebble.extension;

import com.mitchellbosecke.pebble.attributes.AttributeResolver;
import com.mitchellbosecke.pebble.operator.BinaryOperator;
import com.mitchellbosecke.pebble.operator.UnaryOperator;
import com.mitchellbosecke.pebble.tokenParser.TokenParser;
import java.util.List;
import java.util.Map;

public abstract class AbstractExtension implements Extension {

  @Override
  public List<TokenParser> getTokenParsers() {
    return null;
  }

  @Override
  public List<BinaryOperator> getBinaryOperators() {
    return null;
  }

  @Override
  public List<UnaryOperator> getUnaryOperators() {
    return null;
  }

  @Override
  public Map<String, Filter> getFilters() {
    return null;
  }

  @Override
  public Map<String, Test> getTests() {
    return null;
  }

  @Override
  public Map<String, Function> getFunctions() {
    return null;
  }

  @Override
  public Map<String, Object> getGlobalVariables() {
    return null;
  }

  @Override
  public List<NodeVisitorFactory> getNodeVisitors() {
    return null;
  }

  @Override
  public List<AttributeResolver> getAttributeResolver() {
    return null;
  }
}
