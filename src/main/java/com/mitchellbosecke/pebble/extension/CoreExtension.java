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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.filter.Filter;
import com.mitchellbosecke.pebble.node.expression.binary.NodeExpressionBinaryAdd;
import com.mitchellbosecke.pebble.node.expression.binary.NodeExpressionBinaryAnd;
import com.mitchellbosecke.pebble.node.expression.binary.NodeExpressionBinaryDivide;
import com.mitchellbosecke.pebble.node.expression.binary.NodeExpressionBinaryEqual;
import com.mitchellbosecke.pebble.node.expression.binary.NodeExpressionBinaryGreaterThan;
import com.mitchellbosecke.pebble.node.expression.binary.NodeExpressionBinaryGreaterThanEquals;
import com.mitchellbosecke.pebble.node.expression.binary.NodeExpressionBinaryIs;
import com.mitchellbosecke.pebble.node.expression.binary.NodeExpressionBinaryIsNot;
import com.mitchellbosecke.pebble.node.expression.binary.NodeExpressionBinaryLessThan;
import com.mitchellbosecke.pebble.node.expression.binary.NodeExpressionBinaryLessThanEquals;
import com.mitchellbosecke.pebble.node.expression.binary.NodeExpressionBinaryModulus;
import com.mitchellbosecke.pebble.node.expression.binary.NodeExpressionBinaryMultiply;
import com.mitchellbosecke.pebble.node.expression.binary.NodeExpressionBinaryNotEqual;
import com.mitchellbosecke.pebble.node.expression.binary.NodeExpressionBinaryOr;
import com.mitchellbosecke.pebble.node.expression.binary.NodeExpressionBinarySubtract;
import com.mitchellbosecke.pebble.node.expression.unary.NodeExpressionUnaryNegative;
import com.mitchellbosecke.pebble.node.expression.unary.NodeExpressionUnaryNot;
import com.mitchellbosecke.pebble.node.expression.unary.NodeExpressionUnaryPositive;
import com.mitchellbosecke.pebble.operator.Associativity;
import com.mitchellbosecke.pebble.operator.BinaryOperator;
import com.mitchellbosecke.pebble.operator.BinaryOperatorImpl;
import com.mitchellbosecke.pebble.operator.UnaryOperator;
import com.mitchellbosecke.pebble.operator.UnaryOperatorImpl;
import com.mitchellbosecke.pebble.test.Test;
import com.mitchellbosecke.pebble.tokenParser.BlockTokenParser;
import com.mitchellbosecke.pebble.tokenParser.ExtendsTokenParser;
import com.mitchellbosecke.pebble.tokenParser.ForTokenParser;
import com.mitchellbosecke.pebble.tokenParser.IfTokenParser;
import com.mitchellbosecke.pebble.tokenParser.ImportTokenParser;
import com.mitchellbosecke.pebble.tokenParser.IncludeTokenParser;
import com.mitchellbosecke.pebble.tokenParser.MacroTokenParser;
import com.mitchellbosecke.pebble.tokenParser.SetTokenParser;
import com.mitchellbosecke.pebble.tokenParser.TokenParser;

public class CoreExtension extends AbstractExtension {

	private String charset = "UTF-8";

	@Override
	public void initRuntime(PebbleEngine engine) {
		charset = engine.getCharset();
	}

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
		return parsers;
	}

	@Override
	public List<UnaryOperator> getUnaryOperators() {
		ArrayList<UnaryOperator> operators = new ArrayList<>();
		operators.add(new UnaryOperatorImpl("not", 50, NodeExpressionUnaryNot.class));
		operators.add(new UnaryOperatorImpl("+", 500, NodeExpressionUnaryPositive.class));
		operators.add(new UnaryOperatorImpl("-", 500, NodeExpressionUnaryNegative.class));
		return operators;
	}

	@Override
	public List<BinaryOperator> getBinaryOperators() {
		ArrayList<BinaryOperator> operators = new ArrayList<>();
		operators.add(new BinaryOperatorImpl("or", 10, NodeExpressionBinaryOr.class, Associativity.LEFT));
		operators.add(new BinaryOperatorImpl("and", 15, NodeExpressionBinaryAnd.class, Associativity.LEFT));
		operators.add(new BinaryOperatorImpl("==", 20, NodeExpressionBinaryEqual.class, Associativity.LEFT));
		operators.add(new BinaryOperatorImpl("equals", 20, NodeExpressionBinaryEqual.class, Associativity.LEFT));
		operators.add(new BinaryOperatorImpl("!=", 20, NodeExpressionBinaryNotEqual.class, Associativity.LEFT));
		operators.add(new BinaryOperatorImpl(">", 20, NodeExpressionBinaryGreaterThan.class, Associativity.LEFT));
		operators.add(new BinaryOperatorImpl("<", 20, NodeExpressionBinaryLessThan.class, Associativity.LEFT));
		operators
				.add(new BinaryOperatorImpl(">=", 20, NodeExpressionBinaryGreaterThanEquals.class, Associativity.LEFT));
		operators.add(new BinaryOperatorImpl("<=", 20, NodeExpressionBinaryLessThanEquals.class, Associativity.LEFT));
		operators.add(new BinaryOperatorImpl("+", 30, NodeExpressionBinaryAdd.class, Associativity.LEFT));
		operators.add(new BinaryOperatorImpl("-", 30, NodeExpressionBinarySubtract.class, Associativity.LEFT));
		operators.add(new BinaryOperatorImpl("*", 60, NodeExpressionBinaryMultiply.class, Associativity.LEFT));
		operators.add(new BinaryOperatorImpl("/", 60, NodeExpressionBinaryDivide.class, Associativity.LEFT));
		operators.add(new BinaryOperatorImpl("%", 60, NodeExpressionBinaryModulus.class, Associativity.LEFT));
		operators.add(new BinaryOperatorImpl("is", 100, NodeExpressionBinaryIs.class, Associativity.LEFT));
		operators.add(new BinaryOperatorImpl("is not", 100, NodeExpressionBinaryIsNot.class, Associativity.LEFT));
		return operators;
	}

	@Override
	public List<Filter> getFilters() {
		ArrayList<Filter> filters = new ArrayList<>();
		filters.add(lowerFilter);
		filters.add(upperFilter);
		filters.add(dateFilter);
		filters.add(urlEncoderFilter);
		filters.add(formatFilter);
		filters.add(numberFilter);
		filters.add(abbreviateFilter);
		filters.add(capitalizeFilter);
		filters.add(trimFilter);
		filters.add(jsonEncodeFilter);
		filters.add(defaultFilter);
		return filters;
	}

	@Override
	public List<Test> getTests() {
		ArrayList<Test> tests = new ArrayList<>();
		tests.add(evenTest);
		tests.add(oddTest);
		tests.add(nullTest);
		tests.add(emptyTest);
		tests.add(iterableTest);
		return tests;
	}

	private Filter lowerFilter = new Filter() {
		public String getName() {
			return "lower";
		}

		public Object apply(Object input, List<Object> args) {
			return ((String) input).toLowerCase();
		}
	};

	private Filter upperFilter = new Filter() {
		public String getName() {
			return "upper";
		}

		public Object apply(Object input, List<Object> args) {
			return ((String) input).toUpperCase();
		}
	};

	private Filter urlEncoderFilter = new Filter() {
		public String getName() {
			return "urlencode";
		}

		public Object apply(Object input, List<Object> args) {
			String arg = (String) input;
			try {
				arg = URLEncoder.encode(arg, charset);
			} catch (UnsupportedEncodingException e) {
			}
			return arg;
		}
	};

	private Filter formatFilter = new Filter() {
		public String getName() {
			return "format";
		}

		public Object apply(Object input, List<Object> args) {
			String arg = (String) input;
			Object[] formatArgs = args.toArray();

			return String.format(arg, formatArgs);
		}
	};

	private Filter dateFilter = new Filter() {
		public String getName() {
			return "date";
		}

		public Object apply(Object input, List<Object> args) {

			Date arg = null;

			DateFormat existingFormat = null;
			DateFormat intendedFormat = null;

			if (args.size() == 1) {
				arg = (Date) input;
				intendedFormat = new SimpleDateFormat((String) args.get(0));
			} else if (args.size() == 2) {

				existingFormat = new SimpleDateFormat((String) args.get(0));
				intendedFormat = new SimpleDateFormat((String) args.get(1));

				try {
					arg = existingFormat.parse((String) input);
				} catch (ParseException e) {
					// TODO: figure out what to do here
				}
			}

			return intendedFormat.format(arg);
		}
	};

	private Filter numberFilter = new Filter() {
		public String getName() {
			return "number";
		}

		public Object apply(Object input, List<Object> args) {
			Number number = (Number) input;
			Format format = new DecimalFormat((String) args.get(0));

			return format.format(number);
		}
	};

	private Filter abbreviateFilter = new Filter() {
		public String getName() {
			return "abbreviate";
		}

		public Object apply(Object input, List<Object> args) {
			String str = (String) input;
			int maxWidth = (Integer) args.get(0);

			return StringUtils.abbreviate(str, maxWidth);
		}
	};

	private Filter capitalizeFilter = new Filter() {
		public String getName() {
			return "capitalize";
		}

		public Object apply(Object input, List<Object> args) {
			String str = (String) input;
			return StringUtils.capitalize(str);
		}
	};

	private Filter trimFilter = new Filter() {
		public String getName() {
			return "trim";
		}

		public Object apply(Object input, List<Object> args) {
			String str = (String) input;
			return str.trim();
		}
	};

	private Filter jsonEncodeFilter = new Filter() {
		public String getName() {
			return "json";
		}

		public Object apply(Object input, List<Object> args) {

			ObjectMapper mapper = new ObjectMapper();

			String json = null;
			try {
				json = mapper.writeValueAsString(input);
			} catch (JsonProcessingException e) {
			}
			return json;
		}
	};

	private Filter defaultFilter = new Filter() {
		public String getName() {
			return "default";
		}

		public Object apply(Object input, List<Object> args) {

			Object defaultObj = args.get(0);

			if (emptyTest.apply(input, new ArrayList<>())) {
				return defaultObj;
			}
			return input;
		}
	};

	private Test evenTest = new Test() {
		public String getName() {
			return "even";
		}

		public Boolean apply(Object input, List<Object> args) {

			Integer obj = (Integer) input;
			return (obj % 2 == 0);
		}
	};

	private Test oddTest = new Test() {
		public String getName() {
			return "odd";
		}

		public Boolean apply(Object input, List<Object> args) {

			return evenTest.apply(input, args) == false;
		}
	};

	private Test nullTest = new Test() {
		public String getName() {
			return "null";
		}

		public Boolean apply(Object input, List<Object> args) {

			return input == null;
		}
	};

	private Test emptyTest = new Test() {
		public String getName() {
			return "empty";
		}

		public Boolean apply(Object input, List<Object> args) {
			boolean isEmpty = input == null;

			if (!isEmpty && input instanceof String) {
				isEmpty = StringUtils.isBlank(((String) input));
			}

			if (!isEmpty && input instanceof Collection) {
				isEmpty = ((Collection<?>) input).isEmpty();
			}

			if (!isEmpty && input instanceof Map) {
				isEmpty = ((Map<?, ?>) input).isEmpty();
			}

			return isEmpty;
		}
	};

	private Test iterableTest = new Test() {
		public String getName() {
			return "iterable";
		}

		public Boolean apply(Object input, List<Object> args) {

			return input instanceof Iterable;
		}
	};

}
