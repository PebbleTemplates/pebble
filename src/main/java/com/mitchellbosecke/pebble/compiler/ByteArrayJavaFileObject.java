/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Copyright (c) 2013 by Mitchell Bösecke
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.compiler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

import javax.tools.SimpleJavaFileObject;

/**
 * Are used for the output of freshly compiled pebble templates before they are
 * instantiated.
 * 
 * @author Mitchell
 * 
 */
public class ByteArrayJavaFileObject extends SimpleJavaFileObject {

	protected final ByteArrayOutputStream bos = new ByteArrayOutputStream();

	private String binaryName;

	public ByteArrayJavaFileObject(String name, Kind kind) {
		super(URI.create("string:///" + name.replace('.', '/') + kind.extension), kind);
		this.binaryName = name;
	}

	@Override
	public OutputStream openOutputStream() throws IOException {
		return bos;
	}

	@Override
	public InputStream openInputStream() throws IOException {
		return new ByteArrayInputStream(bos.toByteArray());
	}

	/**
	 * Will be used by our file manager to get the byte code that can be put
	 * into memory to instantiate our class
	 * 
	 * @return compiled byte code
	 */
	public byte[] getBytes() {
		return bos.toByteArray();
	}

	public String getBinaryName() {
		return binaryName;
	}
}
