package com.mitchellbosecke.pebble.lexer;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TemplateSource implements CharSequence {

	/** The value is used for character storage. */
	private final char value[];

	/**
	 * An index of the first character for the remaining un-tokenized source.
	 */
	private int offset = 0;

	/**
	 * Tracking the line number that we are currently tokenizing.
	 */
	private int lineNumber = 1;
	
	private final String filename;

	private Pattern newLine = Pattern.compile(Pattern.quote("\n"));

	public TemplateSource(Reader reader, String filename) throws IOException {
		this.value = copyReaderIntoCharArray(reader);
		this.filename = filename;
	}

	private char[] copyReaderIntoCharArray(Reader reader) throws IOException {
		StringWriter writer = new StringWriter();

		char[] buffer = new char[1024 * 4];
		int n = 0;
		while ((n = reader.read(buffer)) != -1) {
			writer.write(buffer, 0, n);
		}

		String value = writer.toString();

		// standardize newlines
		value = value.replaceAll("(\r\n|\n)", "\n");

		return value.toCharArray();
	}
	
	public void advance(int amount){
		for(int index = 0; index < amount; index++){
			char character = value[index];
			if(character == '\n'){
				this.lineNumber++;
			}
		}

		// advance the index used to represent the start of the remaining
		// un-tokenized source.
		this.offset += amount;
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
	public void advance(String text) {
		// count newlines
		Matcher matcher = newLine.matcher(text);
		while (matcher.find()) {
			this.lineNumber++;
		}

		// advance the index used to represent the start of the remaining
		// un-tokenized source.
		this.offset += text.length();
	}
	
	public String substring(int start, int end){
		return new String(Arrays.copyOfRange(value, this.offset + start , this.offset + end));
	}
	
	public String substring(int end){
		return new String(Arrays.copyOfRange(value, offset, offset + end));
	}

	@Override
	public int length() {
		return value.length - offset;
	}

	@Override
	public char charAt(int index) {
		return value[offset + index];
	}

	@Override
	public CharSequence subSequence(int start, int end) {
		return new String(Arrays.copyOfRange(value, this.offset + start, this.offset + end));
	}

	public String toString() {
		return new String(Arrays.copyOfRange(value, offset, value.length));
	}

	public int getLineNumber() {
		return lineNumber;
	}
	
	public String getFilename(){
		return filename;
	}
}
