package io.pebbletemplates.pebble.loader;

import io.pebbletemplates.pebble.error.LoaderException;
import io.pebbletemplates.pebble.utils.PathUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract base class for loaders which user the servlet context to load templates.
 *
 * @author mbosecke
 * @author chkal
 */
public abstract class AbstractServletLoader implements Loader<String> {

  private static final Logger logger = LoggerFactory.getLogger(AbstractServletLoader.class);

  private String prefix;

  private String suffix;

  private String charset = "UTF-8";

  private char expectedSeparator = '/';

  protected abstract InputStream getResourceAsStream(String location);

  protected abstract URL getResource(String location) throws MalformedURLException;

  @Override
  public Reader getReader(String templateName) {

    InputStreamReader isr = null;
    Reader reader = null;

    InputStream is = null;
    String location = this.getLocation(templateName);

    logger.debug("Looking for template in {}.", location);

    is = getResourceAsStream(location);

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
      return getResource(this.getLocation(templateName)) != null;
    } catch (MalformedURLException e) {
      return false;
    }
  }
}
