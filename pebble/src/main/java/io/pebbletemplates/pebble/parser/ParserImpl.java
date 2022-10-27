/*
 * This file is part of Pebble.
 * <p>
 * Copyright (c) 2014 by Mitchell Bösecke
 * <p>
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package io.pebbletemplates.pebble.parser;

import io.pebbletemplates.pebble.operator.BinaryOperator;
import io.pebbletemplates.pebble.operator.UnaryOperator;
import io.pebbletemplates.pebble.error.ParserException;
import io.pebbletemplates.pebble.lexer.Token;
import io.pebbletemplates.pebble.lexer.TokenStream;
import io.pebbletemplates.pebble.node.BodyNode;
import io.pebbletemplates.pebble.node.PrintNode;
import io.pebbletemplates.pebble.node.RenderableNode;
import io.pebbletemplates.pebble.node.RootNode;
import io.pebbletemplates.pebble.node.TextNode;
import io.pebbletemplates.pebble.node.expression.Expression;
import io.pebbletemplates.pebble.tokenParser.TokenParser;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ParserImpl implements Parser {

  /**
   * Binary operators
   */
  private final Map<String, BinaryOperator> binaryOperators;

  /**
   * Unary operators
   */
  private final Map<String, UnaryOperator> unaryOperators;

  /**
   * Token parsers
   */
  private final Map<String, TokenParser> tokenParsers;

  /**
   * An expression parser.
   */
  private ExpressionParser expressionParser;

  /**
   * The TokenStream that we are converting into an Abstract Syntax Tree.
   */
  private TokenStream stream;

  /**
   * TokenParser objects provided by the extensions.
   */

  /**
   * used to keep track of the name of the block that we are currently inside of. This is purely
   * just for the parent() function.
   */
  private LinkedList<String> blockStack;

  /**
   * parser options
   */
  private ParserOptions parserOptions;

  /**
   * Constructor
   *
   * @param binaryOperators A map of binary operators
   * @param unaryOperators A map of unary operators
   * @param tokenParsers A map of token parsers
   */
  public ParserImpl(Map<String, UnaryOperator> unaryOperators,
      Map<String, BinaryOperator> binaryOperators,
      Map<String, TokenParser> tokenParsers, ParserOptions parserOptions) {
    this.binaryOperators = binaryOperators;
    this.unaryOperators = unaryOperators;
    this.tokenParsers = tokenParsers;
    this.parserOptions = parserOptions;
  }

  @Override
  public RootNode parse(TokenStream stream) {

    // expression parser
    this.expressionParser = new ExpressionParser(this, this.binaryOperators, this.unaryOperators,
        this.parserOptions);

    this.stream = stream;

    this.blockStack = new LinkedList<>();

    BodyNode body = this.subparse();

    return new RootNode(body);
  }

  @Override
  public BodyNode subparse() {
    return this.subparse(null);
  }

  @Override
  /**
   * The main method for the parser. This method does the work of converting
   * a TokenStream into a Node
   *
   * @param stopCondition    A stopping condition provided by a token parser
   * @return Node        The root node of the generated Abstract Syntax Tree
   */ public BodyNode subparse(StoppingCondition stopCondition) {

    // these nodes will be the children of the root node
    List<RenderableNode> nodes = new ArrayList<>();

    Token token;
    while (!this.stream.isEOF()) {

      switch (this.stream.current().getType()) {
        case TEXT:

          /*
           * The current token is a text token. Not much to do here other
           * than convert it to a text Node.
           */
          token = this.stream.current();
          nodes.add(new TextNode(token.getValue(), token.getLineNumber()));
          this.stream.next();
          break;

        case PRINT_START:

          /*
           * We are entering a print delimited region at this point. These
           * regions will contain some sort of expression so let's pass
           * control to our expression parser.
           */

          // go to the next token because the current one is just the
          // opening delimiter
          token = this.stream.next();

          Expression<?> expression = this.expressionParser.parseExpression();
          nodes.add(new PrintNode(expression, token.getLineNumber()));

          // we expect to see a print closing delimiter
          this.stream.expect(Token.Type.PRINT_END);

          break;

        case EXECUTE_START:

          // go to the next token because the current one is just the
          // opening delimiter
          this.stream.next();

          token = this.stream.current();

          /*
           * We expect a name token at the beginning of every block.
           *
           * We do not use stream.expect() because it consumes the current
           * token. The current token may be needed by a token parser
           * which has provided a stopping condition. Ex. the 'if' token
           * parser may need to check if the current token is either
           * 'endif' or 'else' and act accordingly, thus we should not
           * consume it.
           */
          if (!Token.Type.NAME.equals(token.getType())) {
            throw new ParserException(null, "A block must start with a tag name.",
                token.getLineNumber(),
                this.stream.getFilename());
          }

          // If this method was executed using a TokenParser and
          // that parser provided a stopping condition (ex. checking
          // for the 'endif' token) let's check for that condition
          // now.
          if (stopCondition != null && stopCondition.evaluate(token)) {
            return new BodyNode(token.getLineNumber(), nodes);
          }

          // find an appropriate parser for this name
          TokenParser tokenParser = this.tokenParsers.get(token.getValue());

          if (tokenParser == null) {
            throw new ParserException(null,
                String.format("Unexpected tag name \"%s\"", token.getValue()),
                token.getLineNumber(), this.stream.getFilename());
          }

          RenderableNode node = tokenParser.parse(token, this);

          // node might be null (ex. "extend" token parser)
          if (node != null) {
            nodes.add(node);
          }

          break;

        default:
          throw new ParserException(null, "Parser ended in undefined state.",
              this.stream.current().getLineNumber(),
              this.stream.getFilename());
      }
    }

    // create the root node with the children that we have found
    return new BodyNode(this.stream.current().getLineNumber(), nodes);
  }

  @Override
  public TokenStream getStream() {
    return this.stream;
  }

  public void setStream(TokenStream stream) {
    this.stream = stream;
  }

  @Override
  public ExpressionParser getExpressionParser() {
    return this.expressionParser;
  }

  @Override
  public String peekBlockStack() {
    return this.blockStack.peek();
  }

  @Override
  public String popBlockStack() {
    return this.blockStack.pop();
  }

  @Override
  public void pushBlockStack(String blockName) {
    this.blockStack.push(blockName);
  }
}
