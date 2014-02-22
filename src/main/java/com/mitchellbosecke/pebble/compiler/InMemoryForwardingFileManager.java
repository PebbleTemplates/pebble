package com.mitchellbosecke.pebble.compiler;

import java.io.IOException;
import java.security.SecureClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.StandardJavaFileManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;

public class InMemoryForwardingFileManager extends ForwardingJavaFileManager<StandardJavaFileManager> {

	private static final Logger logger = LoggerFactory.getLogger(InMemoryForwardingFileManager.class);

	/**
	 * In memory java file objects
	 */
	private final Map<String, ByteArrayJavaFileObject> javaFileObjects = new HashMap<>();

	/**
	 * Used to find other pebble classes during compilation
	 */
	private final PebbleInternalsFinder finder = new PebbleInternalsFinder();

	public InMemoryForwardingFileManager(ClassLoader classLoader, StandardJavaFileManager standardFileManager) {
		super(standardFileManager);
	}

	@Override
	public ClassLoader getClassLoader(Location location) {

		return new SecureClassLoader(InMemoryForwardingFileManager.class.getClassLoader()) {

			@Override
			protected Class<?> findClass(String name) throws ClassNotFoundException {
				logger.debug(String.format("Finding class: %s", name));

				ByteArrayJavaFileObject fileObject = javaFileObjects.get(name);

				/*
				 * Because pebble only compiles one template at a time, we can
				 * remove this java file object from memory as it will no longer
				 * be needed.
				 */
				javaFileObjects.remove(name);

				Class<?> clazz;
				if (fileObject != null) {
					byte[] bytes = fileObject.getBytes();
					clazz = defineClass(name, bytes, 0, bytes.length);
					resolveClass(clazz);
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

	/**
	 * The compiler will call this method to find dependencies (classes required
	 * during compilation).
	 */
	@Override
	public Iterable<JavaFileObject> list(Location location, String packageName, Set<JavaFileObject.Kind> kinds,
			boolean recurse) throws IOException {

		ArrayList<JavaFileObject> result = new ArrayList<>();
		Iterable<JavaFileObject> parentResults = super.list(location, packageName, kinds, recurse);

		/*
		 * add classes stored in this file manager (probably not necessary now
		 * that we are only compiling one template at a time)
		 */
		if (PebbleTemplateImpl.COMPILED_PACKAGE_NAME.equals(packageName)) {
			result.addAll(new ArrayList<>(javaFileObjects.values()));
		}

		// use the PebbleInternalsFinder to find pebble internal classes
		if (packageName.startsWith(PebbleEngine.class.getPackage().getName())) {
			result.addAll(finder.find(packageName));
		}

		// combine parent results
		for (JavaFileObject obj : parentResults) {
			result.add(obj);
		}

		return result;
	}

	@Override
	public String inferBinaryName(Location location, JavaFileObject file) {
		if (file instanceof ByteArrayJavaFileObject) {
			return ((ByteArrayJavaFileObject) file).getBinaryName();
		} else if (file instanceof UriJavaFileObject) {
			return ((UriJavaFileObject) file).getBinaryName();
		} else if (file instanceof VirtualFileJavaFileObject) {
			return ((VirtualFileJavaFileObject) file).getBinaryName();
		} else {
			return super.inferBinaryName(location, file);
		}
	}

	@Override
	public void close() throws IOException {
		super.close();
	}

}