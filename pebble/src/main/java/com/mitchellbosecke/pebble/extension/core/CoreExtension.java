/*
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.mitchellbosecke.pebble.extension.core;

import static com.mitchellbosecke.pebble.operator.BinaryOperatorType.FILTER;
import static com.mitchellbosecke.pebble.operator.BinaryOperatorType.NORMAL;
import static com.mitchellbosecke.pebble.operator.BinaryOperatorType.TEST;

import com.mitchellbosecke.pebble.extension.AbstractExtension;
import com.mitchellbosecke.pebble.extension.Filter;
import com.mitchellbosecke.pebble.extension.Function;
import com.mitchellbosecke.pebble.extension.NodeVisitorFactory;
import com.mitchellbosecke.pebble.extension.Test;
import com.mitchellbosecke.pebble.node.expression.AddExpression;
import com.mitchellbosecke.pebble.node.expression.AndExpression;
import com.mitchellbosecke.pebble.node.expression.ConcatenateExpression;
import com.mitchellbosecke.pebble.node.expression.ContainsExpression;
import com.mitchellbosecke.pebble.node.expression.DivideExpression;
import com.mitchellbosecke.pebble.node.expression.EqualsExpression;
import com.mitchellbosecke.pebble.node.expression.FilterExpression;
import com.mitchellbosecke.pebble.node.expression.GreaterThanEqualsExpression;
import com.mitchellbosecke.pebble.node.expression.GreaterThanExpression;
import com.mitchellbosecke.pebble.node.expression.LessThanEqualsExpression;
import com.mitchellbosecke.pebble.node.expression.LessThanExpression;
import com.mitchellbosecke.pebble.node.expression.ModulusExpression;
import com.mitchellbosecke.pebble.node.expression.MultiplyExpression;
import com.mitchellbosecke.pebble.node.expression.NegativeTestExpression;
import com.mitchellbosecke.pebble.node.expression.NotEqualsExpression;
import com.mitchellbosecke.pebble.node.expression.OrExpression;
import com.mitchellbosecke.pebble.node.expression.PositiveTestExpression;
import com.mitchellbosecke.pebble.node.expression.RangeExpression;
import com.mitchellbosecke.pebble.node.expression.SubtractExpression;
import com.mitchellbosecke.pebble.node.expression.UnaryMinusExpression;
import com.mitchellbosecke.pebble.node.expression.UnaryNotExpression;
import com.mitchellbosecke.pebble.node.expression.UnaryPlusExpression;
import com.mitchellbosecke.pebble.operator.Associativity;
import com.mitchellbosecke.pebble.operator.BinaryOperator;
import com.mitchellbosecke.pebble.operator.BinaryOperatorImpl;
import com.mitchellbosecke.pebble.operator.UnaryOperator;
import com.mitchellbosecke.pebble.operator.UnaryOperatorImpl;
import com.mitchellbosecke.pebble.tokenParser.BlockTokenParser;
import com.mitchellbosecke.pebble.tokenParser.CacheTokenParser;
import com.mitchellbosecke.pebble.tokenParser.EmbedTokenParser;
import com.mitchellbosecke.pebble.tokenParser.ExtendsTokenParser;
import com.mitchellbosecke.pebble.tokenParser.FilterTokenParser;
import com.mitchellbosecke.pebble.tokenParser.FlushTokenParser;
import com.mitchellbosecke.pebble.tokenParser.ForTokenParser;
import com.mitchellbosecke.pebble.tokenParser.FromTokenParser;
import com.mitchellbosecke.pebble.tokenParser.IfTokenParser;
import com.mitchellbosecke.pebble.tokenParser.ImportTokenParser;
import com.mitchellbosecke.pebble.tokenParser.IncludeTokenParser;
import com.mitchellbosecke.pebble.tokenParser.MacroTokenParser;
import com.mitchellbosecke.pebble.tokenParser.ParallelTokenParser;
import com.mitchellbosecke.pebble.tokenParser.SetTokenParser;
import com.mitchellbosecke.pebble.tokenParser.TokenParser;
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
    operators.add(new BinaryOperatorImpl("or", 10, OrExpression::new, NORMAL, Associativity.LEFT));
    operators.add(new BinaryOperatorImpl("and", 15, AndExpression::new, NORMAL, Associativity.LEFT));
    operators.add(new BinaryOperatorImpl("is", 20, PositiveTestExpression::new, TEST, Associativity.LEFT));
    operators.add(new BinaryOperatorImpl("is not", 20, NegativeTestExpression::new, TEST, Associativity.LEFT));
    operators.add(new BinaryOperatorImpl("contains", 20, ContainsExpression::new, NORMAL, Associativity.LEFT));
    operators.add(new BinaryOperatorImpl("==", 30, EqualsExpression::new, NORMAL, Associativity.LEFT));
    operators.add(new BinaryOperatorImpl("equals", 30, EqualsExpression::new, NORMAL, Associativity.LEFT));
    operators.add(new BinaryOperatorImpl("!=", 30, NotEqualsExpression::new, NORMAL, Associativity.LEFT));
    operators.add(new BinaryOperatorImpl(">", 30, GreaterThanExpression::new, NORMAL, Associativity.LEFT));
    operators.add(new BinaryOperatorImpl("<", 30, LessThanExpression::new, NORMAL, Associativity.LEFT));
    operators.add(new BinaryOperatorImpl(">=", 30, GreaterThanEqualsExpression::new, NORMAL, Associativity.LEFT));
    operators.add(new BinaryOperatorImpl("<=", 30, LessThanEqualsExpression::new, NORMAL, Associativity.LEFT));
    operators.add(new BinaryOperatorImpl("+", 40, AddExpression::new, NORMAL, Associativity.LEFT));
    operators.add(new BinaryOperatorImpl("-", 40, SubtractExpression::new, NORMAL, Associativity.LEFT));
    operators.add(new BinaryOperatorImpl("*", 60, MultiplyExpression::new, NORMAL, Associativity.LEFT));
    operators.add(new BinaryOperatorImpl("/", 60, DivideExpression::new, NORMAL, Associativity.LEFT));
    operators.add(new BinaryOperatorImpl("%", 60, ModulusExpression::new, NORMAL, Associativity.LEFT));
    operators.add(new BinaryOperatorImpl("|", 100, FilterExpression::new, FILTER, Associativity.LEFT));
    operators.add(new BinaryOperatorImpl("~", 110, ConcatenateExpression::new, NORMAL, Associativity.LEFT));
    operators.add(new BinaryOperatorImpl("..", 120, RangeExpression::new, NORMAL, Associativity.LEFT));

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
