/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Copyright (c) 2014 by Mitchell Bösecke
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.mitchellbosecke.pebble.error.ParserException;
import com.mitchellbosecke.pebble.lexer.Token;
import com.mitchellbosecke.pebble.lexer.TokenStream;
import com.mitchellbosecke.pebble.node.ArgumentsNode;
import com.mitchellbosecke.pebble.node.FunctionOrMacroNameNode;
import com.mitchellbosecke.pebble.node.NamedArgumentNode;
import com.mitchellbosecke.pebble.node.PositionalArgumentNode;
import com.mitchellbosecke.pebble.node.TestInvocationExpression;
import com.mitchellbosecke.pebble.node.expression.BinaryExpression;
import com.mitchellbosecke.pebble.node.expression.BlockFunctionExpression;
import com.mitchellbosecke.pebble.node.expression.ContextVariableExpression;
import com.mitchellbosecke.pebble.node.expression.Expression;
import com.mitchellbosecke.pebble.node.expression.FilterExpression;
import com.mitchellbosecke.pebble.node.expression.FilterInvocationExpression;
import com.mitchellbosecke.pebble.node.expression.FunctionOrMacroInvocationExpression;
import com.mitchellbosecke.pebble.node.expression.GetAttributeExpression;
import com.mitchellbosecke.pebble.node.expression.LiteralBooleanExpression;
import com.mitchellbosecke.pebble.node.expression.LiteralDoubleExpression;
import com.mitchellbosecke.pebble.node.expression.LiteralLongExpression;
import com.mitchellbosecke.pebble.node.expression.LiteralNullExpression;
import com.mitchellbosecke.pebble.node.expression.LiteralStringExpression;
import com.mitchellbosecke.pebble.node.expression.NegativeTestExpression;
import com.mitchellbosecke.pebble.node.expression.ParentFunctionExpression;
import com.mitchellbosecke.pebble.node.expression.PositiveTestExpression;
import com.mitchellbosecke.pebble.node.expression.TernaryExpression;
import com.mitchellbosecke.pebble.node.expression.UnaryExpression;
import com.mitchellbosecke.pebble.operator.Associativity;
import com.mitchellbosecke.pebble.operator.BinaryOperator;
import com.mitchellbosecke.pebble.operator.UnaryOperator;

/**
 * Parses expressions.
 */
public class ExpressionParser {

	private final Parser parser;
	private TokenStream stream;
	private Map<String, BinaryOperator> binaryOperators;
	private Map<String, UnaryOperator> unaryOperators;

	/**
	 * Constructor
	 * 
	 * @param parser
	 *            A reference to the main parser
	 */
	public ExpressionParser(Parser parser, Map<String, BinaryOperator> binaryOperators,
			Map<String, UnaryOperator> unaryOperators) {
		this.parser = parser;
		this.binaryOperators = binaryOperators;
		this.unaryOperators = unaryOperators;
	}

	/**
	 * The public entry point for parsing an expression.
	 * 
	 * @return NodeExpression the expression that has been parsed.
	 * @throws ParserException
	 */
	public Expression<?> parseExpression() throws ParserException {
		return parseExpression(0);
	}

	/**
	 * A private entry point for parsing an expression. This method takes in the
	 * precedence required to operate a "precedence climbing" parsing algorithm.
	 * It is a recursive method.
	 * 
	 * @see http://en.wikipedia.org/wiki/Operator-precedence_parser
	 * 
	 * @return The NodeExpression representing the parsed expression.
	 * @throws ParserException
	 */
	private Expression<?> parseExpression(int minPrecedence) throws ParserException {

		this.stream = parser.getStream();
		Token token = stream.current();
		Expression<?> expression = null;

		/*
		 * The first check is to see if the expression begins with a unary
		 * operator, or an opening bracket, or neither.
		 */
		if (isUnary(token)) {
			UnaryOperator operator = this.unaryOperators.get(token.getValue());
			stream.next();
			expression = parseExpression(operator.getPrecedence());

			UnaryExpression unaryExpression = null;
			Class<? extends UnaryExpression> operatorNodeClass = operator.getNodeClass();
			try {
				unaryExpression = operatorNodeClass.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			unaryExpression.setChildExpression(expression);

			expression = unaryExpression;

		} else if (token.test(Token.Type.PUNCTUATION, "(")) {

			stream.next();
			expression = parseExpression();
			stream.expect(Token.Type.PUNCTUATION, ")");
			expression = parsePostfixExpression(expression);

		} else {
			/*
			 * starts with neither. Let's parse out the first expression that we
			 * can find. There may be one, there may be many (separated by
			 * binary operators); right now we are just looking for the first.
			 */
			expression = subparseExpression();
		}

		/*
		 * If, after parsing the first expression we encounter a binary operator
		 * then we know we have another expression on the other side of the
		 * operator that requires parsing. Otherwise we're done.
		 */
		token = stream.current();
		while (isBinary(token) && binaryOperators.get(token.getValue()).getPrecedence() >= minPrecedence) {

			// find out which operator we are dealing with and then skip over it
			BinaryOperator operator = binaryOperators.get(token.getValue());
			stream.next();

			Expression<?> expressionRight = null;

			// the right hand expression of the FILTER operator is handled in a
			// unique way
			if (FilterExpression.class.equals(operator.getNodeClass())) {
				expressionRight = parseFilterInvocationExpression();
			}
			// the right hand expression of TEST operators is handled in a
			// unique way
			else if (PositiveTestExpression.class.equals(operator.getNodeClass())
					|| NegativeTestExpression.class.equals(operator.getNodeClass())) {
				expressionRight = parseTestInvocationExpression();
			} else {
				/*
				 * parse the expression on the right hand side of the operator
				 * while maintaining proper associativity and precedence
				 */
				expressionRight = parseExpression(Associativity.LEFT.equals(operator.getAssociativity()) ? operator
						.getPrecedence() + 1 : operator.getPrecedence());
			}

			/*
			 * we have to wrap the left and right side expressions into one
			 * final expression. The operator provides us with the type of
			 * expression we are creating.
			 */
			BinaryExpression<?> finalExpression = null;
			Class<? extends BinaryExpression<?>> operatorNodeClass = operator.getNodeClass();
			try {
				finalExpression = operatorNodeClass.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
				throw new ParserException(e, "Error instantiating operator node [" + operatorNodeClass.getName() + "]");
			}

			finalExpression.setLeft(expression);
			finalExpression.setRight(expressionRight);

			expression = finalExpression;

			token = stream.current();
		}

		if (minPrecedence == 0) {
			return parseTernaryExpression(expression);
		}

		return expression;
	}

	/**
	 * Checks if a token is a unary operator.
	 * 
	 * @param token
	 *            The token that we are checking
	 * @return boolean Whether the token is a unary operator or not
	 */
	private boolean isUnary(Token token) {
		return token.test(Token.Type.OPERATOR) && this.unaryOperators.containsKey(token.getValue());
	}

	/**
	 * Checks if a token is a binary operator.
	 * 
	 * @param token
	 *            The token that we are checking
	 * @return boolean Whether the token is a binary operator or not
	 */
	private boolean isBinary(Token token) {
		return token.test(Token.Type.OPERATOR) && this.binaryOperators.containsKey(token.getValue());
	}

	/**
	 * Finds and returns the next "simple" expression; an expression of which
	 * can be found on either side of a binary operator but does not contain a
	 * binary operator. Ex. "var.field", "true", "12", etc.
	 * 
	 * @return NodeExpression The expression that it found.
	 * @throws ParserException
	 */
	private Expression<?> subparseExpression() throws ParserException {
		final Token token = stream.current();
		Expression<?> node = null;

		switch (token.getType()) {

			case NAME:
				switch (token.getValue()) {

				// a constant?
					case "true":
					case "TRUE":
						node = new LiteralBooleanExpression(true);
						break;
					case "false":
					case "FALSE":
						node = new LiteralBooleanExpression(false);
						break;
					case "none":
					case "NONE":
					case "null":
					case "NULL":
						node = new LiteralNullExpression();
						break;

					default:

						// name of a function?
						if (stream.peek().test(Token.Type.PUNCTUATION, "(")) {
							node = new FunctionOrMacroNameNode(token.getValue());
						}

						// variable name
						else {
							node = new ContextVariableExpression(token.getValue());
						}
						break;
				}
				break;

			case NUMBER:
				final String numberValue = token.getValue();
				if (numberValue.contains(".")) {
					node = new LiteralDoubleExpression(Double.valueOf(numberValue));
				} else {
					node = new LiteralLongExpression(Long.valueOf(numberValue));
				}

				break;

			case STRING:
				node = new LiteralStringExpression(token.getValue());
				break;

			// not found, syntax error
			default:
				throw new ParserException(null, String.format("Unexpected token \"%s\" of value \"%s\"", token
						.getType().toString(), token.getValue()), token.getLineNumber(), stream.getFilename());
		}

		// there may or may not be more to this expression - let's keep looking
		stream.next();
		return parsePostfixExpression(node);
	}

	@SuppressWarnings("unchecked")
	private Expression<?> parseTernaryExpression(Expression<?> expression) throws ParserException {
		while (this.stream.current().test(Token.Type.PUNCTUATION, "?")) {

			stream.next();

			Expression<?> expression2 = null;
			Expression<?> expression3 = null;

			if (!stream.current().test(Token.Type.PUNCTUATION, ":")) {
				expression2 = parseExpression();

				if (stream.current().test(Token.Type.PUNCTUATION, ":")) {
					stream.next();
					expression3 = parseExpression();
				}
			} else {
				stream.next();
				expression2 = expression;
				expression3 = parseExpression();
			}

			expression = new TernaryExpression((Expression<Boolean>) expression, expression2, expression3);
		}

		return expression;
	}

	/**
	 * Determines if there is more to the provided expression than we originally
	 * thought. We will look for the filter operator or perhaps we are getting
	 * an attribute from a variable (ex. var.attribute).
	 * 
	 * @param node
	 *            The expression that we have already discovered
	 * @return Either the original expression that was passed in or a slightly
	 *         modified version of it, depending on what was discovered.
	 * @throws ParserException
	 */
	private Expression<?> parsePostfixExpression(Expression<?> node) throws ParserException {
		Token current;
		while (true) {
			current = stream.current();

			if (current.test(Token.Type.PUNCTUATION, ".")
			    || current.test(Token.Type.PUNCTUATION, "[")) {

				// a period represents getting an attribute from a variable or
				// calling a method
				node = parseSubscriptExpression(node);

			} else if (current.test(Token.Type.PUNCTUATION, "(")) {

				// function call
				node = parseFunctionOrMacroInvocation(node);

			} else {
				break;
			}
		}
		return node;
	}

	private Expression<?> parseFunctionOrMacroInvocation(Expression<?> node) throws ParserException {
		String functionName = ((FunctionOrMacroNameNode) node).getName();
		ArgumentsNode args = parseArguments();

		/*
		 * The following core functions have their own Nodes and are rendered in
		 * unique ways for the sake of performance.
		 */
		switch (functionName) {
			case "parent":
				return new ParentFunctionExpression(parser.peekBlockStack());
			case "block":
				return new BlockFunctionExpression(args);
		}

		return new FunctionOrMacroInvocationExpression(functionName, args);
	}

	private Expression<?> parseFilterInvocationExpression() throws ParserException {
		TokenStream stream = parser.getStream();
		Token filterToken = stream.expect(Token.Type.NAME);

		ArgumentsNode args = null;
		if (stream.current().test(Token.Type.PUNCTUATION, "(")) {
			args = this.parseArguments();
		} else {
			args = new ArgumentsNode(null, null);
		}

		return new FilterInvocationExpression(filterToken.getValue(), args);
	}

	private Expression<?> parseTestInvocationExpression() throws ParserException {
		TokenStream stream = parser.getStream();
		int lineNumber = stream.current().getLineNumber();

		Token testToken = stream.expect(Token.Type.NAME);

		ArgumentsNode args = null;
		if (stream.current().test(Token.Type.PUNCTUATION, "(")) {
			args = this.parseArguments();
		} else {
			args = new ArgumentsNode(null, null);
		}

		return new TestInvocationExpression(lineNumber, testToken.getValue(), args);
	}

	/**
	 * A subscript expression can either be an expression getting an attribute
	 * from a variable, or calling a method from a variable.
	 * 
	 * @param node
	 *            The expression parsed so far
	 * @return NodeExpression The parsed subscript expression
	 * @throws ParserException
	 */
	private Expression<?> parseSubscriptExpression(Expression<?> node) throws ParserException {
		TokenStream stream = parser.getStream();

		if (stream.current().test(Token.Type.PUNCTUATION, ".")) {

			// skip over the '.' token
			stream.next();

			Token token = stream.expect(Token.Type.NAME);

			node = new GetAttributeExpression(node, token.getValue());

		} else if (stream.current().test(Token.Type.PUNCTUATION, "[")) {
			// skip over opening '[' bracket
			stream.next();

			// treat the string value inside the brackets just the same as we
			// would an attribute name following a '.', except that the
			// attribute name gathered this way is NOT held to the same naming
			// restrictions (e.g. can include hyphens '-')
			Token token = stream.expect(Token.Type.STRING);
			node = new GetAttributeExpression(node, token.getValue());

			// move past the closing ']' bracket
			stream.expect(Token.Type.PUNCTUATION, "]");
		}

		return node;
	}

	public ArgumentsNode parseArguments() throws ParserException {
		return parseArguments(false);
	}

	public ArgumentsNode parseArguments(boolean isMacroDefinition) throws ParserException {

		List<PositionalArgumentNode> positionalArgs = new ArrayList<>();
		List<NamedArgumentNode> namedArgs = new ArrayList<>();
		this.stream = this.parser.getStream();

		stream.expect(Token.Type.PUNCTUATION, "(");

		while (!stream.current().test(Token.Type.PUNCTUATION, ")")) {

			String argumentName = null;
			Expression<?> argumentValue = null;

			if (!namedArgs.isEmpty() || !positionalArgs.isEmpty()) {
				stream.expect(Token.Type.PUNCTUATION, ",");
			}

			/*
			 * Most arguments consist of VALUES with optional NAMES but in the
			 * case of a macro definition the user is specifying NAMES with
			 * optional VALUES. Therefore the logic changes slightly.
			 */
			if (isMacroDefinition) {
				argumentName = parseNewVariableName();
				if (stream.current().test(Token.Type.PUNCTUATION, "=")) {
					stream.expect(Token.Type.PUNCTUATION, "=");
					argumentValue = parseExpression();
				}
			} else {
				if (stream.peek().test(Token.Type.PUNCTUATION, "=")) {
					argumentName = parseNewVariableName();
					stream.expect(Token.Type.PUNCTUATION, "=");
				}
				argumentValue = parseExpression();
			}

			if (argumentName == null) {
				if(!namedArgs.isEmpty()){
					throw new ParserException(null, "Positional arguments must be declared before any named arguments.");
				}
				positionalArgs.add(new PositionalArgumentNode(argumentValue));
			} else {
				namedArgs.add(new NamedArgumentNode(argumentName, argumentValue));
			}

		}

		stream.expect(Token.Type.PUNCTUATION, ")");

		return new ArgumentsNode(positionalArgs, namedArgs);
	}

	/**
	 * Parses a new variable that will need to be initialized in the Java code.
	 * 
	 * This is used for the set tag, the for loop, and in named arguments.
	 * 
	 * @return
	 * @throws ParserException
	 */
	public String parseNewVariableName() throws ParserException {

		// set the stream because this function may be called externally (for
		// and set token parsers)
		this.stream = this.parser.getStream();
		Token token = stream.current();
		token.test(Token.Type.NAME);

		String[] reserved = new String[] { "true", "false", "null", "none" };
		if (Arrays.asList(reserved).contains(token.getValue())) {
			throw new ParserException(null, String.format("Can not assign a value to %s", token.getValue()),
					token.getLineNumber(), stream.getFilename());
		}

		stream.next();
		return token.getValue();
	}
}
