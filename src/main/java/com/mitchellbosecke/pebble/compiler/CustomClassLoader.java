package com.mitchellbosecke.pebble.compiler;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLClassLoader;
import java.util.Hashtable;

import com.mitchellbosecke.pebble.PebbleEngine;

public class CustomClassLoader extends ClassLoader {

	private PebbleEngine engine;

	public CustomClassLoader(PebbleEngine engine) {
		super(CustomClassLoader.class.getClassLoader());
		this.setEngine(engine);
	}

	public Class<?> loadClass(String className) throws ClassNotFoundException {
		return findClass(className);
	}

	public Class<?> findClass(String className) {
		byte classByte[];
		Class<?> result = null;
		result = (Class<?>) classes.get(className);
		if (result != null) {
			return result;
		}

		try {
			return findSystemClass(className);
		} catch (Exception e) {
		}
		try {
			String compiledTemplateDirectory = engine.getCompiledTemplateDirectory().endsWith(
					String.valueOf(File.separatorChar)) ? engine.getCompiledTemplateDirectory() : engine
					.getCompiledTemplateDirectory() + File.separatorChar;

			String classPath = getResource(
					compiledTemplateDirectory + className.replace('.', File.separatorChar) + ".class").getFile()
					.substring(1);
			classByte = loadClassData(classPath);
			result = defineClass(className, classByte, 0, classByte.length, null);
			classes.put(className, result);
			return result;
		} catch (Exception e) {
			return null;
		}
	}

	private byte[] loadClassData(String className) throws IOException {

		File f;
		f = new File(className);
		int size = (int) f.length();
		byte buff[] = new byte[size];
		FileInputStream fis = new FileInputStream(f);
		DataInputStream dis = new DataInputStream(fis);
		dis.readFully(buff);
		dis.close();
		return buff;
	}

	public PebbleEngine getEngine() {
		return engine;
	}

	public void setEngine(PebbleEngine engine) {
		this.engine = engine;
	}

	private Hashtable<String, Class<?>> classes = new Hashtable<>();
}