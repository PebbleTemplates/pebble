package com.mitchellbosecke.pebble.compiler;

import java.io.IOException;
import java.security.SecureClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;

public class InMemoryForwardingFileManager extends ForwardingJavaFileManager<StandardJavaFileManager> {

	private static final Logger logger = LoggerFactory.getLogger(InMemoryForwardingFileManager.class);

	private final Map<String, ByteArrayJavaFileObject> javaFileObjects = new HashMap<>();

	public InMemoryForwardingFileManager() {
		super(ToolProvider.getSystemJavaCompiler().getStandardFileManager(null, Locale.getDefault(), null));
	}

	@Override
	public ClassLoader getClassLoader(Location location) {

		return new SecureClassLoader(InMemoryForwardingFileManager.class.getClassLoader()) {

			@Override
			protected Class<?> findClass(String name) throws ClassNotFoundException {
				logger.debug(String.format("Finding class: %s", name));

				ByteArrayJavaFileObject fileObject = javaFileObjects.get(name);
				
				// the fileobject is no longer required
				javaFileObjects.remove(name);
				
				Class<?> clazz;
				if (fileObject != null) {
					byte[] bytes = fileObject.getBytes();
					clazz = defineClass(name, bytes, 0, bytes.length);
				} else {
					throw new ClassNotFoundException(name);
				}

				return clazz;

			}
		};
	}

	@Override
	public JavaFileObject getJavaFileForOutput(Location location, String className, Kind kind, FileObject sibling)
			throws IOException {
		if (kind == Kind.CLASS) {
			ByteArrayJavaFileObject buffer = new ByteArrayJavaFileObject(className, kind);
			javaFileObjects.put(className, buffer);
			return buffer;
		} else {
			return super.getJavaFileForOutput(location, className, kind, sibling);
		}
	}

	@Override
	public Iterable<JavaFileObject> list(Location location, String packageName, Set<JavaFileObject.Kind> kinds,
			boolean recurse) throws IOException {

		ArrayList<JavaFileObject> out = new ArrayList<>();

		if (packageName.startsWith(PebbleTemplateImpl.COMPILED_PACKAGE_NAME)) {
			out.addAll(new ArrayList<>(javaFileObjects.values()));
		}

		for (JavaFileObject obj : super.list(location, packageName, kinds, recurse)) {
			out.add(obj);
		}

		return out;
	}

	@Override
	public String inferBinaryName(Location location, JavaFileObject file) {
		if (file instanceof ByteArrayJavaFileObject) {
			return ((ByteArrayJavaFileObject) file).getBinaryName();
		} else {
			return super.inferBinaryName(location, file);
		}
	}

	@Override
	public void close() throws IOException {
		super.close();
	}

}