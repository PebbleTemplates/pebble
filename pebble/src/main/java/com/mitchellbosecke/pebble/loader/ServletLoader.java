package com.mitchellbosecke.pebble.loader;

import com.mitchellbosecke.pebble.error.LoaderException;
import com.mitchellbosecke.pebble.utils.PathUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;

import javax.servlet.ServletContext;

/**
 * Loader that uses a servlet context to find templates.
 *
 * @author mbosecke
 */
public class ServletLoader implements Loader<String> {

  private static final Logger logger = LoggerFactory.getLogger(ServletLoader.class);

  private String prefix;

  private String suffix;

  private String charset = "UTF-8";

  private char expectedSeparator = '/';

  private final ServletContext context;

  public ServletLoader(ServletContext context) {
    this.context = context;
  }

  @Override
  public Reader getReader(String templateName) {

    InputStreamReader isr = null;
    Reader reader = null;

    InputStream is = null;
    String location = this.getLocation(templateName);

    logger.debug("Looking for template in {}.", location);

    is = this.context.getResourceAsStream(location);

    if (is == null) {
      throw new LoaderException(null, "Could not find template \"" + location + "\"");
    }

    try {
      isr = new InputStreamReader(is, this.charset);
      reader = new BufferedReader(isr);
    } catch (UnsupportedEncodingException e) {
    }

    return reader;
  }

  private String getLocation(String templateName) {
    // Add the prefix and make sure that it ends with a separator character
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
    return path.toString();
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

  @Override
  public boolean resourceExists(String templateName) {
    try {
      return this.context.getResource(this.getLocation(templateName)) != null;
    } catch (MalformedURLException e) {
      return false;
    }
  }
}
