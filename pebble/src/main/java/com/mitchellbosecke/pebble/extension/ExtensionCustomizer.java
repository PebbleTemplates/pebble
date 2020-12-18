package com.mitchellbosecke.pebble.extension;

import com.mitchellbosecke.pebble.attributes.AttributeResolver;
import com.mitchellbosecke.pebble.operator.BinaryOperator;
import com.mitchellbosecke.pebble.operator.UnaryOperator;
import com.mitchellbosecke.pebble.tokenParser.TokenParser;

import java.util.List;
import java.util.Map;

/**
 *  Base class that allows implementing a customizer to modify Pebbles build-in extensions.
 *  It is meant to provide a way to remove or replace functions, filters, tags, etc. to change
 *  the standard behaviour. Use-cases can be down-stripping available functionality for security
 *  reasons.
 *
 *  Implementations of this class are meant to overwrite methods and access registered functionality
 *  before it is loaded into the PebbleEngine by calling super.
 *
 *  The ExentsionCustomizer can be registred via {@link com.mitchellbosecke.pebble.PebbleEngine.Builder#registerExtensionCustomizer}
 *  and is applied for every non-user-provided extension.
 *
 */
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
