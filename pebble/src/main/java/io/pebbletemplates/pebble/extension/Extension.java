/*
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package io.pebbletemplates.pebble.extension;

import io.pebbletemplates.pebble.attributes.AttributeResolver;
import io.pebbletemplates.pebble.operator.BinaryOperator;
import io.pebbletemplates.pebble.operator.UnaryOperator;
import io.pebbletemplates.pebble.tokenParser.TokenParser;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;

public interface Extension {

  /**
   * Use this method to provide custom filters.
   *
   * @return A list of filters. It is okay to return null.
   */
  @Nullable
  Map<@NonNull String, @NonNull Filter> getFilters();

  /**
   * Use this method to provide custom tests.
   *
   * @return A list of tests. It is okay to return null.
   */
  @Nullable
  Map<@NonNull String, @NonNull Test> getTests();

  /**
   * Use this method to provide custom functions.
   *
   * @return A list of functions. It is okay to return null.
   */
  @Nullable
  Map<@NonNull String, @NonNull Function> getFunctions();

  /**
   * Use this method to provide custom tags.
   *
   * A TokenParser is used to parse a stream of tokens into Nodes which are then responsible for
   * compiling themselves into Java.
   *
   * @return A list of TokenParsers. It is okay to return null.
   */
  @Nullable
  List<@NonNull TokenParser> getTokenParsers();

  /**
   * Use this method to provide custom binary operators.
   *
   * @return A list of Operators. It is okay to return null;
   */
  @Nullable
  List<@NonNull BinaryOperator> getBinaryOperators();

  /**
   * Use this method to provide custom unary operators.
   *
   * @return A list of Operators. It is okay to return null;
   */
  @Nullable
  List<@NonNull UnaryOperator> getUnaryOperators();

  /**
   * Use this method to provide variables available to all templates
   *
   * @return Map of global variables available to all templates
   */
  @Nullable
  Map<@NonNull String, @NonNull Object> getGlobalVariables();

  /**
   * Node visitors will travel the AST tree during the compilation phase.
   *
   * @return a list of node visitors
   */
  @Nullable
  List<@NonNull NodeVisitorFactory> getNodeVisitors();

  /**
   * AttributeResolver will resolve instance attributes
   *
   * @return a list of attribute resolver
   */
  @Nullable
  List<@NonNull AttributeResolver> getAttributeResolver();
}
