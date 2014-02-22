package com.mitchellbosecke.pebble.compiler;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;

import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;

import org.jboss.vfs.VFS;
import org.jboss.vfs.VirtualFile;

/**
 * The Java Compiler API asks the file manager for compilation dependencies
 * which in turn delegates responsibility to this class. This class will locate
 * Pebble internal classes and return them as JavaFileObjects for the compiler
 * to use.
 * 
 * @author Mitchell
 * 
 */
class PebbleInternalsFinder {

	private static final String CLASS_FILE_EXTENSION = ".class";

	public List<JavaFileObject> find(String packageName) throws IOException {
		String packageDirectory = packageName.replaceAll("\\.", "/");

		List<JavaFileObject> result = new ArrayList<>();

		Enumeration<URL> urls = PebbleInternalsFinder.class.getClassLoader().getResources(packageDirectory);
		while (urls.hasMoreElements()) {
			result.addAll(listUnder(packageName, urls.nextElement()));
		}

		return result;
	}

	private Collection<JavaFileObject> listUnder(String packageName, URL directoryURL) {
		List<JavaFileObject> results = new ArrayList<>();

		File directory;
		try {
			directory = new File(URLDecoder.decode(directoryURL.getFile(), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException("Failed to decode using UTF-8", e);
		}

		// file system
		if (directory.isDirectory()) {
			results.addAll(listFileSystem(packageName, directory));
		}
		// jar
		else if ("jar".equalsIgnoreCase(directoryURL.getProtocol())) {
			results.addAll(listJar(directoryURL));
		}
		// virtual file system (jboss and wildfly)
		else if ("vfs".equalsIgnoreCase(directoryURL.getProtocol())) {
			return listVirtualFileSystem(packageName, directoryURL);

		}

		return results;
	}

	/**
	 * This method might be used when running pebble unit tests and the pebble
	 * classes have not yet been packaged into a jar.
	 * 
	 * @param packageName
	 * @param directory
	 * @return
	 */
	private List<JavaFileObject> listFileSystem(String packageName, File directory) {
		List<JavaFileObject> results = new ArrayList<JavaFileObject>();
		for (File child : directory.listFiles()) {
			if (child.isFile() && child.getName().endsWith(CLASS_FILE_EXTENSION)) {
				String binaryName = packageName + "." + child.getName().replace(CLASS_FILE_EXTENSION, "");
				results.add(new UriJavaFileObject(binaryName, child.toURI()));
			}
		}
		return results;
	}

	/**
	 * This method will be used when running in JBoss because JBoss abstracts
	 * the pebble jar into a virtual file.
	 * 
	 * @param packageName
	 * @param directoryURL
	 * @return
	 */
	private List<JavaFileObject> listVirtualFileSystem(String packageName, URL directoryURL) {
		List<JavaFileObject> results = new ArrayList<JavaFileObject>();
		try {

			@SuppressWarnings("deprecation")
			VirtualFile folder = VFS.getChild(directoryURL);
			if (folder.isDirectory()) {
				for (VirtualFile child : folder.getChildren()) {
					if (child.getName().endsWith(CLASS_FILE_EXTENSION)) {
						String binaryName = packageName + "." + child.getName().replace(CLASS_FILE_EXTENSION, "");
						results.add(new VirtualFileJavaFileObject(binaryName, child, Kind.CLASS));
					}
				}
			}

		} catch (Exception e) {
			throw new RuntimeException("A compilation error has occurred", e);
		}
		return results;
	}

	/**
	 * The most common method used when the pebble classes are located in a jar.
	 * 
	 * @param packageFolderURL
	 * @return
	 */
	private List<JavaFileObject> listJar(URL packageFolderURL) {
		List<JavaFileObject> results = new ArrayList<JavaFileObject>();
		try {
			String jarUri = packageFolderURL.toExternalForm().split("!")[0];

			JarURLConnection connection = (JarURLConnection) packageFolderURL.openConnection();
			String rootEntryName = connection.getEntryName();
			int rootEnd = rootEntryName.length() + 1;
			Enumeration<JarEntry> entries = connection.getJarFile().entries();

			while (entries.hasMoreElements()) {
				JarEntry entry = entries.nextElement();
				String name = entry.getName();

				if (name.startsWith(rootEntryName) && name.indexOf('/', rootEnd) == -1
						&& name.endsWith(CLASS_FILE_EXTENSION)) {

					URI uri = URI.create(jarUri + "!/" + name);
					String binaryName = name.replaceAll("/", ".").replaceAll(CLASS_FILE_EXTENSION + "$", "");

					results.add(new UriJavaFileObject(binaryName, uri));
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("A compilation error has occurred", e);
		}
		return results;
	}
}