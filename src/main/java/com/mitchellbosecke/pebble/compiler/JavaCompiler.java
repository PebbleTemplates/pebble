package com.mitchellbosecke.pebble.compiler;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.error.CompilationException;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;

public class JavaCompiler {

	private static final Logger logger = LoggerFactory.getLogger(JavaCompiler.class);

	public static PebbleTemplateImpl compile(PebbleEngine engine, String javaSource, String className)
			throws CompilationException {

		String fullClassName = PebbleTemplateImpl.COMPILED_PACKAGE_NAME + "." + className;

		/* Instantiating the java compiler */
		javax.tools.JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

		/*
		 * Retrieving the standard file manager from compiler object, which is
		 * used to provide basic building block for customizing how a compiler
		 * reads and writes to files.
		 * 
		 * The same file manager can be reopened for another compiler task. Thus
		 * we reduce the overhead of scanning through file system and jar files
		 * each time
		 */
		StandardJavaFileManager standardFileManager = ToolProvider.getSystemJavaCompiler().getStandardFileManager(null,
				Locale.getDefault(), null);
		InMemoryForwardingFileManager fileManager = new InMemoryForwardingFileManager(
				JavaCompiler.class.getClassLoader(), standardFileManager);

		/*
		 * Prepare a list of compilation units (java source code file objects)
		 * to input to compilation task
		 */
		List<JavaFileObject> compilationUnits = new ArrayList<>();
		compilationUnits.add(new StringSourceJavaFileObject(fullClassName, javaSource));

		// prepare compilation options
		List<String> compilationOptions = new ArrayList<>();

		// build classpath
		StringBuilder classPathBuilder = new StringBuilder();
		classPathBuilder.append(System.getProperty("java.class.path"));
		classPathBuilder.append(System.getProperty("path.separator")).append(getPebbleJarLocation());
		compilationOptions.addAll(Arrays.asList("-classpath", classPathBuilder.toString()));

		/* Create a diagnostic controller, which holds the compilation problems */
		DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();

		/*
		 * Create a compilation task from compiler by passing in the required
		 * input objects prepared above
		 */
		CompilationTask compilerTask = compiler.getTask(null, fileManager, diagnostics, compilationOptions, null,
				compilationUnits);

		boolean status = compilerTask.call();

		if (!status) {// If compilation error occurs
			/* Iterate through each compilation problem and print it */
			for (Diagnostic<?> diagnostic : diagnostics.getDiagnostics()) {
				logger.error(String.format("Error on line %d in %s", diagnostic.getLineNumber(), diagnostic));
			}

			throw new CompilationException(null, "Compilation error occurred");
		}
		try {
			fileManager.close();// Close the file manager
		} catch (IOException e) {
			e.printStackTrace();
		}

		PebbleTemplateImpl template;
		try {

			ClassLoader cl = fileManager.getClassLoader(null);
			Constructor<?> constructor = cl.loadClass(fullClassName).getDeclaredConstructor(String.class,
					PebbleEngine.class);

			constructor.setAccessible(true);
			template = (PebbleTemplateImpl) constructor.newInstance(javaSource, engine);

		} catch (IllegalAccessException | NoSuchMethodException | SecurityException | InstantiationException
				| InvocationTargetException | IllegalArgumentException e) {
			e.printStackTrace();
			throw new CompilationException(e, "Compilation error occurred");
		} catch (ClassNotFoundException e) {
			throw new CompilationException(e, String.format("Could not find generated class: %s", fullClassName));
		}

		return template;
	}

	private static String getPebbleJarLocation() throws CompilationException {

		String location = null;
		try {
			URL url = PebbleTemplateImpl.class.getProtectionDomain().getCodeSource().getLocation();
			URI uri = urlToUri(url);

			location = uri.getPath();

		} catch (URISyntaxException e) {
			throw new CompilationException(e, "A compilation error occurred");
		}
		return location;
	}

	/**
	 * Handles spaces and weird characters properly
	 * 
	 * @throws URISyntaxException
	 */
	private static URI urlToUri(URL u) throws URISyntaxException {
		return new URI(u.getProtocol(), u.getAuthority(), null, -1, u.getPath(), u.getQuery(), u.getRef());
	}

}
