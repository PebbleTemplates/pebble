package com.mitchellbosecke.pebble.lexer;

import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An implementation of CharSequence that is tuned to be used specifically by
 * {@link LexerImpl}. It is possible to advance through the sequence without
 * allocating a copy and it is possible to perform regex matches from the
 * logical beginning of the remaining un-tokenized source. This class will also
 * standardize newline characters from different architectures.
 * 
 * @author mbosecke
 *
 */
public class TemplateSource implements CharSequence {

    /**
     * The characters found within the template.
     */
    private char source[];

    /**
     * Number of characters stored in source array remaining to be tokenized
     */
    private int size = 0;

    /**
     * Default capacity
     */
    private static final int DEFAULT_CAPACITY = 1024;

    /**
     * An index of the first character for the remaining un-tokenized source.
     */
    private int offset = 0;

    /**
     * Tracking the line number that we are currently tokenizing.
     */
    private int lineNumber = 1;

    /**
     * Filename of the template
     */
    private final String filename;

    /**
     * Pattern used to detect newlines from multiple different systems
     */

    /**
     * Constructor
     * 
     * @param reader
     * @param filename
     * @throws IOException
     */
    public TemplateSource(Reader reader, String filename) throws IOException {
        this.filename = filename;
        this.source = new char[DEFAULT_CAPACITY];
        copyReaderIntoCharArray(reader);
    }

    /**
     * Read the contents of the template into the internal char[].
     * 
     * @param reader
     * @return
     * @throws IOException
     */
    private void copyReaderIntoCharArray(Reader reader) throws IOException {
        char[] buffer = new char[1024 * 4];
        int amountJustRead = 0;
        while ((amountJustRead = reader.read(buffer)) != -1) {

            ensureCapacity(size + amountJustRead);
            append(buffer, amountJustRead);
        }
        reader.close();
    }

    /**
     * Append characters to the internal array.
     * 
     * @param characters
     * @param amount
     */
    private void append(char[] characters, int amount) {
        for (int i = 0; i < amount; ++i) {
            this.source[size + i] = characters[i];
        }
        size += amount;
    }

    /**
     * Ensure that the internal array has a minimum capacity.
     * 
     * @param minCapacity
     */
    private void ensureCapacity(int minCapacity) {
        if (source.length - minCapacity < 0) {
            grow(minCapacity);
        }
    }

    /**
     * Grow the internal array to at least the desired minimum capacity.
     * 
     * @param minCapacity
     */
    private void grow(int minCapacity) {
        int oldCapacity = source.length;

        /*
         * double the capacity of the array and if that's not enough, just use
         * the minCapacity
         */
        int newCapacity = Math.max(oldCapacity << 1, minCapacity);

        this.source = Arrays.copyOf(source, newCapacity);
    }

    /**
     * Moves the start index a distance equal to the length of the provided
     * text.
     * 
     * This method also counts how many "newlines" are within this text so that
     * we can increment which line number we're on. The line number is used to
     * create valuable error messages.
     * 
     * @param text
     *            The text of which the length determines how far the cursor is
     *            moved
     */
    public void advance(int amount) {

        for (int index = 0; index < amount; index++) {

            char character = this.charAt(index);

            // windows newline
            if ('\r' == character && '\n' == this.charAt(index + 1)) {

                this.lineNumber++;
                index++; // increment by an extra character due to newline being
                         // a two-character entity

                // various other newline characters
            } else if ('\r' == character || '\n' == character || '\u0085' == character || '\u2028' == character
                    || '\u2029' == character) {
                
                this.lineNumber ++;
            }
        }

        this.size -= amount;
        this.offset += amount;
    }

    public String substring(int start, int end) {
        return new String(Arrays.copyOfRange(source, this.offset + start, this.offset + end));
    }

    public String substring(int end) {
        return new String(Arrays.copyOfRange(source, offset, offset + end));
    }

    @Override
    public int length() {
        return size;
    }

    @Override
    public char charAt(int index) {
        return source[offset + index];
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return new String(Arrays.copyOfRange(source, this.offset + start, this.offset + end));
    }

    public String toString() {
        return new String(Arrays.copyOfRange(source, offset, offset + size));
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public String getFilename() {
        return filename;
    }
}
