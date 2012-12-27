/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Copyright (c) 2012 Mitchell Bosecke.
 * 
 * This work is licensed under the Creative Commons Attribution-ShareAlike 3.0 
 * Unported License. To view a copy of this license, visit 
 * http://creativecommons.org/licenses/by-sa/3.0/
 ******************************************************************************/
package com.mitchellbosecke.pebble.compiler;

import java.io.IOException;
import java.net.URI;

import javax.tools.SimpleJavaFileObject;

/**
 * Creates a dynamic source code file object
 * 
 * This is an example of how we can prepare a dynamic java source code for
 * compilation. This class reads the java code from a string and prepares a
 * JavaFileObject
 * 
 */
public class DynamicJavaSourceCodeObject extends SimpleJavaFileObject {
	private String qualifiedName;
	private String sourceCode;

	/**
	 * Converts the name to an URI, as that is the format expected by
	 * JavaFileObject
	 * 
	 * 
	 * @param fully
	 *            qualified name given to the class file
	 * @param code
	 *            the source code string
	 */
	protected DynamicJavaSourceCodeObject(String name, String code) {
		super(URI.create("string:///" + name.replaceAll("\\.", "/")
				+ Kind.SOURCE.extension), Kind.SOURCE);
		this.qualifiedName = name;
		this.sourceCode = code;
	}

	@Override
	public CharSequence getCharContent(boolean ignoreEncodingErrors)
			throws IOException {
		return sourceCode;
	}

	public String getQualifiedName() {
		return qualifiedName;
	}

	public void setQualifiedName(String qualifiedName) {
		this.qualifiedName = qualifiedName;
	}

	public String getSourceCode() {
		return sourceCode;
	}

	public void setSourceCode(String sourceCode) {
		this.sourceCode = sourceCode;
	}
}
