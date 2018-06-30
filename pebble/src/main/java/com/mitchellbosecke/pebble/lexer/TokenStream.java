/*
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.mitchellbosecke.pebble.lexer;

import com.mitchellbosecke.pebble.error.ParserException;
import com.mitchellbosecke.pebble.lexer.Token.Type;
import java.util.ArrayList;
import java.util.Collection;

public class TokenStream {

  private ArrayList<Token> tokens = new ArrayList<>();

  private int current;

  private String filename;

  /**
   * Constructor for a Token Stream
   *
   * @param tokens A collection of tokens
   * @param name The filename of the template that these tokens came from
   */
  public TokenStream(Collection<Token> tokens, String name) {
    this.tokens.addAll(tokens);
    this.current = 0;
    this.filename = name;
  }

  /**
   * Consumes and returns the next token in the stream.
   *
   * @return The next token
   */
  public Token next() {
    return this.tokens.get(++this.current);
  }

  /**
   * Checks the current token to see if it matches the provided type. If it doesn't match this will
   * throw a SyntaxException. This will consume a token.
   *
   * @param type The type of token that we expect
   * @return Token The current token
   */
  public Token expect(Token.Type type) {
    return this.expect(type, null);
  }

  /**
   * Checks the current token to see if it matches the provided type. If it doesn't match this will
   * throw a SyntaxException. This will consume a token.
   *
   * @param type The type of token that we expect
   * @param value The expected value of the token
   * @return Token The current token
   */
  public Token expect(Token.Type type, String value) {
    Token token = this.tokens.get(this.current);

    boolean success = value == null ? token.test(type) : token.test(type, value);

    if (!success) {
      String message = String
          .format("Unexpected token of value \"%s\" and type %s, expected token of type %s",
              token.getValue(), token.getType().toString(), type);
      throw new ParserException(null, message, token.getLineNumber(), this.filename);
    }
    this.next();
    return token;
  }

  /**
   * Returns the next token in the stream without consuming it.
   *
   * @return The next token
   */
  public Token peek() {
    return this.peek(1);
  }

  /**
   * Returns a future token in the stream without consuming any.
   *
   * @param number How many tokens to lookahead
   * @return The token we are peeking at
   */
  public Token peek(int number) {
    return this.tokens.get(this.current + number);
  }

  public boolean isEOF() {
    return this.tokens.get(this.current).getType().equals(Type.EOF);
  }

  @Override
  public String toString() {
    return String.format("Current: %s. All: %s", this.current(), this.tokens);
  }

  /**
   * Looks at the current token. Does not consume the token.
   *
   * @return Token The current token
   */
  public Token current() {
    return this.tokens.get(this.current);
  }

  public String getFilename() {
    return this.filename;
  }

  /**
   * used for testing purposes
   *
   * @return List of tokens
   */
  public ArrayList<Token> getTokens() {
    return this.tokens;
  }
}
