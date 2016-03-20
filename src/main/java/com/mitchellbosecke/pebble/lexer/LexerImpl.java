/*******************************************************************************
 * This file is part of Pebble.
 * <p>
 * Copyright (c) 2014 by Mitchell Bösecke
 * <p>
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.lexer;

import com.mitchellbosecke.pebble.error.ParserException;
import com.mitchellbosecke.pebble.lexer.Token.Type;
import com.mitchellbosecke.pebble.operator.BinaryOperator;
import com.mitchellbosecke.pebble.operator.UnaryOperator;
import com.mitchellbosecke.pebble.utils.Pair;
import com.mitchellbosecke.pebble.utils.StringLengthComparator;
import com.mitchellbosecke.pebble.utils.StringUtils;

import java.io.IOException;
import java.io.Reader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class reads the template input and builds single items out of it.
 * <p>
 * This class is not thread safe.
 */
public final class LexerImpl implements Lexer {

    /**
     * Syntax
     */
    private final Syntax syntax;

    /**
     * Unary operators
     */
    private final Collection<UnaryOperator> unaryOperators;

    /**
     * Binary operators
     */
    private final Collection<BinaryOperator> binaryOperators;

    /**
     * As we progress through the source we maintain a string which is the text
     * that has yet to be tokenized.
     */
    private TemplateSource source;

    /**
     * The list of tokens that we find and use to create a TokenStream
     */
    private ArrayList<Token> tokens;

    /**
     * Make sure every opening bracket has a closing bracket.
     */
    private LinkedList<Pair<String, Integer>> brackets;

    /**
     * The state of the lexer is important so that we know what to expect next
     * and to help discover errors in the template (ex. unclosed comments).
     */
    private State state;

    private LinkedList<State> states;

    private enum State {
        DATA, EXECUTE, PRINT, COMMENT
    }

    /**
     * If we encountered an END delimiter that was preceded with a whitespace
     * trim character (ex. {{ foo -}}) then this boolean is toggled to "true"
     * which tells the lexData() method to trim leading whitespace from the next
     * text token.
     */
    private boolean trimLeadingWhitespaceFromNextData = false;

    /**
     * Static regular expressions for names, numbers, and punctuation.
     */
    private static final Pattern REGEX_NAME = Pattern.compile("^[a-zA-Z_][a-zA-Z0-9_]*");

    private static final Pattern REGEX_NUMBER = Pattern.compile("^[0-9]+(\\.[0-9]+)?");

    // the negative lookbehind assertion is used to ignore escaped quotation
    // marks
    private static final Pattern REGEX_STRING = Pattern
            .compile("((\").*?(?<!\\\\)(\"))|((').*?(?<!\\\\)('))", Pattern.DOTALL);

    private static final String PUNCTUATION = "()[]{}?:.,|=";

    /**
     * Regular expression to find operators
     */
    private Pattern regexOperators;

    /**
     * Constructor
     *
     * @param syntax          The primary syntax
     * @param unaryOperators  The available unary operators
     * @param binaryOperators The available binary operators
     */
    public LexerImpl(Syntax syntax, Collection<UnaryOperator> unaryOperators, Collection<BinaryOperator> binaryOperators) {
        this.syntax = syntax;
        this.unaryOperators = unaryOperators;
        this.binaryOperators = binaryOperators;
    }

    /**
     * This is the main method used to tokenize the raw contents of a template.
     *
     * @param reader The reader provided from the Loader
     * @param name   The name of the template (used for meaningful error messages)
     * @throws ParserException Thrown from the Reader object
     */
    @Override
    public TokenStream tokenize(Reader reader, String name) throws ParserException {

        // operator regex
        buildOperatorRegex();

        // standardize the character used for line breaks
        try {
            this.source = new TemplateSource(reader, name);
        } catch (IOException e) {
            throw new ParserException(e, "Can not convert template Reader into a String", 0, name);
        }

        /*
         * Start in a DATA state. This state basically means that we are NOT in
         * between a pair of meaningful delimiters.
         */
        this.state = State.DATA;

        this.tokens = new ArrayList<>();
        this.states = new LinkedList<>();
        this.brackets = new LinkedList<>();

        /*
         * loop through the entire source and apply different lexing methods
         * depending on what kind of state we are in at the time.
         *
         * This will always start on lexData();
         */
        while (this.source.length() > 0) {
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
            throw new ParserException(null, String.format("Unclosed \"%s\"", expected), source.getLineNumber(),
                    source.getFilename());
        }

        return new TokenStream(tokens, source.getFilename());
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
        Matcher matcher = this.syntax.getRegexStartDelimiters().matcher(source);
        boolean match = matcher.find();

        String text;
        String startDelimiterToken = null;

        // if we didn't find another start delimiter, the text
        // token goes all the way to the end of the template.
        if (!match) {
            text = source.toString();
            source.advance(source.length());
        } else {
            text = source.substring(matcher.start());
            startDelimiterToken = source.substring(matcher.start(), matcher.end());

            // advance to after the start delimiter
            source.advance(matcher.end());
        }

        // trim leading whitespace from this text if we previously
        // encountered the appropriate whitespace trim character
        if (trimLeadingWhitespaceFromNextData) {
            text = StringUtils.ltrim(text);
            trimLeadingWhitespaceFromNextData = false;
        }
        Token textToken = pushToken(Type.TEXT, text);

        if (match) {

            checkForLeadingWhitespaceTrim(textToken);

            if (this.syntax.getCommentOpenDelimiter().equals(startDelimiterToken)) {

                // we don't actually push any tokens for comments
                pushState(State.COMMENT);

            } else if (this.syntax.getPrintOpenDelimiter().equals(startDelimiterToken)) {

                pushToken(Token.Type.PRINT_START);
                pushState(State.PRINT);

            } else if ((this.syntax.getExecuteOpenDelimiter().equals(startDelimiterToken))) {

                // check for verbatim tag
                Matcher verbatimStartMatcher = this.syntax.getRegexVerbatimStart().matcher(source);
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

        Matcher matcher = this.syntax.getRegexExecuteClose().matcher(source);

        // check if we are at the execute closing delimiter
        if (brackets.isEmpty() && matcher.lookingAt()) {
            pushToken(Token.Type.EXECUTE_END, this.syntax.getExecuteCloseDelimiter());
            source.advance(matcher.end());
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

        Matcher matcher = this.syntax.getRegexPrintClose().matcher(source);

        // check if we are at the print closing delimiter
        if (brackets.isEmpty() && matcher.lookingAt()) {
            pushToken(Token.Type.PRINT_END, this.syntax.getPrintCloseDelimiter());
            source.advance(matcher.end());
            popState();
        } else {
            lexExpression();
        }
    }

    /**
     * Tokenizes between comment delimiters.
     * <p>
     * Simply find the closing delimiter for the comment and move the cursor to
     * that point.
     *
     * @throws ParserException
     */
    private void lexComment() throws ParserException {

        // all we need to do is find the end of the comment.
        Matcher matcher = this.syntax.getRegexCommentClose().matcher(source);

        boolean match = matcher.find(0);
        if (!match) {
            throw new ParserException(null, "Unclosed comment.", source.getLineNumber(), source.getFilename());
        }

        /*
         * check if the commented ended with the whitespace trim character by
         * reversing the comment and performing a regular forward regex search.
         */
        String comment = source.substring(matcher.start());
        String reversedComment = new StringBuilder(comment).reverse().toString();
        Matcher whitespaceTrimMatcher = this.syntax.getRegexLeadingWhitespaceTrim().matcher(reversedComment);
        if (whitespaceTrimMatcher.lookingAt()) {
            this.trimLeadingWhitespaceFromNextData = true;
        }

        // move cursor to end of comment (and closing delimiter)
        source.advance(matcher.end());
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
        source.advanceThroughWhitespace();
        /*
         * Matcher matcher = REGEX_WHITESPACE.matcher(source); if
         * (matcher.lookingAt()) { source.advance(matcher.end()); }
         */

        // operators
        Matcher matcher = regexOperators.matcher(source);
        if (matcher.lookingAt()) {
            token = source.substring(matcher.end());
            pushToken(Token.Type.OPERATOR, token);
            source.advance(matcher.end());
            return;
        }

        // names
        matcher = REGEX_NAME.matcher(source);
        if (matcher.lookingAt()) {
            token = source.substring(matcher.end());
            pushToken(Token.Type.NAME, token);
            source.advance(matcher.end());
            return;
        }

        // numbers
        matcher = REGEX_NUMBER.matcher(source);
        if (matcher.lookingAt()) {
            token = source.substring(matcher.end());
            pushToken(Token.Type.NUMBER, token);
            source.advance(matcher.end());
            return;
        }

        // punctuation
        if (PUNCTUATION.indexOf(source.charAt(0)) >= 0) {
            String character = String.valueOf(source.charAt(0));

            // opening bracket
            if ("([{".indexOf(character) >= 0) {
                brackets.push(new Pair<String, Integer>(character, source.getLineNumber()));
            }

            // closing bracket
            else if (")]}".indexOf(character) >= 0) {
                if (brackets.isEmpty())
                    throw new ParserException(null, "Unexpected \"" + character + "\"", source.getLineNumber(),
                            source.getFilename());
                else {
                    HashMap<String, String> validPairs = new HashMap<>();
                    validPairs.put("(", ")");
                    validPairs.put("[", "]");
                    validPairs.put("{", "}");
                    String lastBracket = brackets.pop().getLeft();
                    String expected = validPairs.get(lastBracket);
                    if (!expected.equals(character)) {
                        throw new ParserException(null, "Unclosed \"" + expected + "\"", source.getLineNumber(),
                                source.getFilename());
                    }
                }
            }

            pushToken(Token.Type.PUNCTUATION, character);
            source.advance(1);
            return;
        }

        // strings
        matcher = REGEX_STRING.matcher(source);
        if (matcher.lookingAt()) {
            token = source.substring(matcher.end());

            source.advance(matcher.end());

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
        throw new ParserException(null, String.format("Unexpected character [%s]", source.charAt(0)),
                source.getLineNumber(), source.getFilename());

    }

    private void checkForLeadingWhitespaceTrim(Token leadingToken) {

        Matcher whitespaceTrimMatcher = this.syntax.getRegexLeadingWhitespaceTrim().matcher(source);

        if (whitespaceTrimMatcher.lookingAt()) {
            if (leadingToken != null) {
                leadingToken.setValue(StringUtils.rtrim(leadingToken.getValue()));
            }
            source.advance(whitespaceTrimMatcher.end());
        }

    }

    private void checkForTrailingWhitespaceTrim() {
        Matcher whitespaceTrimMatcher = this.syntax.getRegexTrailingWhitespaceTrim().matcher(source);

        if (whitespaceTrimMatcher.lookingAt()) {
            this.trimLeadingWhitespaceFromNextData = true;
        }
    }

    /**
     * Implementation of the "verbatim" tag
     *
     * @throws ParserException
     */
    private void lexVerbatimData(Matcher verbatimStartMatcher) throws ParserException {

        // move cursor past the opening verbatim tag
        source.advance(verbatimStartMatcher.end());

        // look for the "endverbatim" tag and storing everything between
        // now and then into a TEXT node
        Matcher verbatimEndMatcher = this.syntax.getRegexVerbatimEnd().matcher(source);

        // check for EOF
        if (!verbatimEndMatcher.find()) {
            throw new ParserException(null, "Unclosed verbatim tag.", source.getLineNumber(), source.getFilename());
        }
        String verbatimText = source.substring(verbatimEndMatcher.start());

        // check if the verbatim start tag has a trailing whitespace trim
        if (verbatimStartMatcher.group(0) != null) {
            verbatimText = StringUtils.ltrim(verbatimText);
        }

        // check if the verbatim end tag had a leading whitespace trim
        if (verbatimEndMatcher.group(1) != null) {
            verbatimText = StringUtils.rtrim(verbatimText);
        }

        // check if the verbatim end tag had a trailing whitespace trim
        if (verbatimEndMatcher.group(2) != null) {
            trimLeadingWhitespaceFromNextData = true;
        }

        // move cursor past the verbatim text and end delimiter
        source.advance(verbatimEndMatcher.end());

        pushToken(Type.TEXT, verbatimText);
    }

    /**
     * Create a Token of a certain type but has no particular value. This will
     * pass control to the overloaded method that will push this token into a
     * list of tokens that we are maintaining.
     *
     * @param type The type of Token we are creating
     */
    private Token pushToken(Token.Type type) {
        return pushToken(type, null);
    }

    /**
     * Create a Token of a certain type and value and push it into the list of
     * tokens that we are maintaining. `
     *
     * @param type  The type of token we are creating
     * @param value The value of the new token
     */
    private Token pushToken(Token.Type type, String value) {
        // ignore empty text tokens
        if (type.equals(Token.Type.TEXT) && (value == null || "".equals(value))) {
            return null;
        }
        Token result = new Token(type, value, source.getLineNumber());
        this.tokens.add(result);

        return result;
    }

    /**
     * Pushes the current state onto the stack and then updates the current
     * state to the new state.
     *
     * @param state The new state to use as the current state
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
     * Retrieves the operators (both unary and binary) from the PebbleEngine and
     * then dynamically creates one giant regular expression to detect for the
     * existence of one of these operators.
     *
     * @return Pattern The regular expression used to find an operator
     */
    private void buildOperatorRegex() {

        List<String> operators = new ArrayList<>();

        for (UnaryOperator operator : unaryOperators) {
            operators.add(operator.getSymbol());
        }

        for (BinaryOperator operator : binaryOperators) {
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
            char nextChar = operator.charAt(operator.length() - 1);
            if (Character.isLetter(nextChar) || Character.getType(nextChar) == Character.LETTER_NUMBER) {
                regex.append("(?![a-zA-Z])");
            }
        }

        this.regexOperators = Pattern.compile(regex.toString());
    }

}
