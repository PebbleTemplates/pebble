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

import com.mitchellbosecke.pebble.template.PebbleTemplate;

public class InMemoryForwardingFileManager extends
		ForwardingJavaFileManager<StandardJavaFileManager> {

	private static final Logger logger = LoggerFactory
			.getLogger(InMemoryForwardingFileManager.class);

	private static InMemoryForwardingFileManager instance;

	private final StandardJavaFileManager fileManager;

	/**
	 * Instance of JavaClassObject that will store the compiled bytecode of our
	 * class
	 */
	private final Map<String, ByteArrayJavaFileObject> javaFileObjects = new HashMap<>();

	/**
	 * Will initialize the manager with the specified standard java file manager
	 * 
	 * @param standardManger
	 */
	private InMemoryForwardingFileManager(
			StandardJavaFileManager standardManager) {
		super(standardManager);
		fileManager = standardManager;
	}

	public static InMemoryForwardingFileManager getInstance(
			StandardJavaFileManager manager) {
		if (instance == null) {
			instance = new InMemoryForwardingFileManager(manager);
		}
		return instance;
	}

	@Override
	public ClassLoader getClassLoader(Location location) {

		return new SecureClassLoader(
				InMemoryForwardingFileManager.class.getClassLoader()) {

			@Override
			protected Class<?> findClass(String name)
					throws ClassNotFoundException {
				logger.debug(String.format("Finding class: %s", name));

				ByteArrayJavaFileObject fileObject = javaFileObjects.get(name);
				Class<?> clazz;
				if(fileObject != null){
					byte[] bytes = fileObject.getBytes();
					clazz = defineClass(name, bytes, 0, bytes.length);
				}else{
					throw new ClassNotFoundException(name);
				}
				
				return clazz;

			}
		};
	}

	@Override
	public JavaFileObject getJavaFileForOutput(Location location,
			String className, Kind kind, FileObject sibling) throws IOException {
		if (kind == Kind.CLASS) {
			ByteArrayJavaFileObject buffer = new ByteArrayJavaFileObject(
					className, kind);
			javaFileObjects.put(className, buffer);
			return buffer;
		} else {
			return super.getJavaFileForOutput(location, className, kind,
					sibling);
		}
	}

	@Override
	public Iterable<JavaFileObject> list(Location location, String packageName,
			Set<JavaFileObject.Kind> kinds, boolean recurse) throws IOException {

		ArrayList<JavaFileObject> out = new ArrayList<>();

		if (packageName.startsWith(PebbleTemplate.COMPILED_PACKAGE_NAME)) {
			out.addAll(new ArrayList<>(javaFileObjects.values()));
		}

		for (JavaFileObject obj : fileManager.list(location, packageName,
				kinds, recurse)) {
			out.add(obj);
		}

		return out;
	}

	@Override
	public String inferBinaryName(Location location, JavaFileObject file) {
		if (file instanceof ByteArrayJavaFileObject) {
			return ((ByteArrayJavaFileObject) file).getBinaryName();
		} else {
			return fileManager.inferBinaryName(location, file);
		}
	}

	@Override
	public boolean hasLocation(Location location) {
		return super.hasLocation(location);
	}

	@Override
	public boolean isSameFile(FileObject obj1, FileObject obj2) {
		return super.isSameFile(obj1, obj2);
	}

}