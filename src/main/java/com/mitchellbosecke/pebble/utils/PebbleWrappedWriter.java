package com.mitchellbosecke.pebble.utils;

import java.io.IOException;
import java.io.Writer;

import com.mitchellbosecke.pebble.error.PebbleException;

public class PebbleWrappedWriter {

	private final Writer writer;

	public PebbleWrappedWriter(Writer writer) {
		this.writer = writer;
	}

	public void write(String text) throws PebbleException {
		try {
			writer.write(text);
		} catch (IOException e) {
			e.printStackTrace();
			throw new PebbleException("Error occurred while attempted to write to provided Writer.");
		}
	}

	public void flush() throws PebbleException {
		try {
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
			throw new PebbleException("Unable to flush provided Writer.");
		}
	}

	public Writer getWriter() {
		return this.writer;
	}

}
