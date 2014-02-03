/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Original work Copyright (c) 2009-2013 by the Twig Team
 * Modified work Copyright (c) 2013 by Mitchell Bösecke
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.lexer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.error.ParserException;
import com.mitchellbosecke.pebble.lexer.Token.Type;
import com.mitchellbosecke.pebble.operator.BinaryOperator;
import com.mitchellbosecke.pebble.operator.UnaryOperator;
import com.mitchellbosecke.pebble.utils.Pair;
import com.mitchellbosecke.pebble.utils.StringLengthComparator;
import com.mitchellbosecke.pebble.utils.StringUtils;

public class LexerImpl implements Lexer {

	/**
	 * Main components
	 */
	private final PebbleEngine engine;
	private String source;
	private String filename;

	/**
	 * Variables for keeping track of where we are located
	 */
	private int lineNumber;
	private int end;
	private int cursor;

	/**
	 * The list of tokens that we find and use to create a TokenStream
	 */
	private ArrayList<Token> tokens;

	/**
	 * Make sure every opening bracket has a closing bracket.
	 */
	private Stack<Pair<String, Integer>> brackets;

	/**
	 * The different delimiters which change the state of the lexer. The regular
	 * expressions used to find these delimiters are dynamically generated. They
	 * are safe to change via the getter/setter methods.
	 * 
	 */
	private String delimiterCommentOpen = "{#";
	private String delimiterCommentClose = "#}";
	private String delimiterExecuteOpen = "{%";
	private String delimiterExecuteClose = "%}";
	private String delimiterPrintOpen = "{{";
	private String delimiterPrintClose = "}}";

	/**
	 * The regular expressions used to find the different delimiters
	 */
	private Pattern regexPrintClose;
	private Pattern regexExecuteClose;
	private Pattern regexCommentClose;
	private Pattern regexStartDelimiters;

	/**
	 * The state of the lexer is important so that we know what to expect next
	 * and to help discover errors in the template (ex. unclosed comments).
	 */
	private State state;
	private Stack<State> states;

	private static enum State {
		DATA, EXECUTE, PRINT, COMMENT
	};

	/**
	 * Generic regular expressions for names, numbers, and punctuation.
	 */
	private static final Pattern REGEX_NAME = Pattern.compile("^[a-zA-Z_][a-zA-Z0-9_]*");
	private static final Pattern REGEX_NUMBER = Pattern.compile("^[0-9]+(\\.[0-9]+)?");
	private static final Pattern REGEX_STRING = Pattern.compile("((\").*?(?<!\\\\)(\"))|((').*?(?<!\\\\)('))",
			Pattern.DOTALL);
	private static final String PUNCTUATION = "()[]{}?:.,|=";

	/**
	 * Constructor
	 * 
	 * @param engine
	 *            The PebbleEngine that the lexer can use to get information
	 *            from
	 */
	public LexerImpl(PebbleEngine engine) {
		this.engine = engine;

		// generate the regexes used to find the individual delimiters
		this.regexPrintClose = Pattern.compile("^\\s*" + Pattern.quote(delimiterPrintClose) + "\\n?");
		this.regexExecuteClose = Pattern.compile("^\\s*" + Pattern.quote(delimiterExecuteClose) + "\\n?");
		this.regexCommentClose = Pattern.compile(Pattern.quote(delimiterCommentClose) + "\\n?");

		// Generate a special regex used to find the next START delimiters
		this.regexStartDelimiters = Pattern.compile(Pattern.quote(delimiterPrintOpen) + "|"
				+ Pattern.quote(delimiterExecuteOpen) + "|" + Pattern.quote(delimiterCommentOpen));
	}

	/**
	 * This is the main method used to tokenize the raw contents of a template.
	 * 
	 * @param source
	 *            The raw contents of the template
	 * @param name
	 *            The name of the template (used for meaningful error messages)
	 * @throws ParserException
	 */
	@Override
	public TokenStream tokenize(String source, String name) throws ParserException {

		// standardize the character used for line breaks
		this.source = source.replaceAll("(\r\n|\n)", "\n");

		/*
		 * Start in a DATA state. This state basically means that we are NOT in
		 * between a pair of meaningful delimiters.
		 */
		this.state = State.DATA;

		this.filename = name;
		this.lineNumber = 1;
		this.cursor = 0;
		this.end = source.length();
		this.tokens = new ArrayList<>();
		this.states = new Stack<>();
		this.brackets = new Stack<>();

		/*
		 * loop through the entire source and apply different lexing methods
		 * depending on what kind of state we are in at the time.
		 * 
		 * This will always start on lexData();
		 */
		while (this.cursor < this.end) {
			switch (this.state) {
				case DATA:
					lexData();
					break;
				case EXECUTE:
					lexExecute();
					break;
				case PRINT:
					lexPrint();
					break;
				case COMMENT:
					lexComment();
					break;
				default:
					break;
			}

		}

		// end of file token
		pushToken(Token.Type.EOF);

		// make sure that all brackets have been closed, else throw an error
		if (!this.brackets.isEmpty()) {
			String expected = brackets.pop().getLeft();
			throw new ParserException(null, String.format("Unclosed \"%s\"", expected), lineNumber, filename);
		}

		return new TokenStream(tokens, filename);
	}

	/**
	 * The DATA state assumes that we are current NOT in between any pair of
	 * meaningful delimiters. We are currently looking for the next "open" or
	 * "start" delimiter, ex. the opening comment delimiter, or the opening
	 * variable delimiter.
	 */
	private void lexData() {
		// find the next start delimiter
		Matcher matcher = regexStartDelimiters.matcher(source);
		boolean match = matcher.find(cursor);

		// check for EOF
		if (!match) {
			pushToken(Token.Type.TEXT, source.substring(cursor));
			cursor = end;
			return;
		}

		// push the text that we're about to skip over
		String text = source.substring(cursor, matcher.start());
		pushToken(Type.TEXT, text);

		// get the individual delimiter, we still don't know which one it was
		String token = source.substring(matcher.start(), matcher.end());

		moveCursor(text + token);

		if (delimiterCommentOpen.equals(token)) {

			// we don't actually push any tokens for comments
			pushState(State.COMMENT);

		} else if (delimiterPrintOpen.equals(token)) {

			pushToken(Token.Type.PRINT_START);
			pushState(State.PRINT);

		} else if (delimiterExecuteOpen.equals(token)) {

			pushToken(Token.Type.EXECUTE_START);
			pushState(State.EXECUTE);

		}

	}

	/**
	 * Tokenizes between execute delimiters.
	 * 
	 * @throws ParserException
	 */
	private void lexExecute() throws ParserException {
		Matcher matcher = regexExecuteClose.matcher(source.substring(cursor));

		// check if we are at the execute closing delimiter
		if (brackets.isEmpty() && matcher.lookingAt()) {
			String token = source.substring(cursor, cursor + matcher.end());
			pushToken(Token.Type.EXECUTE_END);
			moveCursor(token);
			popState();
		} else {
			lexExpression();
		}
	}

	/**
	 * Tokenizes between print delimiters.
	 * 
	 * @throws ParserException
	 */
	private void lexPrint() throws ParserException {
		Matcher matcher = regexPrintClose.matcher(source.substring(cursor));

		// check if we are at the print closing delimiter
		if (brackets.isEmpty() && matcher.lookingAt()) {
			String token = source.substring(cursor, cursor + matcher.end());
			pushToken(Token.Type.PRINT_END);
			moveCursor(token);
			popState();
		} else {
			lexExpression();
		}
	}

	/**
	 * Tokenizes between comment delimiters.
	 * 
	 * Simply find the closing delimiter for the comment and move the cursor to
	 * that point.
	 * 
	 * @throws ParserException
	 */
	private void lexComment() throws ParserException {

		// all we need to do is find the end of the comment.
		Matcher matcher = regexCommentClose.matcher(source);

		boolean match = matcher.find(cursor);
		if (!match) {
			throw new ParserException(null, "Unclosed comment.", lineNumber, filename);
		}

		// move cursor to end of comment delimiter
		String commentWithEndTag = source.substring(cursor, matcher.end());
		moveCursor(commentWithEndTag);
		popState();
	}

	/**
	 * Tokenizing an expression which can be found within both execute and print
	 * regions.
	 * 
	 * @throws ParserException
	 */
	private void lexExpression() throws ParserException {
		String token;

		// whitespace
		Pattern whitespace = Pattern.compile("^\\s+");
		Matcher matcher = whitespace.matcher(source.substring(cursor));
		if (matcher.lookingAt()) {
			moveCursor(source.substring(cursor, cursor + matcher.end()));
		}

		// operators
		Pattern operators = getOperatorRegex();
		matcher = operators.matcher(source.substring(cursor));
		if (matcher.lookingAt()) {
			token = source.substring(cursor, cursor + matcher.end());
			pushToken(Token.Type.OPERATOR, token);
			moveCursor(token);
			return;
		}

		// names
		matcher = REGEX_NAME.matcher(source.substring(cursor));
		if (matcher.lookingAt()) {
			token = source.substring(cursor, cursor + matcher.end());
			pushToken(Token.Type.NAME, token);
			moveCursor(token);
			return;
		}

		// numbers
		matcher = REGEX_NUMBER.matcher(source.substring(cursor));
		if (matcher.lookingAt()) {
			token = source.substring(cursor, cursor + matcher.end());
			pushToken(Token.Type.NUMBER, token);
			moveCursor(token);
			return;
		}

		// punctuation
		if (PUNCTUATION.indexOf(source.charAt(cursor)) >= 0) {
			String character = String.valueOf(source.charAt(cursor));

			// opening bracket
			if ("([{".indexOf(character) >= 0) {
				brackets.push(new Pair<String, Integer>(character, lineNumber));
			}

			// closing bracket
			else if (")]}".indexOf(character) >= 0) {
				if (brackets.isEmpty())
					throw new ParserException(null, "Unexpected \"" + character + "\"", lineNumber, filename);
				else {
					HashMap<String, String> validPairs = new HashMap<>();
					validPairs.put("(", ")");
					validPairs.put("[", "]");
					validPairs.put("{", "}");
					String lastBracket = brackets.pop().getLeft();
					String expected = validPairs.get(lastBracket);
					if (!expected.equals(character)) {
						throw new ParserException(null, "Unclosed \"" + expected + "\"", lineNumber, filename);
					}
				}
			}

			pushToken(Token.Type.PUNCTUATION, character);
			++cursor;
			return;
		}

		// strings
		matcher = REGEX_STRING.matcher(source.substring(cursor));
		if (matcher.lookingAt()) {
			token = source.substring(cursor, cursor + matcher.end());

			moveCursor(token);

			char quotationType = token.charAt(0);

			// remove first and last quotation marks
			token = token.substring(1, token.length() - 1);

			// remove backslashes used to escape inner quotation marks
			if (quotationType == '\'') {
				token = token.replaceAll("\\\\(')", "$1");
			} else if (quotationType == '"') {
				token = token.replaceAll("\\\\(\")", "$1");
			}

			pushToken(Token.Type.STRING, token);
			return;
		}

		// we should have found something and returned by this point
		throw new ParserException(null, String.format("Unexpected character \"%s\"", source.charAt(cursor)), lineNumber,
				filename);

	}

	/**
	 * Create a Token of a certain type but has no particular value. This will
	 * pass control to the overloaded method that will push this token into a
	 * list of tokens that we are maintaining.
	 * 
	 * @param type
	 *            The type of Token we are creating
	 */
	private void pushToken(Token.Type type) {
		pushToken(type, null);
	}

	/**
	 * Create a Token of a certain type and value and push it into the list of
	 * tokens that we are maintaining.
	 * 
	 * @param type
	 *            The type of token we are creating
	 * @param value
	 *            The value of the new token
	 */
	private void pushToken(Token.Type type, String value) {
		// ignore empty text tokens
		if (type.equals(Token.Type.TEXT) && StringUtils.isEmpty(value)) {
			return;
		}
		this.tokens.add(new Token(type, value, this.lineNumber));
	}

	/**
	 * Moves the cursor a distance equal to the length of the provided text.
	 * 
	 * This method also counts how many "newlines" are within this text so that
	 * we can increment which line number we're on. The line number is used to
	 * create valuable error messages.
	 * 
	 * @param text
	 *            The text of which the length determines how far the cursor is
	 *            moved
	 */
	private void moveCursor(String text) {
		this.cursor += text.length();
		Pattern newLine = Pattern.compile(Pattern.quote("\n"));
		Matcher matcher = newLine.matcher(text);
		int count = 0;
		while (matcher.find()) {
			count += 1;
		}
		this.lineNumber += count;
	}

	/**
	 * Pushes the current state onto the stack and then updates the current
	 * state to the new state.
	 * 
	 * @param state
	 *            The new state to use as the current state
	 */
	private void pushState(State state) {
		this.states.push(this.state);
		this.state = state;
	}

	/**
	 * Pop state from the stack
	 */
	private void popState() {
		this.state = this.states.pop();
	}

	/**
	 * @return the commentOpenDelimiter
	 */
	public String getCommentOpenDelimiter() {
		return delimiterCommentOpen;
	}

	/**
	 * @param commentOpenDelimiter
	 *            the commentOpenDelimiter to set
	 */
	public void setCommentOpenDelimiter(String commentOpenDelimiter) {
		this.delimiterCommentOpen = commentOpenDelimiter;
	}

	/**
	 * @return the commentCloseDelimiter
	 */
	public String getCommentCloseDelimiter() {
		return delimiterCommentClose;
	}

	/**
	 * @param commentCloseDelimiter
	 *            the commentCloseDelimiter to set
	 */
	public void setCommentCloseDelimiter(String commentCloseDelimiter) {
		this.delimiterCommentClose = commentCloseDelimiter;
	}

	/**
	 * @return the executeOpenDelimiter
	 */
	public String getExecuteOpenDelimiter() {
		return delimiterExecuteOpen;
	}

	/**
	 * @param executeOpenDelimiter
	 *            the executeOpenDelimiter to set
	 */
	public void setExecuteOpenDelimiter(String executeOpenDelimiter) {
		this.delimiterExecuteOpen = executeOpenDelimiter;
	}

	/**
	 * @return the executeCloseDelimiter
	 */
	public String getExecuteCloseDelimiter() {
		return delimiterExecuteClose;
	}

	/**
	 * @param executeCloseDelimiter
	 *            the executeCloseDelimiter to set
	 */
	public void setExecuteCloseDelimiter(String executeCloseDelimiter) {
		this.delimiterExecuteClose = executeCloseDelimiter;
	}

	/**
	 * @return the printOpenDelimiter
	 */
	public String getPrintOpenDelimiter() {
		return delimiterPrintOpen;
	}

	/**
	 * @param printOpenDelimiter
	 *            the printOpenDelimiter to set
	 */
	public void setPrintOpenDelimiter(String printOpenDelimiter) {
		this.delimiterPrintOpen = printOpenDelimiter;
	}

	/**
	 * @return the printCloseDelimiter
	 */
	public String getPrintCloseDelimiter() {
		return delimiterPrintClose;
	}

	/**
	 * @param printCloseDelimiter
	 *            the printCloseDelimiter to set
	 */
	public void setPrintCloseDelimiter(String printCloseDelimiter) {
		this.delimiterPrintClose = printCloseDelimiter;
	}

	/**
	 * Retrieves the operators (both unary and binary) from the PebbleEngine and
	 * then dynamically creates one giant regular expression to detect for the
	 * existence of one of these operators.
	 * 
	 * @return Pattern The regular expression used to find an operator
	 */
	private Pattern getOperatorRegex() {

		List<String> operators = new ArrayList<>();

		for (UnaryOperator operator : engine.getUnaryOperators().values()) {
			operators.add(operator.getSymbol());
		}

		for (BinaryOperator operator : engine.getBinaryOperators().values()) {
			operators.add(operator.getSymbol());
		}

		/*
		 * Since java's matcher doesn't conform with the posix standard of
		 * matching the longest alternative (it matches the first alternative),
		 * we must first sort all of the operators by length before creating the
		 * regex. This is to help match "is not" over "is".
		 */
		Collections.sort(operators, new StringLengthComparator());

		StringBuilder regex = new StringBuilder("^");

		boolean isFirst = true;
		for (String operator : operators) {
			if (isFirst) {
				isFirst = false;
			} else {
				regex.append("|");
			}
			regex.append(Pattern.quote(operator));
		}

		return Pattern.compile(regex.toString());
	}

}
