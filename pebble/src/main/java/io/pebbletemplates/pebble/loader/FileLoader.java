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
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * This loader searches for templates under a configured filesystem directory. Symbolic links that
 * resolve outside that directory are rejected.
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
    InputStream is = null;
    try {
      Path file = this.getFile(templateName);
      is = Files.newInputStream(file, LinkOption.NOFOLLOW_LINKS);
      return new BufferedReader(new InputStreamReader(is, this.charset));
    } catch (UnsupportedEncodingException e) {
      if (is != null) {
        try {
          is.close();
        } catch (IOException closeException) {
          e.addSuppressed(closeException);
        }
      }
      throw new LoaderException(e, String.format("Invalid charset '%s'", this.charset));
    } catch (IOException e) {
      throw new LoaderException(e, String.format("Could not find template [prefix='%s', templateName='%s']", this.prefix, templateName));
    }
  }

  private Path getFile(String templateName) throws IOException {
    Path baseDirPath = Paths.get(this.getPrefix()).normalize();
    Path resolvedPath = resolveTemplatePath(templateName, baseDirPath);
    Path realBaseDirPath = baseDirPath.toRealPath();
    Path realResolvedPath = resolvedPath.toRealPath();
    ensurePathIsWithinBase(realResolvedPath, realBaseDirPath, templateName);
    ensureRegularFile(realResolvedPath);

    return realResolvedPath;
  }

  private Path resolveTemplatePath(String templateName, Path baseDirPath) {
    String fileName = templateName + (this.getSuffix() == null ? "" : this.getSuffix());
    fileName = PathUtils.sanitize(fileName, File.separatorChar);
    Path userPath = Paths.get(fileName);
    if (userPath.isAbsolute()) {
      throw new LoaderException(null, String.format("templateName '%s' must be relative", fileName));
    }

    Path resolvedPath = baseDirPath.resolve(userPath).normalize();
    logger.trace("Looking for template in {}.", resolvedPath);
    ensurePathIsWithinBase(resolvedPath, baseDirPath, fileName);
    return resolvedPath;
  }

  private void ensurePathIsWithinBase(Path path, Path baseDirPath, String templateName) {
    if (!path.startsWith(baseDirPath)) {
      throw new LoaderException(null, String.format(
          "template is not in the base directory path [baseDir='%s', templateName='%s']",
          this.prefix, templateName));
    }
  }

  private void ensureRegularFile(Path path) throws NoSuchFileException {
    if (!Files.isRegularFile(path, LinkOption.NOFOLLOW_LINKS)) {
      throw new NoSuchFileException(path.toString());
    }
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
    try {
      this.getFile(templateName);
      return true;
    } catch (NoSuchFileException e) {
      return false;
    } catch (IOException e) {
      throw new LoaderException(e, String.format("Could not find template [prefix='%s', templateName='%s']", this.prefix, templateName));
    }
  }
}
