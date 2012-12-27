package com.mitchellbosecke.pebble.compiler;

import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.node.Node;
import com.mitchellbosecke.pebble.node.NodeRoot;
import com.mitchellbosecke.pebble.template.AbstractPebbleTemplate;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

public class CompilerImpl implements Compiler {

	private final PebbleEngine engine;
	private StringBuilder builder;
	private int indentation;
	private String className;

	public CompilerImpl(PebbleEngine engine) {
		this.engine = engine;
	}

	@Override
	public Compiler compile(Node node) {
		this.builder = new StringBuilder();
		this.indentation = 0;

		if (node instanceof NodeRoot) {
			this.className = engine.getTemplateClassName(((NodeRoot) node)
					.getFilename());
		}
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
	public Compiler appendContent(String string) {
		write("append(").string(string).raw(");");
		return this;
	}

	@Override
	public PebbleTemplate compileToJava() {

		/* Creating dynamic java source code file object */
		DynamicJavaSourceCodeObject fileObject = new DynamicJavaSourceCodeObject(
				"com.mitchellbosecke.pebble.template." + this.className,
				getSource());
		JavaFileObject javaFileObjects[] = new JavaFileObject[] { fileObject };

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
		StandardJavaFileManager stdFileManager = compiler
				.getStandardFileManager(null, Locale.getDefault(), null);

		/*
		 * Prepare a list of compilation units (java source code file objects)
		 * to input to compilation task
		 */
		Iterable<? extends JavaFileObject> compilationUnits = Arrays
				.asList(javaFileObjects);

		/* Prepare any compilation options to be used during compilation */
		// In this example, we are asking the compiler to place the output files
		// under bin folder.
		String[] compileOptions = new String[] { "-d", "target/classes" };
		Iterable<String> compilationOptions = Arrays.asList(compileOptions);

		/* Create a diagnostic controller, which holds the compilation problems */
		DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();

		/*
		 * Create a compilation task from compiler by passing in the required
		 * input objects prepared above
		 */
		CompilationTask compilerTask = compiler.getTask(null, stdFileManager,
				diagnostics, compilationOptions, null, compilationUnits);

		// Perform the compilation by calling the call method on compilerTask
		// object.
		boolean status = compilerTask.call();

		if (!status) {// If compilation error occurs
			/* Iterate through each compilation problem and print it */
			for (Diagnostic<?> diagnostic : diagnostics.getDiagnostics()) {
				System.out.format("Error on line %d in %s",
						diagnostic.getLineNumber(), diagnostic);
			}
		}
		try {
			stdFileManager.close();// Close the file manager
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			@SuppressWarnings("unchecked")
			Class<PebbleTemplate> clazz = (Class<PebbleTemplate>) Class
					.forName(fileObject.getQualifiedName());
			AbstractPebbleTemplate template = (AbstractPebbleTemplate) clazz
					.newInstance();
			template.setSourceCode(getSource());
			return template;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

}
