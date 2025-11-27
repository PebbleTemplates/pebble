/*
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package io.pebbletemplates.pebble.loader;

import io.pebbletemplates.pebble.error.LoaderException;
import io.pebbletemplates.pebble.utils.PathUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * This loader searches for a file located anywhere on the filesystem. It uses java.io.File to
 * perform the lookup.
 *
 * @author mbosecke
 */
public class FileLoader implements Loader<String> {

  private static final Logger logger = LoggerFactory.getLogger(FileLoader.class);

  private String prefix;
  private String suffix;
  private String charset = "UTF-8";

  public FileLoader(String prefix) {
    this.setPrefix(prefix);
  }

  @Override
  public Reader getReader(String templateName) {
    File file = this.getFile(templateName);
    try {
      InputStream is = new FileInputStream(file);
      return new BufferedReader(new InputStreamReader(is, this.charset));
    } catch (FileNotFoundException e) {
      throw new LoaderException(null, String.format("Could not find template [prefix='%s', templateName='%s']", this.prefix, templateName));
    } catch (UnsupportedEncodingException e) {
      throw new LoaderException(e, String.format("Invalid charset '%s'", this.charset));
    }
  }

  private File getFile(String templateName) {
    StringBuilder path = new StringBuilder();
    path.append(this.getPrefix());
    if (!this.getPrefix().endsWith(String.valueOf(File.separatorChar))) {
      path.append(File.separatorChar);
    }

    templateName = templateName + (this.getSuffix() == null ? "" : this.getSuffix());
    templateName = PathUtils.sanitize(templateName, File.separatorChar);
    logger.trace("Looking for template in {}{}.", path, templateName);

    this.checkIfDirectoryTraversal(templateName);
    return new File(path.toString(), templateName);
  }

  public String getSuffix() {
    return this.suffix;
  }

  @Override
  public void setSuffix(String suffix) {
    this.suffix = suffix;
  }

  public String getPrefix() {
    return this.prefix;
  }

  @Override
  public void setPrefix(String prefix) {
    if (prefix == null) {
      throw new LoaderException(null, "Prefix cannot be null");
    }
    String trimmedPrefix = prefix.trim();
    if (trimmedPrefix.isEmpty()) {
      throw new LoaderException(null, "Prefix cannot be empty");
    }
    if (!Paths.get(trimmedPrefix).isAbsolute()) {
      throw new LoaderException(null, "Prefix must be an absolute path");
    }
    this.prefix = trimmedPrefix;
  }

  public String getCharset() {
    return this.charset;
  }

  @Override
  public void setCharset(String charset) {
    this.charset = charset;
  }

  @Override
  public String resolveRelativePath(String relativePath, String anchorPath) {
    return PathUtils.resolveRelativePath(relativePath, anchorPath, File.separatorChar);
  }

  @Override
  public String createCacheKey(String templateName) {
    return templateName;
  }

  @Override
  public boolean resourceExists(String templateName) {
    return this.getFile(templateName).exists();
  }

  private void checkIfDirectoryTraversal(String templateName) {
    Path baseDirPath = Paths.get(prefix);
    Path userPath = Paths.get(templateName);
    if (userPath.isAbsolute()) {
      throw new LoaderException(null, String.format("templateName '%s' must be relative", templateName));
    }

    // Join the two paths together, then normalize so that any ".." elements
    // in the userPath can remove parts of baseDirPath.
    // (e.g. "/foo/bar/baz" + "../attack" -> "/foo/bar/attack")
    Path resolvedPath = baseDirPath.resolve(userPath).normalize();

    // Make sure the resulting path is still within the required directory.
    // (In the example above, "/foo/bar/attack" is not.)
    if (!resolvedPath.startsWith(baseDirPath)) {
      throw new LoaderException(null, String.format("User path escapes the base path [prefix='%s', templateName='%s']", this.prefix, templateName));
    }
  }
}
