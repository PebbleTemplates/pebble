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
import com.mitchellbosecke.pebble.utils.Pair;
import com.mitchellbosecke.pebble.utils.StringLengthComparator;
import com.mitchellbosecke.pebble.utils.StringUtils;

public class LexerImpl implements Lexer {

    /**
     * Main components
     */
    private final PebbleEngine engine;

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
     * Regular expression to find operators
     */
    private Pattern regexOperators;

    /**
     * The state of the lexer is important so that we know what to expect next
     * and to help discover errors in the template (ex. unclosed comments).
     */
    private State state;

    private LinkedList<State> states;

    private static enum State {
        DATA, EXECUTE, PRINT, COMMENT
    };

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

        String possibleNewline = "(\r\n|\n\r|\r|\n|\u0085|\u2028|\u2029)?";

        // regexes used to find the individual delimiters
        this.regexPrintClose = Pattern.compile(
                "^\\s*" + Pattern.quote(whitespaceTrim) + "?" + Pattern.quote(delimiterPrintClose) + possibleNewline);
        this.regexExecuteClose = Pattern.compile(
                "^\\s*" + Pattern.quote(whitespaceTrim) + "?" + Pattern.quote(delimiterExecuteClose) + possibleNewline);
        this.regexCommentClose = Pattern.compile(Pattern.quote(delimiterCommentClose) + possibleNewline);

        // combination regex used to find the next START delimiter of any kind
        this.regexStartDelimiters = Pattern.compile(Pattern.quote(delimiterPrintOpen) + "|"
                + Pattern.quote(delimiterExecuteOpen) + "|" + Pattern.quote(delimiterCommentOpen));

        // regex to find the verbatim tag
        this.regexVerbatimStart = Pattern.compile("^\\s*verbatim\\s*(" + Pattern.quote(whitespaceTrim) + ")?"
                + Pattern.quote(delimiterExecuteClose) + possibleNewline);
        this.regexVerbatimEnd = Pattern.compile(Pattern.quote(delimiterExecuteOpen) + "("
                + Pattern.quote(whitespaceTrim) + ")?" + "\\s*endverbatim\\s*(" + Pattern.quote(whitespaceTrim) + ")?"
                + Pattern.quote(delimiterExecuteClose) + possibleNewline);

        // regex for the whitespace trim character
        this.regexLeadingWhitespaceTrim = Pattern.compile(Pattern.quote(whitespaceTrim) + "\\s+");
        this.regexTrailingWhitespaceTrim = Pattern
                .compile("^\\s*" + Pattern.quote(whitespaceTrim) + "(" + Pattern.quote(delimiterPrintClose) + "|"
                        + Pattern.quote(delimiterExecuteClose) + "|" + Pattern.quote(delimiterCommentClose) + ")");
    }

    /**
     * This is the main method used to tokenize the raw contents of a template.
     * 
     * @param reader
     *            The reader provided from the Loader
     * @param name
     *            The name of the template (used for meaningful error messages)
     * @throws ParserException
     *             Thrown from the Reader object
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
        Matcher matcher = regexStartDelimiters.matcher(source);
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

            if (delimiterCommentOpen.equals(startDelimiterToken)) {

                // we don't actually push any tokens for comments
                pushState(State.COMMENT);

            } else if (delimiterPrintOpen.equals(startDelimiterToken)) {

                pushToken(Token.Type.PRINT_START);
                pushState(State.PRINT);

            } else if (delimiterExecuteOpen.equals(startDelimiterToken)) {

                // check for verbatim tag
                Matcher verbatimStartMatcher = regexVerbatimStart.matcher(source);
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

        Matcher matcher = regexExecuteClose.matcher(source);

        // check if we are at the execute closing delimiter
        if (brackets.isEmpty() && matcher.lookingAt()) {
            pushToken(Token.Type.EXECUTE_END, delimiterExecuteClose);
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

        Matcher matcher = regexPrintClose.matcher(source);

        // check if we are at the print closing delimiter
        if (brackets.isEmpty() && matcher.lookingAt()) {
            pushToken(Token.Type.PRINT_END, delimiterPrintClose);
            source.advance(matcher.end());
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
        Matcher whitespaceTrimMatcher = regexLeadingWhitespaceTrim.matcher(reversedComment);
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

        Matcher whitespaceTrimMatcher = regexLeadingWhitespaceTrim.matcher(source);

        if (whitespaceTrimMatcher.lookingAt()) {
            if (leadingToken != null) {
                leadingToken.setValue(StringUtils.rtrim(leadingToken.getValue()));
            }
            source.advance(whitespaceTrimMatcher.end());
        }

    }

    private void checkForTrailingWhitespaceTrim() {
        Matcher whitespaceTrimMatcher = regexTrailingWhitespaceTrim.matcher(source);

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
        Matcher verbatimEndMatcher = regexVerbatimEnd.matcher(source);

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
     * @param type
     *            The type of Token we are creating
     */
    private Token pushToken(Token.Type type) {
        return pushToken(type, null);
    }

    /**
     * Create a Token of a certain type and value and push it into the list of
     * tokens that we are maintaining. `
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
        Token result = new Token(type, value, source.getLineNumber());
        this.tokens.add(result);

        return result;
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
    private void buildOperatorRegex() {

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
            char nextChar = operator.charAt(operator.length() - 1);
            if (Character.isLetter(nextChar) || Character.getType(nextChar) == Character.LETTER_NUMBER) {
                regex.append("(?![a-zA-Z])");
            }
        }

        this.regexOperators = Pattern.compile(regex.toString());
    }

    public String getWhitespaceTrim() {
        return whitespaceTrim;
    }

    public void setWhitespaceTrim(String whitespaceTrim) {
        this.whitespaceTrim = whitespaceTrim;
    }

}
