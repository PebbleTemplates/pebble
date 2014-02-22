package com.mitchellbosecke.pebble.compiler;

import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URL;
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
	private final ClassLoader classLoader;
	private static final String CLASS_FILE_EXTENSION = ".class";

	public PebbleInternalsFinder() {
		this.classLoader = PebbleInternalsFinder.class.getClassLoader();
	}

	public List<JavaFileObject> find(String packageName) throws IOException {
		String packageDirectory = packageName.replaceAll("\\.", "/");

		List<JavaFileObject> result = new ArrayList<>();

		Enumeration<URL> urls = classLoader.getResources(packageDirectory);
		while (urls.hasMoreElements()) {
			result.addAll(listUnder(packageName, urls.nextElement()));
		}

		return result;
	}

	private Collection<JavaFileObject> listUnder(String packageName, URL directoryURL) {
		List<JavaFileObject> results = new ArrayList<>();

		// jar
		if ("jar".equalsIgnoreCase(directoryURL.getProtocol())) {
			results.addAll(processJar(directoryURL));
		}
		// virtual file system (jboss and wildfly)
		else if ("vfs".equalsIgnoreCase(directoryURL.getProtocol())) {
			return processVirtualFileSystem(packageName, directoryURL);

		}

		return results;
	}

	private List<JavaFileObject> processVirtualFileSystem(String packageName, URL directoryURL) {
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

	private List<JavaFileObject> processJar(URL packageFolderURL) {
		List<JavaFileObject> results = new ArrayList<JavaFileObject>();
		try {
			String jarUri = packageFolderURL.toExternalForm().split("!")[0];

			JarURLConnection jarConn = (JarURLConnection) packageFolderURL.openConnection();
			String rootEntryName = jarConn.getEntryName();
			int rootEnd = rootEntryName.length() + 1;

			Enumeration<JarEntry> entryEnum = jarConn.getJarFile().entries();
			while (entryEnum.hasMoreElements()) {
				JarEntry jarEntry = entryEnum.nextElement();
				String name = jarEntry.getName();
				if (name.startsWith(rootEntryName) && name.indexOf('/', rootEnd) == -1
						&& name.endsWith(CLASS_FILE_EXTENSION)) {
					URI uri = URI.create(jarUri + "!/" + name);
					String binaryName = name.replaceAll("/", ".");
					binaryName = binaryName.replaceAll(CLASS_FILE_EXTENSION + "$", "");

					results.add(new UriJavaFileObject(binaryName, uri));
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("A compilation error has occurred", e);
		}
		return results;
	}
}