/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Original work Copyright (c) 2009-2013 by the Twig Team
 * Modified work Copyright (c) 2013 by Mitchell Bösecke
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.mitchellbosecke.pebble.error.SyntaxException;
import com.mitchellbosecke.pebble.lexer.Token;
import com.mitchellbosecke.pebble.lexer.TokenStream;
import com.mitchellbosecke.pebble.node.NodeExpression;
import com.mitchellbosecke.pebble.node.expression.NodeExpressionArguments;
import com.mitchellbosecke.pebble.node.expression.NodeExpressionBinary;
import com.mitchellbosecke.pebble.node.expression.NodeExpressionBlockReference;
import com.mitchellbosecke.pebble.node.expression.NodeExpressionConstant;
import com.mitchellbosecke.pebble.node.expression.NodeExpressionDeclaration;
import com.mitchellbosecke.pebble.node.expression.NodeExpressionFilter;
import com.mitchellbosecke.pebble.node.expression.NodeExpressionFunctionCall;
import com.mitchellbosecke.pebble.node.expression.NodeExpressionGetAttributeOrMethod;
import com.mitchellbosecke.pebble.node.expression.NodeExpressionParentReference;
import com.mitchellbosecke.pebble.node.expression.NodeExpressionString;
import com.mitchellbosecke.pebble.node.expression.NodeExpressionTernary;
import com.mitchellbosecke.pebble.node.expression.NodeExpressionUnary;
import com.mitchellbosecke.pebble.node.expression.NodeExpressionVariableName;
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
	public ExpressionParser(Parser parser) {
		this.parser = parser;
		this.binaryOperators = parser.getEngine().getBinaryOperators();
		this.unaryOperators = parser.getEngine().getUnaryOperators();
	}

	/**
	 * The public entry point for parsing an expression.
	 * 
	 * @return NodeExpression the expression that has been parsed.
	 * @throws SyntaxException
	 */
	public NodeExpression parseExpression() throws SyntaxException {
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
	 * @throws SyntaxException
	 */
	private NodeExpression parseExpression(int minPrecedence) throws SyntaxException {

		this.stream = parser.getStream();
		Token token = stream.current();
		NodeExpression expression = null;

		/*
		 * The first check is to see if the expression begins with a unary
		 * operator, or an opening bracket, or neither.
		 */

		if (isUnary(token)) {
			UnaryOperator operator = this.unaryOperators.get(token.getValue());
			stream.next();
			expression = parseExpression(operator.getPrecedence());

			NodeExpressionUnary unaryExpression = null;
			Class<? extends NodeExpressionUnary> operatorNodeClass = operator.getNodeClass();
			try {
				unaryExpression = operatorNodeClass.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			unaryExpression.setLineNumber(stream.current().getLineNumber());
			unaryExpression.setNode(expression);

			expression = unaryExpression;

		} else if (token.test(Token.Type.PUNCTUATION, "(")) {

			stream.next();
			expression = parseExpression();
			stream.expect(Token.Type.PUNCTUATION, ")", "An opened parenthesis is not properly closed");
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

			/*
			 * parse the expression on the right hand side of the operator while
			 * maintaining proper associativity and precedence
			 */
			NodeExpression expressionRight = parseExpression(Associativity.LEFT.equals(operator.getAssociativity()) ? operator
					.getPrecedence() + 1 : operator.getPrecedence());

			/*
			 * we have to wrap the left and right side expressions into one
			 * final expression. The operator provides us with the type of node
			 * we are creating (and an instance of that node type)
			 */
			NodeExpressionBinary finalExpression = null;
			Class<? extends NodeExpressionBinary> operatorNodeClass = operator.getNodeClass();
			try {
				finalExpression = operatorNodeClass.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			finalExpression.setLineNumber(stream.current().getLineNumber());
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
	 * @throws SyntaxException
	 */
	private NodeExpression subparseExpression() throws SyntaxException {
		Token token = stream.current();
		NodeExpression node = null;

		switch (token.getType()) {

			case NAME:
				switch (token.getValue()) {

				// a constant?
					case "true":
					case "TRUE":
						node = new NodeExpressionConstant(token.getLineNumber(), true);
						break;
					case "false":
					case "FALSE":
						node = new NodeExpressionConstant(token.getLineNumber(), false);
						break;
					case "none":
					case "NONE":
					case "null":
					case "NULL":
						node = new NodeExpressionConstant(token.getLineNumber(), null);
						break;

					default:

						// name of a function?
						if (stream.peek().test(Token.Type.PUNCTUATION, "(")) {
							node = new NodeExpressionConstant(token.getLineNumber(), token.getValue());
						}

						// variable name
						else {
							node = new NodeExpressionVariableName(token.getLineNumber(), token.getValue());
						}
						break;
				}
				break;

			case NUMBER:
				node = new NodeExpressionConstant(token.getLineNumber(), token.getValue());
				break;

			case STRING:
				node = new NodeExpressionString(token.getLineNumber(), (String) token.getValue());
				break;

			// not found, syntax error
			default:
				throw new SyntaxException(String.format("Unexpected token \"%s\" of value \"%s\"", token.getType()
						.toString(), token.getValue()), token.getLineNumber(), stream.getFilename());
		}

		// there may or may not be more to this expression - let's keep looking
		stream.next();
		return parsePostfixExpression(node);
	}

	private NodeExpression parseTernaryExpression(NodeExpression expression) throws SyntaxException {
		while (this.stream.current().test(Token.Type.PUNCTUATION, "?")) {

			int lineNumber = stream.current().getLineNumber();

			stream.next();

			NodeExpression expression2 = null;
			NodeExpression expression3 = null;

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

			expression = new NodeExpressionTernary(lineNumber, expression, expression2, expression3);
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
	 * @throws SyntaxException
	 */
	private NodeExpression parsePostfixExpression(NodeExpression node) throws SyntaxException {
		Token current;
		while (true) {
			current = stream.current();

			if (current.test(Token.Type.PUNCTUATION, ".")) {

				// a period represents getting an attribute from a variable or
				// calling a method
				node = parseSubscriptExpression(node);

			} else if (current.test(Token.Type.PUNCTUATION, "|")) {

				// handle the filter operator
				node = parseFilterExpression(node);

			} else if (current.test(Token.Type.PUNCTUATION, "(")) {

				// function call
				node = parseFunctionExpression(node);

			} else {
				break;
			}
		}
		return node;
	}

	private NodeExpression parseFunctionExpression(NodeExpression node) throws SyntaxException {
		TokenStream stream = parser.getStream();
		int lineNumber = stream.current().getLineNumber();

		NodeExpressionConstant functionName = (NodeExpressionConstant) node;
		NodeExpressionArguments args = parseArguments();

		/*
		 * The following core functions have their own Nodes
		 * and are compiled in unique ways for the sake of 
		 * performance.
		 */
		switch ((String) functionName.getValue()) {
			case "parent":
				String parentClassName = this.parser.getEngine().getTemplateClassName(this.parser.getParentFileName());
				return new NodeExpressionParentReference(node.getLineNumber(), parentClassName, parser.peekBlockStack());
			case "block":
				String blockName = (String) ((NodeExpressionString) args.getArgs()[0]).getValue();
				return new NodeExpressionBlockReference(node.getLineNumber(), blockName, true);
		}
		

		return new NodeExpressionFunctionCall(lineNumber, functionName, args);
	}

	private NodeExpression parseFilterExpression(NodeExpression node) throws SyntaxException {
		TokenStream stream = parser.getStream();
		int lineNumber = stream.current().getLineNumber();

		// skip over the | character
		stream.next();

		while (true) {
			Token filterToken = stream.expect(Token.Type.NAME);

			NodeExpressionConstant filterName = new NodeExpressionConstant(filterToken.getLineNumber(),
					filterToken.getValue());

			NodeExpressionArguments args = null;
			if (stream.current().test(Token.Type.PUNCTUATION, "(")) {
				args = this.parseArguments();
			}

			node = new NodeExpressionFilter(lineNumber, node, filterName, args);

			if (!stream.current().test(Token.Type.PUNCTUATION, "|")) {
				break;
			} else {
				// skip over the | character and the while loop will continue
				stream.next();
			}
		}

		return node;
	}

	/**
	 * A subscript expression can either be an expression getting an attribute
	 * from a variable, or calling a method from a variable.
	 * 
	 * @param node
	 *            The expression parsed so far
	 * @return NodeExpression The parsed subscript expression
	 * @throws SyntaxException
	 */
	private NodeExpression parseSubscriptExpression(NodeExpression node) throws SyntaxException {
		TokenStream stream = parser.getStream();
		int lineNumber = stream.current().getLineNumber();

		if (stream.current().test(Token.Type.PUNCTUATION, ".")) {

			// skip over the '.' token
			stream.next();

			Token token = stream.expect(Token.Type.NAME);

			NodeExpressionConstant constant = new NodeExpressionConstant(token.getLineNumber(), token.getValue());

			if (stream.current().test(Token.Type.PUNCTUATION, "(")) {

				NodeExpressionArguments arguments = this.parseArguments();
				node = new NodeExpressionGetAttributeOrMethod(lineNumber,
						NodeExpressionGetAttributeOrMethod.Type.METHOD, node, constant, arguments);

			} else {
				node = new NodeExpressionGetAttributeOrMethod(lineNumber, NodeExpressionGetAttributeOrMethod.Type.ANY,
						node, constant);
			}

		}
		return node;
	}

	public NodeExpressionArguments parseArguments() throws SyntaxException {
		return parseArguments(false);
	}

	public NodeExpressionArguments parseArguments(boolean isMethodDefinition) throws SyntaxException {
		List<NodeExpression> vars = new ArrayList<>();
		this.stream = this.parser.getStream();

		int lineNumber = stream.current().getLineNumber();

		stream.expect(Token.Type.PUNCTUATION, "(");

		while (!stream.current().test(Token.Type.PUNCTUATION, ")")) {

			if (!vars.isEmpty()) {
				stream.expect(Token.Type.PUNCTUATION, ",");
			}

			if (isMethodDefinition) {
				Token token = stream.expect(Token.Type.NAME);
				vars.add(new NodeExpressionDeclaration(token.getLineNumber(), token.getValue()));
			} else {
				vars.add(parseExpression());
			}

		}

		stream.expect(Token.Type.PUNCTUATION, ")");

		return new NodeExpressionArguments(lineNumber, vars.toArray(new NodeExpression[vars.size()]));
	}

	public NodeExpressionDeclaration parseDeclarationExpression() throws SyntaxException {

		// set the stream because this function may be called externally
		this.stream = this.parser.getStream();
		Token token = stream.current();
		token.test(Token.Type.NAME);

		String[] reserved = new String[] { "true", "false", "null", "none" };
		if (Arrays.asList(reserved).contains(token.getValue())) {
			throw new SyntaxException(String.format("Can not assign a value to %s", token.getValue()),
					token.getLineNumber(), stream.getFilename());
		}

		stream.next();
		return new NodeExpressionDeclaration(token.getLineNumber(), token.getValue());
	}
}
