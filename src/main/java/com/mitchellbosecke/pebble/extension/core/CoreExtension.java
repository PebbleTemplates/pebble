/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Original work Copyright (c) 2009-2013 by the Twig Team
 * Modified work Copyright (c) 2013 by Mitchell Bösecke
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.extension.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mitchellbosecke.pebble.extension.AbstractExtension;
import com.mitchellbosecke.pebble.extension.Filter;
import com.mitchellbosecke.pebble.extension.Function;
import com.mitchellbosecke.pebble.extension.Test;
import com.mitchellbosecke.pebble.node.expression.AddExpression;
import com.mitchellbosecke.pebble.node.expression.AndExpression;
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
import com.mitchellbosecke.pebble.tokenParser.ExtendsTokenParser;
import com.mitchellbosecke.pebble.tokenParser.FlushTokenParser;
import com.mitchellbosecke.pebble.tokenParser.ForTokenParser;
import com.mitchellbosecke.pebble.tokenParser.IfTokenParser;
import com.mitchellbosecke.pebble.tokenParser.ImportTokenParser;
import com.mitchellbosecke.pebble.tokenParser.IncludeTokenParser;
import com.mitchellbosecke.pebble.tokenParser.MacroTokenParser;
import com.mitchellbosecke.pebble.tokenParser.ParallelTokenParser;
import com.mitchellbosecke.pebble.tokenParser.SetTokenParser;
import com.mitchellbosecke.pebble.tokenParser.TokenParser;

public class CoreExtension extends AbstractExtension {

	@Override
	public List<TokenParser> getTokenParsers() {
		ArrayList<TokenParser> parsers = new ArrayList<>();
		parsers.add(new BlockTokenParser());
		parsers.add(new ExtendsTokenParser());
		parsers.add(new IfTokenParser());
		parsers.add(new ForTokenParser());
		parsers.add(new MacroTokenParser());
		parsers.add(new ImportTokenParser());
		parsers.add(new IncludeTokenParser());
		parsers.add(new SetTokenParser());
		parsers.add(new FlushTokenParser());
		parsers.add(new ParallelTokenParser());
		return parsers;
	}

	@Override
	public List<UnaryOperator> getUnaryOperators() {
		ArrayList<UnaryOperator> operators = new ArrayList<>();
		operators.add(new UnaryOperatorImpl("not", 5, UnaryNotExpression.class));
		operators.add(new UnaryOperatorImpl("+", 500, UnaryPlusExpression.class));
		operators.add(new UnaryOperatorImpl("-", 500, UnaryMinusExpression.class));
		return operators;
	}

	@Override
	public List<BinaryOperator> getBinaryOperators() {
		ArrayList<BinaryOperator> operators = new ArrayList<>();
		operators.add(new BinaryOperatorImpl("or", 10, OrExpression.class, Associativity.LEFT));
		operators.add(new BinaryOperatorImpl("and", 15, AndExpression.class, Associativity.LEFT));
		operators.add(new BinaryOperatorImpl("is", 20, PositiveTestExpression.class, Associativity.LEFT));
		operators.add(new BinaryOperatorImpl("is not", 20, NegativeTestExpression.class, Associativity.LEFT));
		operators.add(new BinaryOperatorImpl("==", 30, EqualsExpression.class, Associativity.LEFT));
		operators.add(new BinaryOperatorImpl("equals", 30, EqualsExpression.class, Associativity.LEFT));
		operators.add(new BinaryOperatorImpl("!=", 30, NotEqualsExpression.class, Associativity.LEFT));
		operators.add(new BinaryOperatorImpl(">", 30, GreaterThanExpression.class, Associativity.LEFT));
		operators.add(new BinaryOperatorImpl("<", 30, LessThanExpression.class, Associativity.LEFT));
		operators.add(new BinaryOperatorImpl(">=", 30, GreaterThanEqualsExpression.class, Associativity.LEFT));
		operators.add(new BinaryOperatorImpl("<=", 30, LessThanEqualsExpression.class, Associativity.LEFT));
		operators.add(new BinaryOperatorImpl("+", 40, AddExpression.class, Associativity.LEFT));
		operators.add(new BinaryOperatorImpl("-", 40, SubtractExpression.class, Associativity.LEFT));
		operators.add(new BinaryOperatorImpl("*", 60, MultiplyExpression.class, Associativity.LEFT));
		operators.add(new BinaryOperatorImpl("/", 60, DivideExpression.class, Associativity.LEFT));
		operators.add(new BinaryOperatorImpl("%", 60, ModulusExpression.class, Associativity.LEFT));
		operators.add(new BinaryOperatorImpl("|", 100, FilterExpression.class, Associativity.LEFT));

		return operators;
	}

	@Override
	public Map<String, Filter> getFilters() {
		Map<String, Filter> filters = new HashMap<>();
		filters.put("abbreviate", new AbbreviateFilter());
		filters.put("capitalize", new CapitalizeFilter());
		filters.put("date", new DateFilter());
		filters.put("default", new DefaultFilter());
		filters.put("lower", new LowerFilter());
		filters.put("numberformat", new NumberFormatFilter());
		filters.put("title", new TitleFilter());
		filters.put("trim", new TrimFilter());
		filters.put("upper", new UpperFilter());
		filters.put("urlencode", new UrlEncoderFilter());
		return filters;
	}

	@Override
	public Map<String, Test> getTests() {
		Map<String, Test> tests = new HashMap<>();
		tests.put("empty", new EmptyTest());
		tests.put("even", new EvenTest());
		tests.put("iterable", new IterableTest());
		tests.put("null", new NullTest());
		tests.put("odd", new OddTest());
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
		return functions;
	}

	@Override
	public Map<String, Object> getGlobalVariables() {

		return null;
	}

}
