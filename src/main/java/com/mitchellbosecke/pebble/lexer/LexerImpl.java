/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Copyright (c) 2014 by Mitchell Bösecke
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.lexer;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.error.ParserException;
import com.mitchellbosecke.pebble.lexer.Token.Type;
import com.mitchellbosecke.pebble.operator.BinaryOperator;
import com.mitchellbosecke.pebble.operator.UnaryOperator;
import com.mitchellbosecke.pebble.utils.IOUtils;
import com.mitchellbosecke.pebble.utils.Pair;
import com.mitchellbosecke.pebble.utils.StringLengthComparator;
import com.mitchellbosecke.pebble.utils.StringUtils;

public class LexerImpl implements Lexer {

	/**
	 * Main components
	 */
	private final PebbleEngine engine;
	private String filename;
	private String originalSource;
	
	/**
	 * As we progress through the source we maintain
	 * a second string which is the text that has yet
	 * to be lexed.
	 */
	private String remainingSource;

	/**
	 * Variables for keeping track of where we are located
	 */
	private int lineNumber;
	private int end;

	/**
	 * The list of tokens that we find and use to create a TokenStream
	 */
	private ArrayList<Token> tokens;

	/**
	 * Make sure every opening bracket has a closing bracket.
	 */
	private LinkedList<Pair<String, Integer>> brackets;

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
	private String whitespaceTrim = "-";

	/**
	 * The regular expressions used to find the different delimiters
	 */
	private Pattern regexPrintClose;
	private Pattern regexExecuteClose;
	private Pattern regexCommentClose;
	private Pattern regexStartDelimiters;
	private Pattern regexLeadingWhitespaceTrim;
	private Pattern regexTrailingWhitespaceTrim;

	/**
	 * Regular expressions used to find "verbatim" and "endverbatim" tags.
	 */
	private Pattern regexVerbatimStart;
	private Pattern regexVerbatimEnd;

	/**
	 * The state of the lexer is important so that we know what to expect next
	 * and to help discover errors in the template (ex. unclosed comments).
	 */
	private State state;
	private LinkedList<State> states;
	
	/**
	 * If we encountered an END delimiter that was preceded with a whitespace
	 * trim character (ex. {{ foo -}}) then this boolean is toggled to "true"
	 * which tells the lexData() method to trim leading whitespace from the next
	 * text token.
	 */
	private boolean trimLeadingWhitespaceFromNextData = false;

	private static enum State {
		DATA, EXECUTE, PRINT, COMMENT
	};

	/**
	 * Static regular expressions for names, numbers, and punctuation.
	 */
	private static final Pattern REGEX_NAME = Pattern
			.compile("^[a-zA-Z_][a-zA-Z0-9_]*");
	private static final Pattern REGEX_NUMBER = Pattern
			.compile("^[0-9]+(\\.[0-9]+)?");

	// the negative lookbehind assertion is used to ignore escaped quotation
	// marks
	private static final Pattern REGEX_STRING = Pattern.compile(
			"((\").*?(?<!\\\\)(\"))|((').*?(?<!\\\\)('))", Pattern.DOTALL);
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

		// regexes used to find the individual delimiters
		this.regexPrintClose = Pattern.compile("^\\s*" + Pattern.quote(whitespaceTrim) + "?" + Pattern.quote(delimiterPrintClose) + "\\n?");
		this.regexExecuteClose = Pattern.compile("^\\s*" + Pattern.quote(whitespaceTrim) + "?" + Pattern.quote(delimiterExecuteClose) + "\\n?");
		this.regexCommentClose = Pattern.compile(Pattern.quote(delimiterCommentClose) + "\\n?");

		// combination regex used to find the next START delimiters of any kind
		this.regexStartDelimiters = Pattern.compile(Pattern.quote(delimiterPrintOpen) + "|"	+ Pattern.quote(delimiterExecuteOpen) + "|"	+ Pattern.quote(delimiterCommentOpen));

		// regex to find the verbatim tag
		this.regexVerbatimStart = Pattern.compile("^\\s*verbatim\\s*(" + Pattern.quote(whitespaceTrim) + ")?" + Pattern.quote(delimiterExecuteClose) + "\\n?");
		this.regexVerbatimEnd = Pattern.compile(Pattern.quote(delimiterExecuteOpen) + "(" + Pattern.quote(whitespaceTrim) + ")?" + "\\s*endverbatim\\s*("  + Pattern.quote(whitespaceTrim) + ")?" + Pattern.quote(delimiterExecuteClose) + "\\n?");

		// regex for the whitespace trim character
		this.regexLeadingWhitespaceTrim = Pattern.compile(Pattern.quote(whitespaceTrim) + "\\s+");
		this.regexTrailingWhitespaceTrim = Pattern.compile("^\\s*" + Pattern.quote(whitespaceTrim) + "(" + Pattern.quote(delimiterPrintClose) + "|"	+ Pattern.quote(delimiterExecuteClose) + "|"	+ Pattern.quote(delimiterCommentClose) + ")");
	}

	/**
	 * This is the main method used to tokenize the raw contents of a template.
	 * 
	 * @param originalSource
	 *            The raw contents of the template
	 * @param name
	 *            The name of the template (used for meaningful error messages)
	 * @throws ParserException
	 */
	@Override
	public TokenStream tokenize(Reader reader, String name)
			throws ParserException {
		
		// standardize the character used for line breaks
		try {
			this.originalSource = IOUtils.toString(reader).replaceAll("(\r\n|\n)", "\n");
		} catch (IOException e) {
			throw new ParserException(e, "Can not convert template Reader into a String");
		}
		this.remainingSource = this.originalSource;

		/*
		 * Start in a DATA state. This state basically means that we are NOT in
		 * between a pair of meaningful delimiters.
		 */
		this.state = State.DATA;

		this.filename = name;
		this.lineNumber = 1;
		this.end = originalSource.length();
		this.tokens = new ArrayList<>();
		this.states = new LinkedList<>();
		this.brackets = new LinkedList<>();

		/*
		 * loop through the entire source and apply different lexing methods
		 * depending on what kind of state we are in at the time.
		 * 
		 * This will always start on lexData();
		 */
		while (this.remainingSource.length() > 0) {
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
			throw new ParserException(null, String.format("Unclosed \"%s\"",
					expected), lineNumber, filename);
		}

		return new TokenStream(tokens, filename);
	}

	/**
	 * The DATA state assumes that we are current NOT in between any pair of
	 * meaningful delimiters. We are currently looking for the next "open" or
	 * "start" delimiter, ex. the opening comment delimiter, or the opening
	 * variable delimiter.
	 * 
	 * @throws ParserException
	 */
	private void lexData() throws ParserException {
		// find the next start delimiter
		Matcher matcher = regexStartDelimiters.matcher(remainingSource);
		boolean match = matcher.find();

		String text;
		
		// if we didn't find another start delimiter, the text
		// token goes all the way to the end of the template.
		if (!match) {
			text = remainingSource;
		}else{
			text = remainingSource.substring(0, matcher.start());
		}
		advanceThroughSource(text);
		
		// trim leading whitespace from this text if we previously
		// encountered the appropriate whitespace trim character
		if(trimLeadingWhitespaceFromNextData){
			text = StringUtils.ltrim(text);
			trimLeadingWhitespaceFromNextData = false;
		}
		Token textToken = pushToken(Type.TEXT, text);
		
		if(match){
			
			// get the individual delimiter, we still don't know which one it was
			String token = remainingSource.substring(matcher.start() - 1, matcher.end());

			advanceThroughSource(token);

			checkForLeadingWhitespaceTrim(textToken);

			if (delimiterCommentOpen.equals(token)) {

				// we don't actually push any tokens for comments
				pushState(State.COMMENT);

			} else if (delimiterPrintOpen.equals(token)) {
				
				pushToken(Token.Type.PRINT_START);
				pushState(State.PRINT);
		

			} else if (delimiterExecuteOpen.equals(token)) {

				// check for verbatim tag
				Matcher verbatimStartMatcher = regexVerbatimStart.matcher(remainingSource);
				if (verbatimStartMatcher.lookingAt()) {

					lexVerbatimData(verbatimStartMatcher);
					pushState(State.DATA);

				} else {

					pushToken(Token.Type.EXECUTE_START);
					pushState(State.EXECUTE);
					
				}

			}
		}

	}

	/**
	 * Tokenizes between execute delimiters.
	 * 
	 * @throws ParserException
	 */
	private void lexExecute() throws ParserException {
		
		// check for the trailing whitespace trim character 
		checkForTrailingWhitespaceTrim();
		
		Matcher matcher = regexExecuteClose.matcher(remainingSource);

		// check if we are at the execute closing delimiter
		if (brackets.isEmpty() && matcher.lookingAt()) {
			String token = remainingSource.substring(0, matcher.end());
			pushToken(Token.Type.EXECUTE_END, delimiterExecuteClose);
			advanceThroughSource(token);
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

		// check for the trailing whitespace trim character 
		checkForTrailingWhitespaceTrim();
		
		Matcher matcher = regexPrintClose.matcher(remainingSource);

		// check if we are at the print closing delimiter
		if (brackets.isEmpty() && matcher.lookingAt()) {
			String token = remainingSource.substring(0, matcher.end());
			pushToken(Token.Type.PRINT_END, delimiterPrintClose);
			advanceThroughSource(token);
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
		Matcher matcher = regexCommentClose.matcher(remainingSource);

		boolean match = matcher.find(0);
		if (!match) {
			throw new ParserException(null, "Unclosed comment.", lineNumber,
					filename);
		}
		
		/* 
		 * check if the commented ended with the whitespace trim character
		 * by reversing the comment and performing a regular forward regex search.
		 */
		String comment = remainingSource.substring(0, matcher.start());
		String reversedComment = new StringBuilder(comment).reverse().toString();
		Matcher whitespaceTrimMatcher = regexLeadingWhitespaceTrim.matcher(reversedComment);
		if (whitespaceTrimMatcher.lookingAt()) {
			this.trimLeadingWhitespaceFromNextData = true;
		}
		
		// move cursor to end of comment (and closing delimiter)
		advanceThroughSource(remainingSource.substring(0, matcher.end()));
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
		Matcher matcher = whitespace.matcher(remainingSource);
		if (matcher.lookingAt()) {
			advanceThroughSource(remainingSource.substring(0, matcher.end()));
		}

		// operators
		Pattern operators = getOperatorRegex();
		matcher = operators.matcher(remainingSource);
		if (matcher.lookingAt()) {
			token = remainingSource.substring(0, matcher.end());
			pushToken(Token.Type.OPERATOR, token);
			advanceThroughSource(token);
			return;
		}

		// names
		matcher = REGEX_NAME.matcher(remainingSource);
		if (matcher.lookingAt()) {
			token = remainingSource.substring(0, matcher.end());
			pushToken(Token.Type.NAME, token);
			advanceThroughSource(token);
			return;
		}

		// numbers
		matcher = REGEX_NUMBER.matcher(remainingSource);
		if (matcher.lookingAt()) {
			token = remainingSource.substring(0, matcher.end());
			pushToken(Token.Type.NUMBER, token);
			advanceThroughSource(token);
			return;
		}

		// punctuation
		if (PUNCTUATION.indexOf(remainingSource.charAt(0)) >= 0) {
			String character = String.valueOf(remainingSource.charAt(0));

			// opening bracket
			if ("([{".indexOf(character) >= 0) {
				brackets.push(new Pair<String, Integer>(character, lineNumber));
			}

			// closing bracket
			else if (")]}".indexOf(character) >= 0) {
				if (brackets.isEmpty())
					throw new ParserException(null, "Unexpected \"" + character
							+ "\"", lineNumber, filename);
				else {
					HashMap<String, String> validPairs = new HashMap<>();
					validPairs.put("(", ")");
					validPairs.put("[", "]");
					validPairs.put("{", "}");
					String lastBracket = brackets.pop().getLeft();
					String expected = validPairs.get(lastBracket);
					if (!expected.equals(character)) {
						throw new ParserException(null, "Unclosed \""
								+ expected + "\"", lineNumber, filename);
					}
				}
			}

			pushToken(Token.Type.PUNCTUATION, character);
			advanceThroughSource(character);
			return;
		}

		// strings
		matcher = REGEX_STRING.matcher(remainingSource);
		if (matcher.lookingAt()) {
			token = remainingSource.substring(0, matcher.end());

			advanceThroughSource(token);

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
		throw new ParserException(null, String.format(
				"Unexpected character [%s]", remainingSource.charAt(0)),
				lineNumber, filename);

	}

	private void checkForLeadingWhitespaceTrim(Token leadingToken) {
		
		Matcher whitespaceTrimMatcher = regexLeadingWhitespaceTrim
				.matcher(remainingSource);

		if (whitespaceTrimMatcher.lookingAt()) {
			if (leadingToken != null) {
				leadingToken
						.setValue(StringUtils.rtrim(leadingToken.getValue()));
			}
			advanceThroughSource(remainingSource.substring(0, whitespaceTrimMatcher.end()));
		}
		
	}
	
	private void checkForTrailingWhitespaceTrim() {
		Matcher whitespaceTrimMatcher = regexTrailingWhitespaceTrim.matcher(remainingSource);

		if (whitespaceTrimMatcher.lookingAt()) {
			this.trimLeadingWhitespaceFromNextData = true;
		}
	}
	
	/**
	 * Implementation of the "verbatim" tag
	 * 
	 * @throws ParserException
	 */
	private void lexVerbatimData(Matcher verbatimStartMatcher)
			throws ParserException {

		// move cursor past the opening verbatim tag
		advanceThroughSource(remainingSource.substring(0, verbatimStartMatcher.end()));

		// look for the "endverbatim" tag and storing everything between
		// now and then into a TEXT node
		Matcher verbatimEndMatcher = regexVerbatimEnd.matcher(remainingSource);

		// check for EOF
		if (!verbatimEndMatcher.find()) {
			throw new ParserException(null, "Unclosed verbatim tag.",
					lineNumber, filename);
		}
		String verbatimText = remainingSource.substring(0, verbatimEndMatcher.start());
		
		// check if the verbatim start tag has a trailing whitespace trim
		if(verbatimStartMatcher.group(0) != null){
			verbatimText = StringUtils.ltrim(verbatimText);
		}
		
		// check if the verbatim end tag had a leading whitespace trim
		if(verbatimEndMatcher.group(1) != null){
			verbatimText = StringUtils.rtrim(verbatimText);
		}

		// move cursor past the verbatim text
		advanceThroughSource(verbatimText);
		
		// check if the verbatim end tag had a trailing whitespace trim
		if(verbatimEndMatcher.group(2) != null){
			trimLeadingWhitespaceFromNextData = true;
		}

		// move cursor past the "endverbatim" tag
		advanceThroughSource(remainingSource.substring(0, verbatimEndMatcher.end()));

		pushToken(Type.TEXT, verbatimText);
	}

	/**
	 * Create a Token of a certain type but has no particular value. This will
	 * pass control to the overloaded method that will push this token into a
	 * list of tokens that we are maintaining.
	 * 
	 * @param type
	 *            The type of Token we are creating
	 */
	private Token pushToken(Token.Type type) {
		return pushToken(type, null);
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
	private Token pushToken(Token.Type type, String value) {
		// ignore empty text tokens
		if (type.equals(Token.Type.TEXT) && (value == null || "".equals(value))) {
			return null;
		}
		Token result = new Token(type, value, this.lineNumber);
		this.tokens.add(result);

		return result;
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
	private void advanceThroughSource(String text) {
		
		// count newlines
		Pattern newLine = Pattern.compile(Pattern.quote("\n"));
		Matcher matcher = newLine.matcher(text);
		int count = 0;
		while (matcher.find()) {
			count += 1;
		}
		this.lineNumber += count;
		
		// update remainingSource
		this.remainingSource = remainingSource.substring(text.length());
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

			/*
			 * If the operator ends in an alpha character we use a negative
			 * lookahead assertion to make sure the next character in the stream
			 * is NOT an alpha character. This ensures user can type
			 * "organization" without the "or" being parsed as an operator.
			 */
			if (Character.isAlphabetic(operator.charAt(operator.length() - 1))) {
				regex.append("(?![a-zA-Z])");
			}
		}

		return Pattern.compile(regex.toString());
	}

	public String getWhitespaceTrim() {
		return whitespaceTrim;
	}

	public void setWhitespaceTrim(String whitespaceTrim) {
		this.whitespaceTrim = whitespaceTrim;
	}

}
