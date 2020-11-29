package com.mitchellbosecke.pebble.extension;

import com.mitchellbosecke.pebble.attributes.AttributeResolver;
import com.mitchellbosecke.pebble.operator.BinaryOperator;
import com.mitchellbosecke.pebble.operator.UnaryOperator;
import com.mitchellbosecke.pebble.tokenParser.TokenParser;

import java.util.List;
import java.util.Map;

public abstract class ExtensionCustomizer implements Extension {

  private final Extension delegate;

  public ExtensionCustomizer(Extension delegate) {
    this.delegate = delegate;
  }

  @Override
  public Map<String, Filter> getFilters() {
    return delegate.getFilters();
  }

  @Override
  public Map<String, Test> getTests() {
    return delegate.getTests();
  }

  @Override
  public Map<String, Function> getFunctions() {
    return delegate.getFunctions();
  }

  @Override
  public List<TokenParser> getTokenParsers() {
    return delegate.getTokenParsers();
  }

  @Override
  public List<BinaryOperator> getBinaryOperators() {
    return delegate.getBinaryOperators();
  }

  @Override
  public List<UnaryOperator> getUnaryOperators() {
    return delegate.getUnaryOperators();
  }

  @Override
  public Map<String, Object> getGlobalVariables() {
    return delegate.getGlobalVariables();
  }

  @Override
  public List<NodeVisitorFactory> getNodeVisitors() {
    return delegate.getNodeVisitors();
  }

  @Override
  public List<AttributeResolver> getAttributeResolver() {
    return delegate.getAttributeResolver();
  }

}
