package com.mitchellbosecke.pebble.utils;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;

/**
 * The only use of this class is in the compile method of the main PebbleEngine.
 * The PebbleEngine will store the template source in an intermediary string
 * before it passes this string to the Lexer. I would like to change that
 * behaviour, and eventually have the PebbleEngine pass the Reader directly to
 * the Lexer. This change would make this IOUtils class obsolete.
 * 
 * @author Mitchell
 * 
 */
public class IOUtils {

	public static String toString(Reader reader) throws IOException {
		StringWriter writer = new StringWriter();

		char[] buffer = new char[1024 * 4];
		int n = 0;
		while ((n = reader.read(buffer)) != -1) {
			writer.write(buffer, 0, n);
		}
		return writer.toString();
	}

}
