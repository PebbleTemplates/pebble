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
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mitchellbosecke.pebble.PebbleEngine;
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
import com.mitchellbosecke.pebble.node.expression.unary.NodeExpressionUnaryMinus;
import com.mitchellbosecke.pebble.node.expression.unary.NodeExpressionUnaryNot;
import com.mitchellbosecke.pebble.node.expression.unary.NodeExpressionUnaryPlus;
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
import com.mitchellbosecke.pebble.utils.OperatorUtils;
import com.mitchellbosecke.pebble.utils.StringUtils;

public class CoreExtension extends AbstractExtension {

	private static final String charset = "UTF-8";

	@Override
	public void initRuntime(PebbleEngine engine) {

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
		parsers.add(new FlushTokenParser());
		parsers.add(new ParallelTokenParser());
		return parsers;
	}

	@Override
	public List<UnaryOperator> getUnaryOperators() {
		ArrayList<UnaryOperator> operators = new ArrayList<>();
		operators.add(new UnaryOperatorImpl("not", 50, NodeExpressionUnaryNot.class));
		operators.add(new UnaryOperatorImpl("+", 500, NodeExpressionUnaryPlus.class));
		operators.add(new UnaryOperatorImpl("-", 500, NodeExpressionUnaryMinus.class));
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
	public Map<String, Filter> getFilters() {
		Map<String, Filter> filters = new HashMap<>();
		filters.put("lower", lowerFilter);
		filters.put("upper", upperFilter);
		filters.put("date", dateFilter);
		filters.put("urlencode", urlEncoderFilter);
		filters.put("format", formatFilter);
		filters.put("numberformat", numberFormatFilter);
		filters.put("abbreviate", abbreviateFilter);
		filters.put("capitalize", capitalizeFilter);
		filters.put("trim", trimFilter);
		filters.put("default", defaultFilter);
		return filters;
	}

	@Override
	public Map<String, Test> getTests() {
		Map<String, Test> tests = new HashMap<>();
		tests.put("even", evenTest);
		tests.put("odd", oddTest);
		tests.put("null", nullTest);
		tests.put("empty", emptyTest);
		tests.put("iterable", iterableTest);
		return tests;
	}

	@Override
	public Map<String, SimpleFunction> getFunctions() {
		Map<String, SimpleFunction> functions = new HashMap<>();

		/*
		 * For efficiency purposes, some core functions are individually parsed
		 * by our expression parser and compiled in their own unique way. This
		 * includes the block and parent functions.
		 */

		functions.put("min", minFunction);
		functions.put("max", maxFunction);
		return functions;
	}

	@Override
	public Map<String, Object> getGlobalVariables() {

		/*
		 * The following core global variables are defined in
		 * PebbleTemplate.initContext():
		 * 
		 * _locale
		 */

		return null;
	}

	private static Filter lowerFilter = new Filter() {
		public Object apply(Object input, List<Object> args) {
			if (input == null) {
				return null;
			}
			return ((String) input).toLowerCase();
		}
	};

	private static Filter upperFilter = new Filter() {
		public Object apply(Object input, List<Object> args) {
			if (input == null) {
				return null;
			}
			return ((String) input).toUpperCase();
		}
	};

	private static Filter urlEncoderFilter = new Filter() {
		public Object apply(Object input, List<Object> args) {
			if (input == null) {
				return null;
			}
			String arg = (String) input;
			try {
				arg = URLEncoder.encode(arg, charset);
			} catch (UnsupportedEncodingException e) {
			}
			return arg;
		}
	};

	private static Filter formatFilter = new Filter() {
		public Object apply(Object input, List<Object> args) {
			if (input == null) {
				return null;
			}
			String arg = (String) input;
			Object[] formatArgs = args.toArray();

			return String.format(arg, formatArgs);
		}
	};

	private static Filter dateFilter = new LocaleAwareFilter() {
		public Object apply(Object input, List<Object> args) {
			if (input == null) {
				return null;
			}
			Date arg = null;

			DateFormat existingFormat = null;
			DateFormat intendedFormat = null;

			if (args.size() == 1) {
				arg = (Date) input;
				intendedFormat = new SimpleDateFormat((String) args.get(0), locale);
			} else if (args.size() == 2) {

				existingFormat = new SimpleDateFormat((String) args.get(0), locale);
				intendedFormat = new SimpleDateFormat((String) args.get(1), locale);

				try {
					arg = existingFormat.parse((String) input);
				} catch (ParseException e) {
					// TODO: figure out what to do here
				}
			}

			return intendedFormat.format(arg);
		}
	};

	private static Filter numberFormatFilter = new LocaleAwareFilter() {
		public Object apply(Object input, List<Object> args) {
			if (input == null) {
				return null;
			}
			Number number = (Number) input;

			if (args.size() > 0) {
				Format format = new DecimalFormat((String) args.get(0));
				return format.format(number);
			} else {
				NumberFormat numberFormat = NumberFormat.getInstance(locale);
				return numberFormat.format(number);
			}
		}
	};

	private static Filter abbreviateFilter = new Filter() {
		public Object apply(Object input, List<Object> args) {
			if (input == null) {
				return null;
			}
			String str = (String) input;
			int maxWidth = (Integer) args.get(0);

			return StringUtils.abbreviate(str, maxWidth);
		}
	};

	private static Filter capitalizeFilter = new Filter() {
		public Object apply(Object input, List<Object> args) {
			if (input == null) {
				return null;
			}
			String str = (String) input;
			return StringUtils.capitalize(str);
		}
	};

	private static Filter trimFilter = new Filter() {
		public Object apply(Object input, List<Object> args) {
			if (input == null) {
				return null;
			}
			String str = (String) input;
			return str.trim();
		}
	};

	private static Filter defaultFilter = new Filter() {
		public Object apply(Object input, List<Object> args) {

			Object defaultObj = args.get(0);

			if (emptyTest.apply(input, new ArrayList<>())) {
				return defaultObj;
			}
			return input;
		}
	};

	private static Test evenTest = new Test() {
		public boolean apply(Object input, List<Object> args) {
			if(input == null){
				throw new IllegalArgumentException("Can not pass null value to \"even\" test.");
			}

			Integer obj = (Integer) input;
			return (obj % 2 == 0);
		}
	};

	private static Test oddTest = new Test() {
		public boolean apply(Object input, List<Object> args) {
			if(input == null){
				throw new IllegalArgumentException("Can not pass null value to \"odd\" test.");
			}
			return evenTest.apply(input, args) == false;
		}
	};

	private static Test nullTest = new Test() {
		public boolean apply(Object input, List<Object> args) {

			return input == null;
		}
	};

	private static Test emptyTest = new Test() {
		public boolean apply(Object input, List<Object> args) {
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

	private static Test iterableTest = new Test() {
		public boolean apply(Object input, List<Object> args) {

			return input instanceof Iterable;
		}
	};

	private static SimpleFunction minFunction = new SimpleFunction() {
		public Object execute(List<Object> args) {
			Object min = null;
			for (Object candidate : args) {
				if (min == null) {
					min = candidate;
					continue;
				}
				if (OperatorUtils.lt(candidate, min)) {
					min = candidate;
				}
			}
			return min;
		}
	};

	private static SimpleFunction maxFunction = new SimpleFunction() {

		public Object execute(List<Object> args) {
			Object min = null;
			for (Object candidate : args) {
				if (min == null) {
					min = candidate;
					continue;
				}
				if (OperatorUtils.gt(candidate, min)) {
					min = candidate;
				}
			}
			return min;

		}
	};
}
