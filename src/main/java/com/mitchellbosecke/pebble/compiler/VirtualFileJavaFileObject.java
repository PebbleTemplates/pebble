package com.mitchellbosecke.pebble.compiler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;

import javax.tools.SimpleJavaFileObject;

import org.jboss.vfs.VirtualFile;

/**
 * JavaFileObjects that wrap around Jboss/Wildfly virtual files.
 * 
 * @author Mitchell
 * 
 */
public class VirtualFileJavaFileObject extends SimpleJavaFileObject {

	private final VirtualFile virtualFile;

	private final String binaryName;

	public VirtualFileJavaFileObject(String binaryName, VirtualFile virtualFile, Kind kind) throws URISyntaxException {
		super(virtualFile.asFileURI(), kind);
		this.binaryName = binaryName;
		this.virtualFile = virtualFile;
	}

	@Override
	public OutputStream openOutputStream() throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public InputStream openInputStream() throws IOException {
		return virtualFile.openStream();
	}

	public String getBinaryName() {
		return binaryName;
	}
}
