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

import com.mitchellbosecke.pebble.template.PebbleTemplate;

public class InMemoryForwardingFileManager extends
		ForwardingJavaFileManager<StandardJavaFileManager> {

	private static InMemoryForwardingFileManager instance;

	private final StandardJavaFileManager fileManager;

	/**
	 * Instance of JavaClassObject that will store the compiled bytecode of our
	 * class
	 */
	private final Map<String, ByteArrayJavaFileObject> classObjects = new HashMap<>();

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
				ByteArrayJavaFileObject classObject = classObjects.get(name);

				if (classObject == null) {
					return super.loadClass(name);
				} else {
					byte[] b = classObject.getBytes();
					return super.defineClass(name, b, 0, b.length);
				}

			}
		};
	}
	
	@Override
	public FileObject getFileForInput(Location location, String packageName, String relativeName) throws IOException{
		return super.getFileForInput(location, packageName, relativeName);
	}

	@Override
	public FileObject getFileForOutput(Location location, String packageName,
			String relativeName, FileObject sibling) throws IOException {
		if (packageName.startsWith(PebbleTemplate.COMPILED_PACKAGE_NAME)) {
			ByteArrayJavaFileObject buffer = new ByteArrayJavaFileObject(
					relativeName, Kind.CLASS);
			classObjects.put(relativeName, buffer);
			return buffer;
		} else {
			return super.getFileForOutput(location, packageName, relativeName,
					sibling);
		}
	}
	
	@Override
	public JavaFileObject getJavaFileForInput(Location location,
			String className, Kind kind) throws IOException {
		return super.getJavaFileForInput(location, className, kind);
	}

	@Override
	public JavaFileObject getJavaFileForOutput(Location location,
			String className, Kind kind, FileObject sibling) throws IOException {
		if (kind == Kind.CLASS) {
			ByteArrayJavaFileObject buffer = new ByteArrayJavaFileObject(
					className, kind);
			classObjects.put(className, buffer);
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
			out.addAll(new ArrayList<>(classObjects.values()));
		} 
		
		for(JavaFileObject obj : fileManager.list(location, packageName, kinds, recurse)){
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
	public boolean hasLocation(Location location){
		return super.hasLocation(location);
	}
	
	@Override
	public boolean isSameFile(FileObject obj1, FileObject obj2){
		return super.isSameFile( obj1, obj2 );
	}

}