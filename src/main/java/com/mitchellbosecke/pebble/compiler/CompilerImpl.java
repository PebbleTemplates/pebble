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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.node.Node;
import com.mitchellbosecke.pebble.template.AbstractPebbleTemplate;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

public class CompilerImpl implements Compiler {

	private static final Logger logger = LoggerFactory.getLogger(CompilerImpl.class);

	private final PebbleEngine engine;
	private StringBuilder builder;
	private int indentation;

	public CompilerImpl(PebbleEngine engine) {
		this.engine = engine;
	}

	@Override
	public Compiler compile(Node node) {
		this.builder = new StringBuilder();
		this.indentation = 0;

		node.compile(this);
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
		builder.append("\"" + string + "\"");
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
	public String getSource() {
		return builder.toString();
	}

	@Override
	public PebbleEngine getEngine() {
		return engine;
	}

	@Override
	public PebbleTemplate compileToJava(String javaSource, String className) {

		String fullClassName = PebbleTemplate.COMPILED_PACKAGE_NAME + "." + className;

		/* Instantiating the java compiler */
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

		/*
		 * Retrieving the standard file manager from compiler object, which is
		 * used to provide basic building block for customizing how a compiler
		 * reads and writes to files.
		 * 
		 * The same file manager can be reopened for another compiler task. Thus
		 * we reduce the overhead of scanning through file system and jar files
		 * each time
		 */
		// StandardJavaFileManager stdFileManager =
		// compiler.getStandardFileManager(null, Locale.getDefault(), null);
		ClassFileManager fileManager = ClassFileManager.getInstance(compiler.getStandardFileManager(null,
				Locale.getDefault(), null));

		/*
		 * Prepare a list of compilation units (java source code file objects)
		 * to input to compilation task
		 */
		List<JavaFileObject> compilationUnits = new ArrayList<>();
		compilationUnits.add(new StringSourceFileObject(fullClassName, javaSource));

		/* Prepare any compilation options to be used during compilation */
		// compiledTemplatesPath = compiledTemplatesPath.replace(" ", "\\ ");

		// logger.info(String.format("Compiling to %s", compiledTemplatesPath));
		// String[] compileOptions = new String[] { "-d", compiledTemplatesPath
		// };
		// Iterable<String> compilationOptions = Arrays.asList(compileOptions);

		/* Create a diagnostic controller, which holds the compilation problems */
		DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();

		/*
		 * Create a compilation task from compiler by passing in the required
		 * input objects prepared above
		 */
		CompilationTask compilerTask = compiler.getTask(null, fileManager, diagnostics, null, null, compilationUnits);

		// Perform the compilation by calling the call method on compilerTask
		// object.
		boolean status = compilerTask.call();

		if (!status) {// If compilation error occurs
			/* Iterate through each compilation problem and print it */
			for (Diagnostic<?> diagnostic : diagnostics.getDiagnostics()) {
				logger.error(String.format("Error on line %d in %s", diagnostic.getLineNumber(), diagnostic));
			}
		}
		try {
			fileManager.close();// Close the file manager
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {

			// PebbleClassLoader pebbleClassLoader =
			// PebbleClassLoader.getInstance();
			// pebbleClassLoader.register(fullClassName,
			// fileManager.getCompiledJavaClassObject());

			AbstractPebbleTemplate template = (AbstractPebbleTemplate) fileManager.getClassLoader(null)
					.loadClass(fullClassName).newInstance();
			template.setSourceCode(getSource());
			return template;
		} catch (IllegalAccessException | InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
		}

		return null;
	}

}
