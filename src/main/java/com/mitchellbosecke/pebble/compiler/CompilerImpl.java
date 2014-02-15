/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Original work Copyright (c) 2009-2013 by the Twig Team
 * Modified work Copyright (c) 2013 by Mitchell Bösecke
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.compiler;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.node.Node;

public class CompilerImpl implements Compiler {

	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(CompilerImpl.class);

	private final PebbleEngine engine;
	private final List<NodeVisitor> visitors;

	private StringBuilder builder;
	private int indentation;

	public CompilerImpl(PebbleEngine engine, List<NodeVisitor> visitors) {
		this.engine = engine;
		this.visitors = visitors;
	}

	@Override
	public Compiler compile(Node node) {
		this.builder = new StringBuilder();
		this.indentation = 0;

		node.compile(this);

		for (NodeVisitor visitor : visitors) {
			node.accept(visitor);
		}
		return this;
	}

	@Override
	public Compiler subcompile(Node node) {
		return subcompile(node, true);
	}

	@Override
	public Compiler subcompile(Node node, boolean raw) {
		if (!raw) {
			addIndentation();
		}
		node.compile(this);
		return this;
	}

	@Override
	public Compiler write(String string) {
		addIndentation();
		builder.append(string);
		return this;
	}

	@Override
	public Compiler raw(String string) {
		builder.append(string);
		return this;
	}

	@Override
	public Compiler string(String string) {
		// quotations and backslash
		string = string.replaceAll("(\"|\'|\\\\)", "\\\\$1");
		// new lines, carriage return, and form feed
		string = string.replaceAll("(\\n|\\r|\\f)", "\\\\n");
		// tab characters
		string = string.replaceAll("(\\t)", "\\\\t");
		builder.append("\"").append(string).append("\"");
		return this;
	}

	private Compiler addIndentation() {
		for (int i = 0; i < indentation; ++i) {
			builder.append("    ");
		}
		return this;
	}

	@Override
	public Compiler indent() {
		indentation++;
		return this;
	}

	@Override
	public Compiler outdent() {
		indentation--;
		return this;
	}

	@Override
	public Compiler newline() {
		return newline(1);
	}

	@Override
	public Compiler newline(int numOfNewLines) {
		for (int i = 0; i < numOfNewLines; i++) {
			raw("\n");
		}
		return this;
	}

	@Override
	public String getSource() {
		return builder.toString();
	}

	@Override
	public PebbleEngine getEngine() {
		return engine;
	}
}
