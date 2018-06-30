/*
 * This file is part of Pebble.
 * <p>
 * Copyright (c) 2014 by Mitchell Bösecke
 * <p>
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.mitchellbosecke.pebble.loader;

import com.mitchellbosecke.pebble.error.LoaderException;
import com.mitchellbosecke.pebble.utils.PathUtils;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Uses a classloader to find templates located on the classpath.
 *
 * @author mbosecke
 */
public class ClasspathLoader implements Loader<String> {

  private static final Logger logger = LoggerFactory.getLogger(ClasspathLoader.class);

  private String prefix;

  private String suffix;

  private String charset = "UTF-8";

  private char expectedSeparator = '/';

  private final ClassLoader rcl;

  public ClasspathLoader(ClassLoader classLoader) {
    this.rcl = classLoader;
  }

  public ClasspathLoader() {
    this(ClasspathLoader.class.getClassLoader());
  }

  @Override
  public Reader getReader(String templateName) {

    // append the prefix and make sure prefix ends with a separator character
    StringBuilder path = new StringBuilder(128);
    if (this.getPrefix() != null) {

      path.append(this.getPrefix());

      // we do NOT use OS dependent separators here; getResourceAsStream
      // explicitly requires forward slashes.
      if (!this.getPrefix().endsWith(Character.toString(this.expectedSeparator))) {
        path.append(this.expectedSeparator);
      }
    }
    path.append(templateName);
    if (this.getSuffix() != null) {
      path.append(this.getSuffix());
    }
    String location = path.toString();
    logger.debug("Looking for template in {}.", location);

    // perform the lookup
    InputStream is = this.rcl.getResourceAsStream(location);

    if (is == null) {
      throw new LoaderException(null, "Could not find template \"" + location + "\"");
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
    return PathUtils.resolveRelativePath(relativePath, anchorPath, this.expectedSeparator);
  }

  @Override
  public String createCacheKey(String templateName) {
    return templateName;
  }
}
