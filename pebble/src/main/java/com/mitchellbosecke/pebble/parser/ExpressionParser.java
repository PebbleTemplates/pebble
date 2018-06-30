/*
 * This file is part of Pebble.
 * <p>
 * Copyright (c) 2014 by Mitchell Bösecke
 * <p>
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.mitchellbosecke.pebble.parser;

import com.mitchellbosecke.pebble.error.ParserException;
import com.mitchellbosecke.pebble.lexer.Token;
import com.mitchellbosecke.pebble.lexer.TokenStream;
import com.mitchellbosecke.pebble.node.ArgumentsNode;
import com.mitchellbosecke.pebble.node.FunctionOrMacroNameNode;
import com.mitchellbosecke.pebble.node.NamedArgumentNode;
import com.mitchellbosecke.pebble.node.PositionalArgumentNode;
import com.mitchellbosecke.pebble.node.TestInvocationExpression;
import com.mitchellbosecke.pebble.node.expression.ArrayExpression;
import com.mitchellbosecke.pebble.node.expression.BinaryExpression;
import com.mitchellbosecke.pebble.node.expression.BlockFunctionExpression;
import com.mitchellbosecke.pebble.node.expression.ConcatenateExpression;
import com.mitchellbosecke.pebble.node.expression.ContextVariableExpression;
import com.mitchellbosecke.pebble.node.expression.Expression;
import com.mitchellbosecke.pebble.node.expression.FilterExpression;
import com.mitchellbosecke.pebble.node.expression.FilterInvocationExpression;
import com.mitchellbosecke.pebble.node.expression.FunctionOrMacroInvocationExpression;
import com.mitchellbosecke.pebble.node.expression.GetAttributeExpression;
import com.mitchellbosecke.pebble.node.expression.LiteralBooleanExpression;
import com.mitchellbosecke.pebble.node.expression.LiteralDoubleExpression;
import com.mitchellbosecke.pebble.node.expression.LiteralIntegerExpression;
import com.mitchellbosecke.pebble.node.expression.LiteralLongExpression;
import com.mitchellbosecke.pebble.node.expression.LiteralNullExpression;
import com.mitchellbosecke.pebble.node.expression.LiteralStringExpression;
import com.mitchellbosecke.pebble.node.expression.MapExpression;
import com.mitchellbosecke.pebble.node.expression.NegativeTestExpression;
import com.mitchellbosecke.pebble.node.expression.ParentFunctionExpression;
import com.mitchellbosecke.pebble.node.expression.PositiveTestExpression;
import com.mitchellbosecke.pebble.node.expression.TernaryExpression;
import com.mitchellbosecke.pebble.node.expression.UnaryExpression;
import com.mitchellbosecke.pebble.operator.Associativity;
import com.mitchellbosecke.pebble.operator.BinaryOperator;
import com.mitchellbosecke.pebble.operator.UnaryOperator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Parses expressions.
 */
public class ExpressionParser {

  private static final Set<String> RESERVED_KEYWORDS = new HashSet<>(
      Arrays.asList("true", "false", "null", "none"));

  private final Parser parser;

  private TokenStream stream;

  private Map<String, BinaryOperator> binaryOperators;

  private Map<String, UnaryOperator> unaryOperators;

  private ParserOptions parserOptions;

  /**
   * Constructor
   *
   * @param parser A reference to the main parser
   * @param binaryOperators All the binary operators
   * @param unaryOperators All the unary operators
   */
  public ExpressionParser(Parser parser, Map<String, BinaryOperator> binaryOperators,
      Map<String, UnaryOperator> unaryOperators, ParserOptions parserOptions) {
    this.parser = parser;
    this.binaryOperators = binaryOperators;
    this.unaryOperators = unaryOperators;
    this.parserOptions = parserOptions;
  }

  /**
   * The public entry point for parsing an expression.
   *
   * @return NodeExpression the expression that has been parsed.
   */
  public Expression<?> parseExpression() {
    return this.parseExpression(0);
  }

  /**
   * A private entry point for parsing an expression. This method takes in the precedence required
   * to operate a "precedence climbing" parsing algorithm. It is a recursive method.
   *
   * @return The NodeExpression representing the parsed expression.
   * @see "http://en.wikipedia.org/wiki/Operator-precedence_parser"
   */
  private Expression<?> parseExpression(int minPrecedence) {

    this.stream = this.parser.getStream();
    Token token = this.stream.current();
    Expression<?> expression;

    /*
     * The first check is to see if the expression begins with a unary
     * operator, or an opening bracket, or neither.
     */
    if (this.isUnary(token)) {
      UnaryOperator operator = this.unaryOperators.get(token.getValue());
      this.stream.next();
      expression = this.parseExpression(operator.getPrecedence());

      UnaryExpression unaryExpression;
      Class<? extends UnaryExpression> operatorNodeClass = operator.getNodeClass();
      try {
        unaryExpression = operatorNodeClass.newInstance();
        unaryExpression.setLineNumber(this.stream.current().getLineNumber());
      } catch (InstantiationException | IllegalAccessException e) {
        throw new RuntimeException(e);
      }
      unaryExpression.setChildExpression(expression);

      expression = unaryExpression;

    } else if (token.test(Token.Type.PUNCTUATION, "(")) {

      this.stream.next();
      expression = this.parseExpression();
      this.stream.expect(Token.Type.PUNCTUATION, ")");
      expression = this.parsePostfixExpression(expression);

    }
    // array definition syntax
    else if (token.test(Token.Type.PUNCTUATION, "[")) {

      // preserve [ token for array parsing
      expression = this.parseArrayDefinitionExpression();
      // don't expect ], because it has been already expected
      // currently, postfix expressions are not supported for arrays
      // expression = parsePostfixExpression(expression);
    }
    // map definition syntax
    else if (token.test(Token.Type.PUNCTUATION, "{")) {

      // preserve { token for map parsing
      expression = this.parseMapDefinitionExpression();
      // don't expect }, because it has been already expected
      // currently, postfix expressions are not supported for maps
      // expression = parsePostfixExpression(expression);

    } else {
      /*
       * starts with neither. Let's parse out the first expression that we
       * can find. There may be one, there may be many (separated by
       * binary operators); right now we are just looking for the first.
       */
      expression = this.subparseExpression();
    }

    /*
     * If, after parsing the first expression we encounter a binary operator
     * then we know we have another expression on the other side of the
     * operator that requires parsing. Otherwise we're done.
     */
    token = this.stream.current();
    while (this.isBinary(token)
        && this.binaryOperators.get(token.getValue()).getPrecedence() >= minPrecedence) {

      // find out which operator we are dealing with and then skip over it
      BinaryOperator operator = this.binaryOperators.get(token.getValue());
      this.stream.next();

      Expression<?> expressionRight;

      // the right hand expression of the FILTER operator is handled in a
      // unique way
      if (FilterExpression.class.equals(operator.getNodeClass())) {
        expressionRight = this.parseFilterInvocationExpression();
      }
      // the right hand expression of TEST operators is handled in a
      // unique way
      else if (PositiveTestExpression.class.equals(operator.getNodeClass())
          || NegativeTestExpression.class.equals(operator.getNodeClass())) {
        expressionRight = this.parseTestInvocationExpression();
      } else {
        /*
         * parse the expression on the right hand side of the operator
         * while maintaining proper associativity and precedence
         */
        expressionRight = this.parseExpression(
            Associativity.LEFT.equals(operator.getAssociativity()) ? operator
                .getPrecedence() + 1 : operator.getPrecedence());
      }

      /*
       * we have to wrap the left and right side expressions into one
       * final expression. The operator provides us with the type of
       * expression we are creating.
       */
      BinaryExpression<?> finalExpression;
      Class<? extends BinaryExpression<?>> operatorNodeClass = operator.getNodeClass();
      try {
        finalExpression = operatorNodeClass.newInstance();
        finalExpression.setLineNumber(this.stream.current().getLineNumber());
      } catch (InstantiationException | IllegalAccessException e) {
        throw new ParserException(e,
            "Error instantiating operator node [" + operatorNodeClass.getName() + "]",
            token.getLineNumber(), this.stream.getFilename());
      }

      finalExpression.setLeft(expression);
      finalExpression.setRight(expressionRight);

      expression = finalExpression;

      token = this.stream.current();
    }

    if (minPrecedence == 0) {
      return this.parseTernaryExpression(expression);
    }

    return expression;
  }

  /**
   * Checks if a token is a unary operator.
   *
   * @param token The token that we are checking
   * @return boolean Whether the token is a unary operator or not
   */
  private boolean isUnary(Token token) {
    return token.test(Token.Type.OPERATOR) && this.unaryOperators.containsKey(token.getValue());
  }

  /**
   * Checks if a token is a binary operator.
   *
   * @param token The token that we are checking
   * @return boolean Whether the token is a binary operator or not
   */
  private boolean isBinary(Token token) {
    return token.test(Token.Type.OPERATOR) && this.binaryOperators.containsKey(token.getValue());
  }

  /**
   * Finds and returns the next "simple" expression; an expression of which can be found on either
   * side of a binary operator but does not contain a binary operator. Ex. "var.field", "true",
   * "12", etc.
   *
   * @return NodeExpression The expression that it found.
   */
  private Expression<?> subparseExpression() {
    final Token token = this.stream.current();
    Expression<?> node;

    switch (token.getType()) {

      case NAME:
        switch (token.getValue()) {

          // a constant?
          case "true":
          case "TRUE":
            node = new LiteralBooleanExpression(true, token.getLineNumber());
            this.stream.next();
            break;
          case "false":
          case "FALSE":
            node = new LiteralBooleanExpression(false, token.getLineNumber());
            this.stream.next();
            break;
          case "none":
          case "NONE":
          case "null":
          case "NULL":
            node = new LiteralNullExpression(token.getLineNumber());
            this.stream.next();
            break;

          default:

            // name of a function?
            if (this.stream.peek().test(Token.Type.PUNCTUATION, "(")) {
              node = new FunctionOrMacroNameNode(token.getValue(),
                  this.stream.peek().getLineNumber());
            }

            // variable name
            else {
              node = new ContextVariableExpression(token.getValue(), token.getLineNumber());
            }
            this.stream.next();
            break;
        }
        break;

      case LONG:
        final String longValue = token.getValue();
        node = new LiteralLongExpression(Long.valueOf(longValue), token.getLineNumber());
        this.stream.next();
        break;

      case NUMBER:
        final String numberValue = token.getValue();
        if (numberValue.contains(".")) {
          node = new LiteralDoubleExpression(Double.valueOf(numberValue), token.getLineNumber());
        } else {
          if (this.parserOptions.isLiteralDecimalTreatedAsInteger()) {
            node = new LiteralIntegerExpression(Integer.valueOf(numberValue),
                token.getLineNumber());
          } else {
            node = new LiteralLongExpression(Long.valueOf(numberValue), token.getLineNumber());
          }
        }
        this.stream.next();
        break;

      case STRING:
      case STRING_INTERPOLATION_START:
        node = this.parseStringExpression();
        break;

      // not found, syntax error
      default:
        throw new ParserException(null,
            String.format("Unexpected token \"%s\" of value \"%s\"", token.getType()
                .toString(), token.getValue()), token.getLineNumber(), this.stream.getFilename());
    }

    // there may or may not be more to this expression - let's keep looking
    return this.parsePostfixExpression(node);
  }

  private Expression<?> parseStringExpression() throws ParserException {
    List<Expression<?>> nodes = new ArrayList<>();

    // Sequential strings are not OK, but strings can follow interpolation
    while (true) {
      if (this.stream.current().test(Token.Type.STRING)) {
        Token token = this.stream.expect(Token.Type.STRING);
        nodes.add(new LiteralStringExpression(token.getValue(), token.getLineNumber()));
      } else if (this.stream.current().test(Token.Type.STRING_INTERPOLATION_START)) {
        this.stream.expect(Token.Type.STRING_INTERPOLATION_START);
        nodes.add(this.parseExpression());
        this.stream.expect(Token.Type.STRING_INTERPOLATION_END);
      } else {
        break;
      }
    }

    Expression<?> first = nodes.remove(0);
    if (nodes.isEmpty()) {
      return first;
    }

    ConcatenateExpression expr, firstExpr;
    expr = firstExpr = new ConcatenateExpression(first, null);

    for (int i = 0; i < nodes.size(); i++) {
      Expression<?> node = nodes.get(i);
      if (i == nodes.size() - 1) {
        expr.setRight(node);
      } else {
        ConcatenateExpression newExpr = new ConcatenateExpression(node, null);
        expr.setRight(newExpr);
        expr = newExpr;
      }
    }

    return firstExpr;
  }

  @SuppressWarnings("unchecked")
  private Expression<?> parseTernaryExpression(Expression<?> expression) {
    // if the next token isn't a ?, we're not dealing with a ternary
    // expression
    if (!this.stream.current().test(Token.Type.PUNCTUATION, "?")) {
      return expression;
    }

    this.stream.next();
    Expression<?> expression2 = this.parseExpression();
    this.stream.expect(Token.Type.PUNCTUATION, ":");
    Expression<?> expression3 = this.parseExpression();

    expression = new TernaryExpression((Expression<Boolean>) expression, expression2, expression3,
        this.stream
            .current().getLineNumber(), this.stream.getFilename());
    return expression;
  }

  /**
   * Determines if there is more to the provided expression than we originally thought. We will look
   * for the filter operator or perhaps we are getting an attribute from a variable (ex.
   * var.attribute or var['attribute'] or var.attribute(bar)).
   *
   * @param node The expression that we have already discovered
   * @return Either the original expression that was passed in or a slightly modified version of it,
   * depending on what was discovered.
   */
  private Expression<?> parsePostfixExpression(Expression<?> node) {
    Token current;
    while (true) {
      current = this.stream.current();

      if (current.test(Token.Type.PUNCTUATION, ".") || current.test(Token.Type.PUNCTUATION, "[")) {

        // a period represents getting an attribute from a variable or
        // calling a method
        node = this.parseBeanAttributeExpression(node);

      } else if (current.test(Token.Type.PUNCTUATION, "(")) {

        // function call
        node = this.parseFunctionOrMacroInvocation(node);

      } else {
        break;
      }
    }
    return node;
  }

  private Expression<?> parseFunctionOrMacroInvocation(Expression<?> node) {
    String functionName = ((FunctionOrMacroNameNode) node).getName();
    ArgumentsNode args = this.parseArguments();

    /*
     * The following core functions have their own Nodes and are rendered in
     * unique ways for the sake of performance.
     */
    switch (functionName) {
      case "parent":
        return new ParentFunctionExpression(this.parser.peekBlockStack(),
            this.stream.current().getLineNumber());
      case "block":
        return new BlockFunctionExpression(args, node.getLineNumber());
    }

    return new FunctionOrMacroInvocationExpression(functionName, args, node.getLineNumber());
  }

  public FilterInvocationExpression parseFilterInvocationExpression() {
    TokenStream stream = this.parser.getStream();
    Token filterToken = stream.expect(Token.Type.NAME);

    ArgumentsNode args;
    if (stream.current().test(Token.Type.PUNCTUATION, "(")) {
      args = this.parseArguments();
    } else {
      args = new ArgumentsNode(null, null, filterToken.getLineNumber());
    }

    return new FilterInvocationExpression(filterToken.getValue(), args,
        filterToken.getLineNumber());
  }

  private Expression<?> parseTestInvocationExpression() {
    TokenStream stream = this.parser.getStream();
    int lineNumber = stream.current().getLineNumber();

    Token testToken = stream.expect(Token.Type.NAME);

    ArgumentsNode args;
    if (stream.current().test(Token.Type.PUNCTUATION, "(")) {
      args = this.parseArguments();
    } else {
      args = new ArgumentsNode(null, null, testToken.getLineNumber());
    }

    return new TestInvocationExpression(lineNumber, testToken.getValue(), args);
  }

  /**
   * A bean attribute expression can either be an expression getting an attribute from a variable in
   * the context, or calling a method from a variable.
   *
   * Ex. foo.bar or foo['bar'] or foo.bar('baz')
   *
   * @param node The expression parsed so far
   * @return NodeExpression The parsed subscript expression
   */
  private Expression<?> parseBeanAttributeExpression(Expression<?> node) {
    TokenStream stream = this.parser.getStream();

    if (stream.current().test(Token.Type.PUNCTUATION, ".")) {

      // skip over the '.' token
      stream.next();

      Token token = stream.expect(Token.Type.NAME);

      ArgumentsNode args = null;
      if (stream.current().test(Token.Type.PUNCTUATION, "(")) {
        args = this.parseArguments();
        if (!args.getNamedArgs().isEmpty()) {
          throw new ParserException(null, "Can not use named arguments when calling a bean method",
              stream
                  .current().getLineNumber(), stream.getFilename());
        }
      }

      node = new GetAttributeExpression(node,
          new LiteralStringExpression(token.getValue(), token.getLineNumber()), args,
          stream.getFilename(), token.getLineNumber());

    } else if (stream.current().test(Token.Type.PUNCTUATION, "[")) {
      // skip over opening '[' bracket
      stream.next();

      node = new GetAttributeExpression(node, this.parseExpression(), stream.getFilename(),
          stream.current()
              .getLineNumber());

      // move past the closing ']' bracket
      stream.expect(Token.Type.PUNCTUATION, "]");
    }

    return node;
  }

  private ArgumentsNode parseArguments() {
    return this.parseArguments(false);
  }

  public ArgumentsNode parseArguments(boolean isMacroDefinition) {

    List<PositionalArgumentNode> positionalArgs = new ArrayList<>();
    List<NamedArgumentNode> namedArgs = new ArrayList<>();
    this.stream = this.parser.getStream();

    this.stream.expect(Token.Type.PUNCTUATION, "(");

    while (!this.stream.current().test(Token.Type.PUNCTUATION, ")")) {

      String argumentName = null;
      Expression<?> argumentValue = null;

      if (!namedArgs.isEmpty() || !positionalArgs.isEmpty()) {
        this.stream.expect(Token.Type.PUNCTUATION, ",");
      }

      /*
       * Most arguments consist of VALUES with optional NAMES but in the
       * case of a macro definition the user is specifying NAMES with
       * optional VALUES. Therefore the logic changes slightly.
       */
      if (isMacroDefinition) {
        argumentName = this.parseNewVariableName();
        if (this.stream.current().test(Token.Type.PUNCTUATION, "=")) {
          this.stream.expect(Token.Type.PUNCTUATION, "=");
          argumentValue = this.parseExpression();
        }
      } else {
        if (this.stream.peek().test(Token.Type.PUNCTUATION, "=")) {
          argumentName = this.parseNewVariableName();
          this.stream.expect(Token.Type.PUNCTUATION, "=");
        }
        argumentValue = this.parseExpression();
      }

      if (argumentName == null) {
        if (!namedArgs.isEmpty()) {
          throw new ParserException(null,
              "Positional arguments must be declared before any named arguments.",
              this.stream.current()
              .getLineNumber(),
              this.stream.getFilename());
        }
        positionalArgs.add(new PositionalArgumentNode(argumentValue));
      } else {
        namedArgs.add(new NamedArgumentNode(argumentName, argumentValue));
      }

    }

    this.stream.expect(Token.Type.PUNCTUATION, ")");

    return new ArgumentsNode(positionalArgs, namedArgs, this.stream.current().getLineNumber());
  }

  /**
   * Parses a new variable that will need to be initialized in the Java code.
   *
   * This is used for the set tag, the for loop, and in named arguments.
   *
   * @return A variable name
   */
  public String parseNewVariableName() {

    // set the stream because this function may be called externally (for
    // and set token parsers)
    this.stream = this.parser.getStream();
    Token token = this.stream.current();
    token.test(Token.Type.NAME);

    if (RESERVED_KEYWORDS.contains(token.getValue())) {
      throw new ParserException(null,
          String.format("Can not assign a value to %s", token.getValue()),
          token.getLineNumber(), this.stream.getFilename());
    }

    this.stream.next();
    return token.getValue();
  }

  private Expression<?> parseArrayDefinitionExpression() {
    TokenStream stream = this.parser.getStream();

    // expect the opening bracket and check for an empty array
    stream.expect(Token.Type.PUNCTUATION, "[");
    if (stream.current().test(Token.Type.PUNCTUATION, "]")) {
      stream.next();
      return new ArrayExpression(stream.current().getLineNumber());
    }

    // there's at least one expression in the array
    List<Expression<?>> elements = new ArrayList<>();
    while (true) {
      Expression<?> expr = this.parseExpression();
      elements.add(expr);
      if (stream.current().test(Token.Type.PUNCTUATION, "]")) {
        // this seems to be the end of the array
        break;
      }
      // expect the comma separator, until we either find a closing
      // bracket or fail the expect
      stream.expect(Token.Type.PUNCTUATION, ",");
    }

    // expect the closing bracket
    stream.expect(Token.Type.PUNCTUATION, "]");

    return new ArrayExpression(elements, stream.current().getLineNumber());
  }

  private Expression<?> parseMapDefinitionExpression() {
    TokenStream stream = this.parser.getStream();

    // expect the opening brace and check for an empty map
    stream.expect(Token.Type.PUNCTUATION, "{");
    if (stream.current().test(Token.Type.PUNCTUATION, "}")) {
      stream.next();
      return new MapExpression(stream.current().getLineNumber());
    }

    // there's at least one expression in the map
    Map<Expression<?>, Expression<?>> elements = new HashMap<>();
    while (true) {
      // key : value
      Expression<?> keyExpr = this.parseExpression();
      stream.expect(Token.Type.PUNCTUATION, ":");
      Expression<?> valueExpr = this.parseExpression();
      elements.put(keyExpr, valueExpr);
      if (stream.current().test(Token.Type.PUNCTUATION, "}")) {
        // this seems to be the end of the map
        break;
      }
      // expect the comma separator, until we either find a closing brace
      // or fail the expect
      stream.expect(Token.Type.PUNCTUATION, ",");
    }

    // expect the closing brace
    stream.expect(Token.Type.PUNCTUATION, "}");

    return new MapExpression(elements, stream.current().getLineNumber());
  }

}
