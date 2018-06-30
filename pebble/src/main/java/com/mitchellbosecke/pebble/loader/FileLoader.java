/*
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.mitchellbosecke.pebble.loader;

import com.mitchellbosecke.pebble.error.LoaderException;
import com.mitchellbosecke.pebble.utils.PathUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

  @Override
  public Reader getReader(String templateName) {

    // add the prefix and ensure the prefix ends with a separator character
    StringBuilder path = new StringBuilder();
    if (this.getPrefix() != null) {

      path.append(this.getPrefix());

      if (!this.getPrefix().endsWith(String.valueOf(File.separatorChar))) {
        path.append(File.separatorChar);
      }
    }

    templateName = templateName + (this.getSuffix() == null ? "" : this.getSuffix());

    logger.debug("Looking for template in {}{}.", path.toString(), templateName);

    /*
     * if template name contains path segments, move those segments into the
     * path variable. The below technique needs to know the difference
     * between the path and file name.
     */
    String[] pathSegments = PathUtils.PATH_SEPARATOR_REGEX.split(templateName);

    if (pathSegments.length > 1) {
      // file name is the last segment
      templateName = pathSegments[pathSegments.length - 1];
    }
    for (int i = 0; i < (pathSegments.length - 1); i++) {
      path.append(pathSegments[i]).append(File.separatorChar);
    }

    // try to load File
    InputStream is = null;
    File file = new File(path.toString(), templateName);
    if (file.exists() && file.isFile()) {
      try {
        is = new FileInputStream(file);
      } catch (FileNotFoundException e) {
      }
    }

    if (is == null) {
      throw new LoaderException(null,
          "Could not find template \"" + path.toString() + templateName + "\"");
    }

    try {
      return new BufferedReader(new InputStreamReader(is, this.charset));
    } catch (UnsupportedEncodingException e) {
    }

    return null;
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
    this.prefix = prefix;
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
}
