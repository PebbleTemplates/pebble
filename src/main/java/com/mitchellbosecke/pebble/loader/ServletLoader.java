package com.mitchellbosecke.pebble.loader;

import com.mitchellbosecke.pebble.error.LoaderException;
import com.mitchellbosecke.pebble.utils.PathUtils;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import javax.servlet.ServletContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    // Add the prefix and make sure that it ends with a separator character
    StringBuilder path = new StringBuilder(128);
    if (getPrefix() != null) {

      path.append(getPrefix());

      // we do NOT use OS dependent separators here; getResourceAsStream
      // explicitly requires forward slashes.
      if (!getPrefix().endsWith(Character.toString(expectedSeparator))) {
        path.append(expectedSeparator);
      }
    }
    path.append(templateName);
    if (getSuffix() != null) {
      path.append(getSuffix());
    }
    String location = path.toString();
    logger.debug("Looking for template in {}.", location);

    is = context.getResourceAsStream(location);

    if (is == null) {
      throw new LoaderException(null, "Could not find template \"" + location + "\"");
    }

    try {
      isr = new InputStreamReader(is, charset);
      reader = new BufferedReader(isr);
    } catch (UnsupportedEncodingException e) {
    }

    return reader;
  }

  public String getSuffix() {
    return suffix;
  }

  @Override
  public void setSuffix(String suffix) {
    this.suffix = suffix;
  }

  public String getPrefix() {
    return prefix;
  }

  @Override
  public void setPrefix(String prefix) {
    this.prefix = prefix;
  }

  public String getCharset() {
    return charset;
  }

  @Override
  public void setCharset(String charset) {
    this.charset = charset;
  }

  @Override
  public String resolveRelativePath(String relativePath, String anchorPath) {
    return PathUtils.resolveRelativePath(relativePath, anchorPath, expectedSeparator);
  }

  @Override
  public String createCacheKey(String templateName) {
    return templateName;
  }

}
