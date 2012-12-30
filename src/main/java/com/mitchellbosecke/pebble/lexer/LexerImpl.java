/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Copyright (c) 2012 Mitchell Bosecke.
 * 
 * This work is licensed under the Creative Commons Attribution-ShareAlike 3.0 
 * Unported License. To view a copy of this license, visit 
 * http://creativecommons.org/licenses/by-sa/3.0/
 ******************************************************************************/
package com.mitchellbosecke.pebble.lexer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.error.SyntaxException;
import com.mitchellbosecke.pebble.lexer.Token.Type;
import com.mitchellbosecke.pebble.parser.Operator;
import com.mitchellbosecke.pebble.utils.Pair;
import com.mitchellbosecke.pebble.utils.StringLengthComparator;

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
	 * The different tags which change the state of the lexer. The regular
	 * expressions used to find these tags are dynamically generated. They are
	 * safe to change via the getter/setter methods.
	 * 
	 */
	private String tagCommentOpen = "{#";
	private String tagCommentClose = "#}";
	private String tagBlockOpen = "{%";
	private String tagBlockClose = "%}";
	private String tagVariableOpen = "{{";
	private String tagVariableClose = "}}";

	/**
	 * The regular expressions used to find the different tags
	 */
	private Pattern regexVariableClose;
	private Pattern regexBlockClose;
	private Pattern regexCommentClose;
	private Pattern regexStartTags;

	/**
	 * The state of the lexer is important so that we know what to expect next
	 * and to help discover errors in the template (ex. unclosed comments).
	 */
	private State state;
	private Stack<State> states;

	private static enum State {
		DATA, BLOCK, VARIABLE, COMMENT
	};

	/**
	 * Generic regular expressions for names, numbers, and punctuation.
	 */
	private static final Pattern REGEX_NAME = Pattern.compile("^[a-zA-Z_][a-zA-Z0-9_]*");
	private static final Pattern REGEX_NUMBER = Pattern.compile("^[0-9]+(\\.[0-9]+)?");
	private static final Pattern REGEX_STRING = Pattern.compile("\".*?\"");
	private static final String PUNCTUATION = "()[]{}?:.,|";

	/**
	 * Constructor
	 * 
	 * @param engine
	 *            The PebbleEngine that the lexer can use to get information
	 *            from
	 */
	public LexerImpl(PebbleEngine engine) {
		this.engine = engine;

		// generate the regexes used to find the individual tags
		this.regexVariableClose = Pattern.compile("^\\s*" + Pattern.quote(tagVariableClose) + "\\n?");
		this.regexBlockClose = Pattern.compile("^\\s*" + Pattern.quote(tagBlockClose) + "\\n?");
		this.regexCommentClose = Pattern.compile(Pattern.quote(tagCommentClose) + "\\n?");

		// Generate a special regex used to find the next START tag
		this.regexStartTags = Pattern.compile(Pattern.quote(tagVariableOpen) + "|" + Pattern.quote(tagBlockOpen) + "|"
				+ Pattern.quote(tagCommentOpen));
	}

	/**
	 * This is the main method used to tokenize the raw contents of a template.
	 * 
	 * @param source
	 *            The raw contents of the template
	 * @param name
	 *            The name of the template (used for meaningful error messages)
	 */
	@Override
	public TokenStream tokenize(String source, String name) {

		// standardize the character used for line breaks
		this.source = source.replaceAll("(\r\n|\n)", "\n");

		/*
		 * Start in a DATA state. This state basically means that we are NOT in
		 * between a pair of meaningful tags.
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
				case BLOCK:
					lexBlock();
					break;
				case VARIABLE:
					lexVariable();
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
			throw new SyntaxException(String.format("Unclosed \"%s\"", expected), lineNumber, filename);
		}

		return new TokenStream(tokens, filename);
	}

	/**
	 * The DATA state assumes that we are current NOT in between any pair of
	 * meaningful tags. We are currently looking for the next "open" or "start"
	 * tag, ex. the opening comment tag, or the opening variable tag.
	 */
	private void lexData() {
		// find the next start tag
		Matcher matcher = regexStartTags.matcher(source);
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

		// get the individual tag, we still don't know which one it was
		String token = source.substring(matcher.start(), matcher.end());

		moveCursor(text + token);

		if (tagCommentOpen.equals(token)) {

			// we don't actually push any tokens for comments
			pushState(State.COMMENT);

		} else if (tagVariableOpen.equals(token)) {

			pushToken(Token.Type.VARIABLE_START);
			pushState(State.VARIABLE);

		} else if (tagBlockOpen.equals(token)) {

			pushToken(Token.Type.BLOCK_START);
			pushState(State.BLOCK);

		}

	}

	/**
	 * Tokenizes between block tags.
	 */
	private void lexBlock() {
		Matcher matcher = regexBlockClose.matcher(source.substring(cursor));

		// check if we are at the block closing tag
		if (brackets.isEmpty() && matcher.lookingAt()) {
			String token = source.substring(cursor, cursor + matcher.end());
			pushToken(Token.Type.BLOCK_END);
			moveCursor(token);
			popState();
		} else {
			lexExpression();
		}
	}

	/**
	 * Tokenizes between variable tags.
	 */
	private void lexVariable() {
		Matcher matcher = regexVariableClose.matcher(source.substring(cursor));

		// check if we are at the variable closing tag
		if (brackets.isEmpty() && matcher.lookingAt()) {
			String token = source.substring(cursor, cursor + matcher.end());
			pushToken(Token.Type.VARIABLE_END);
			moveCursor(token);
			popState();
		} else {
			lexExpression();
		}
	}

	/**
	 * Tokenizes between comment tags.
	 * 
	 * Simply find the closing tag for the comment and move the cursor to that
	 * point.
	 */
	private void lexComment() {

		// all we need to do is find the end of the comment.
		Matcher matcher = regexCommentClose.matcher(source);

		boolean match = matcher.find(cursor);
		if (!match) {
			throw new SyntaxException("Unclosed comment block.", lineNumber, filename);
		}

		// move cursor to end of comment tag
		String commentWithEndTag = source.substring(cursor, matcher.end());
		moveCursor(commentWithEndTag);
		popState();
	}

	/**
	 * Tokenizing an expression which can be found within both block and
	 * variable tags.
	 */
	private void lexExpression() {
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
					throw new SyntaxException("Unexpected \"" + character + "\"", lineNumber, filename);
				else {
					HashMap<String, String> validPairs = new HashMap<>();
					validPairs.put("(", ")");
					validPairs.put("[", "]");
					validPairs.put("{", "}");
					String lastBracket = brackets.pop().getLeft();
					String expected = validPairs.get(lastBracket);
					if (!expected.equals(character)) {
						throw new SyntaxException("Unclosed \"" + expected + "\"", lineNumber, filename);
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
			pushToken(Token.Type.STRING, token);
			moveCursor(token);
			return;
		}

		// we should have found something and returned by this point
		throw new SyntaxException(String.format("Unexpected character \"%s\"", source.charAt(cursor)), lineNumber,
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
		if (type.equals(Token.Type.TEXT) && StringUtils.isEmpty(value))
			return;
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
	 * @return the tagCommentOpen
	 */
	public String getTagCommentOpen() {
		return tagCommentOpen;
	}

	/**
	 * @param tagCommentOpen
	 *            the tagCommentOpen to set
	 */
	public void setTagCommentOpen(String tagCommentOpen) {
		this.tagCommentOpen = tagCommentOpen;
	}

	/**
	 * @return the tagCommentClose
	 */
	public String getTagCommentClose() {
		return tagCommentClose;
	}

	/**
	 * @param tagCommentClose
	 *            the tagCommentClose to set
	 */
	public void setTagCommentClose(String tagCommentClose) {
		this.tagCommentClose = tagCommentClose;
	}

	/**
	 * @return the tagBlockOpen
	 */
	public String getTagBlockOpen() {
		return tagBlockOpen;
	}

	/**
	 * @param tagBlockOpen
	 *            the tagBlockOpen to set
	 */
	public void setTagBlockOpen(String tagBlockOpen) {
		this.tagBlockOpen = tagBlockOpen;
	}

	/**
	 * @return the tagBlockClose
	 */
	public String getTagBlockClose() {
		return tagBlockClose;
	}

	/**
	 * @param tagBlockClose
	 *            the tagBlockClose to set
	 */
	public void setTagBlockClose(String tagBlockClose) {
		this.tagBlockClose = tagBlockClose;
	}

	/**
	 * @return the tagVariableOpen
	 */
	public String getTagVariableOpen() {
		return tagVariableOpen;
	}

	/**
	 * @param tagVariableOpen
	 *            the tagVariableOpen to set
	 */
	public void setTagVariableOpen(String tagVariableOpen) {
		this.tagVariableOpen = tagVariableOpen;
	}

	/**
	 * @return the tagVariableClose
	 */
	public String getTagVariableClose() {
		return tagVariableClose;
	}

	/**
	 * @param tagVariableClose
	 *            the tagVariableClose to set
	 */
	public void setTagVariableClose(String tagVariableClose) {
		this.tagVariableClose = tagVariableClose;
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

		for (Operator operator : engine.getUnaryOperators().values()) {
			operators.add(operator.getSymbol());
		}

		for (Operator operator : engine.getBinaryOperators().values()) {
			operators.add(operator.getSymbol());
		}

		/*
		 * Since java's matcher doesn't conform with the posix standard of
		 * matching the longest alternative (it matches the first alternative),
		 * we must first sort all of the operators by length before creating the
		 * regex. This is to help match "is not" over "is".
		 */
		Collections.sort(operators, new StringLengthComparator());

		String regex = "^";

		boolean isFirst = true;
		for (String operator : operators) {
			if (isFirst) {
				isFirst = false;
			} else {
				regex += "|";
			}
			regex += Pattern.quote(operator);
		}

		return Pattern.compile(regex);
	}

}
