package com.mitchellbosecke.pebble.utils;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;

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
