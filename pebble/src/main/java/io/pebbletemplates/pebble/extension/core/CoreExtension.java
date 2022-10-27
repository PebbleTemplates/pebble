/*
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package io.pebbletemplates.pebble.extension.core;

import io.pebbletemplates.pebble.extension.AbstractExtension;
import io.pebbletemplates.pebble.extension.Filter;
import io.pebbletemplates.pebble.extension.Function;
import io.pebbletemplates.pebble.extension.NodeVisitorFactory;
import io.pebbletemplates.pebble.extension.Test;
import io.pebbletemplates.pebble.node.expression.AddExpression;
import io.pebbletemplates.pebble.node.expression.AndExpression;
import io.pebbletemplates.pebble.node.expression.ConcatenateExpression;
import io.pebbletemplates.pebble.node.expression.ContainsExpression;
import io.pebbletemplates.pebble.node.expression.DivideExpression;
import io.pebbletemplates.pebble.node.expression.EqualsExpression;
import io.pebbletemplates.pebble.node.expression.FilterExpression;
import io.pebbletemplates.pebble.node.expression.GreaterThanEqualsExpression;
import io.pebbletemplates.pebble.node.expression.GreaterThanExpression;
import io.pebbletemplates.pebble.node.expression.LessThanEqualsExpression;
import io.pebbletemplates.pebble.node.expression.LessThanExpression;
import io.pebbletemplates.pebble.node.expression.ModulusExpression;
import io.pebbletemplates.pebble.node.expression.MultiplyExpression;
import io.pebbletemplates.pebble.node.expression.NegativeTestExpression;
import io.pebbletemplates.pebble.node.expression.NotEqualsExpression;
import io.pebbletemplates.pebble.node.expression.OrExpression;
import io.pebbletemplates.pebble.node.expression.PositiveTestExpression;
import io.pebbletemplates.pebble.node.expression.RangeExpression;
import io.pebbletemplates.pebble.node.expression.SubtractExpression;
import io.pebbletemplates.pebble.node.expression.UnaryMinusExpression;
import io.pebbletemplates.pebble.node.expression.UnaryNotExpression;
import io.pebbletemplates.pebble.node.expression.UnaryPlusExpression;
import io.pebbletemplates.pebble.operator.Associativity;
import io.pebbletemplates.pebble.operator.BinaryOperator;
import io.pebbletemplates.pebble.operator.BinaryOperatorImpl;
import io.pebbletemplates.pebble.operator.UnaryOperator;
import io.pebbletemplates.pebble.operator.UnaryOperatorImpl;
import io.pebbletemplates.pebble.operator.*;
import io.pebbletemplates.pebble.tokenParser.BlockTokenParser;
import io.pebbletemplates.pebble.tokenParser.CacheTokenParser;
import io.pebbletemplates.pebble.tokenParser.EmbedTokenParser;
import io.pebbletemplates.pebble.tokenParser.ExtendsTokenParser;
import io.pebbletemplates.pebble.tokenParser.FilterTokenParser;
import io.pebbletemplates.pebble.tokenParser.FlushTokenParser;
import io.pebbletemplates.pebble.tokenParser.ForTokenParser;
import io.pebbletemplates.pebble.tokenParser.FromTokenParser;
import io.pebbletemplates.pebble.tokenParser.IfTokenParser;
import io.pebbletemplates.pebble.tokenParser.ImportTokenParser;
import io.pebbletemplates.pebble.tokenParser.IncludeTokenParser;
import io.pebbletemplates.pebble.tokenParser.MacroTokenParser;
import io.pebbletemplates.pebble.tokenParser.ParallelTokenParser;
import io.pebbletemplates.pebble.tokenParser.SetTokenParser;
import io.pebbletemplates.pebble.tokenParser.TokenParser;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CoreExtension extends AbstractExtension {

  @Override
  public List<TokenParser> getTokenParsers() {
    List<TokenParser> parsers = new ArrayList<>();
    parsers.add(new BlockTokenParser());
    parsers.add(new ExtendsTokenParser());
    parsers.add(new EmbedTokenParser());
    parsers.add(new FilterTokenParser());
    parsers.add(new FlushTokenParser());
    parsers.add(new ForTokenParser());
    parsers.add(new IfTokenParser());
    parsers.add(new ImportTokenParser());
    parsers.add(new IncludeTokenParser());
    parsers.add(new MacroTokenParser());
    parsers.add(new ParallelTokenParser());
    parsers.add(new SetTokenParser());
    parsers.add(new CacheTokenParser());
    parsers.add(new FromTokenParser());

    // verbatim tag is implemented directly in the LexerImpl
    return parsers;
  }

  @Override
  public List<UnaryOperator> getUnaryOperators() {
    List<UnaryOperator> operators = new ArrayList<>();
    operators.add(new UnaryOperatorImpl("not", 500, UnaryNotExpression.class));
    operators.add(new UnaryOperatorImpl("+", 500, UnaryPlusExpression.class));
    operators.add(new UnaryOperatorImpl("-", 500, UnaryMinusExpression.class));
    return operators;
  }

  @Override
  public List<BinaryOperator> getBinaryOperators() {
    List<BinaryOperator> operators = new ArrayList<>();
    operators.add(new BinaryOperatorImpl("or", 10, OrExpression::new, BinaryOperatorType.NORMAL, Associativity.LEFT));
    operators.add(new BinaryOperatorImpl("and", 15, AndExpression::new, BinaryOperatorType.NORMAL, Associativity.LEFT));
    operators.add(new BinaryOperatorImpl("is", 20, PositiveTestExpression::new, BinaryOperatorType.TEST, Associativity.LEFT));
    operators.add(new BinaryOperatorImpl("is not", 20, NegativeTestExpression::new, BinaryOperatorType.TEST, Associativity.LEFT));
    operators.add(new BinaryOperatorImpl("contains", 20, ContainsExpression::new, BinaryOperatorType.NORMAL, Associativity.LEFT));
    operators.add(new BinaryOperatorImpl("==", 30, EqualsExpression::new, BinaryOperatorType.NORMAL, Associativity.LEFT));
    operators.add(new BinaryOperatorImpl("equals", 30, EqualsExpression::new, BinaryOperatorType.NORMAL, Associativity.LEFT));
    operators.add(new BinaryOperatorImpl("!=", 30, NotEqualsExpression::new, BinaryOperatorType.NORMAL, Associativity.LEFT));
    operators.add(new BinaryOperatorImpl(">", 30, GreaterThanExpression::new, BinaryOperatorType.NORMAL, Associativity.LEFT));
    operators.add(new BinaryOperatorImpl("<", 30, LessThanExpression::new, BinaryOperatorType.NORMAL, Associativity.LEFT));
    operators.add(new BinaryOperatorImpl(">=", 30, GreaterThanEqualsExpression::new, BinaryOperatorType.NORMAL, Associativity.LEFT));
    operators.add(new BinaryOperatorImpl("<=", 30, LessThanEqualsExpression::new, BinaryOperatorType.NORMAL, Associativity.LEFT));
    operators.add(new BinaryOperatorImpl("+", 40, AddExpression::new, BinaryOperatorType.NORMAL, Associativity.LEFT));
    operators.add(new BinaryOperatorImpl("-", 40, SubtractExpression::new, BinaryOperatorType.NORMAL, Associativity.LEFT));
    operators.add(new BinaryOperatorImpl("*", 60, MultiplyExpression::new, BinaryOperatorType.NORMAL, Associativity.LEFT));
    operators.add(new BinaryOperatorImpl("/", 60, DivideExpression::new, BinaryOperatorType.NORMAL, Associativity.LEFT));
    operators.add(new BinaryOperatorImpl("%", 60, ModulusExpression::new, BinaryOperatorType.NORMAL, Associativity.LEFT));
    operators.add(new BinaryOperatorImpl("|", 100, FilterExpression::new, BinaryOperatorType.FILTER, Associativity.LEFT));
    operators.add(new BinaryOperatorImpl("~", 110, ConcatenateExpression::new, BinaryOperatorType.NORMAL, Associativity.LEFT));
    operators.add(new BinaryOperatorImpl("..", 120, RangeExpression::new, BinaryOperatorType.NORMAL, Associativity.LEFT));

    return operators;
  }

  @Override
  public Map<String, Filter> getFilters() {
    Map<String, Filter> filters = new HashMap<>();
    filters.put("abbreviate", new AbbreviateFilter());
    filters.put("abs", new AbsFilter());
    filters.put("capitalize", new CapitalizeFilter());
    filters.put("date", new DateFilter());
    filters.put("default", new DefaultFilter());
    filters.put("first", new FirstFilter());
    filters.put("join", new JoinFilter());
    filters.put("last", new LastFilter());
    filters.put("lower", new LowerFilter());
    filters.put("numberformat", new NumberFormatFilter());
    filters.put("slice", new SliceFilter());
    filters.put("sort", new SortFilter());
    filters.put("rsort", new RsortFilter());
    filters.put("reverse", new ReverseFilter());
    filters.put("title", new TitleFilter());
    filters.put("trim", new TrimFilter());
    filters.put("upper", new UpperFilter());
    filters.put("urlencode", new UrlEncoderFilter());
    filters.put("length", new LengthFilter());
    filters.put(ReplaceFilter.FILTER_NAME, new ReplaceFilter());
    filters.put(MergeFilter.FILTER_NAME, new MergeFilter());
    filters.put(SplitFilter.FILTER_NAME, new SplitFilter());
    filters.put(Base64EncoderFilter.FILTER_NAME, new Base64EncoderFilter());
    filters.put(Base64DecoderFilter.FILTER_NAME, new Base64DecoderFilter());
    filters.put(Sha256Filter.FILTER_NAME, new Sha256Filter());
    return filters;
  }

  @Override
  public Map<String, Test> getTests() {
    Map<String, Test> tests = new HashMap<>();
    tests.put("empty", new EmptyTest());
    tests.put("even", new EvenTest());
    tests.put("iterable", new IterableTest());
    tests.put("map", new MapTest());
    tests.put("null", new NullTest());
    tests.put("odd", new OddTest());
    tests.put("defined", new DefinedTest());
    return tests;
  }

  @Override
  public Map<String, Function> getFunctions() {
    Map<String, Function> functions = new HashMap<>();

    /*
     * For efficiency purposes, some core functions are individually parsed
     * by our expression parser and compiled in their own unique way. This
     * includes the block and parent functions.
     */

    functions.put("max", new MaxFunction());
    functions.put("min", new MinFunction());
    functions.put(RangeFunction.FUNCTION_NAME, new RangeFunction());
    return functions;
  }

  @Override
  public Map<String, Object> getGlobalVariables() {
    return null;
  }

  @Override
  public List<NodeVisitorFactory> getNodeVisitors() {
    List<NodeVisitorFactory> visitors = new ArrayList<>();
    visitors.add(new MacroAndBlockRegistrantNodeVisitorFactory());
    return visitors;
  }
}
