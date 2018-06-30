/*
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.mitchellbosecke.pebble.loader;

import java.io.Reader;
import java.io.StringReader;

/**
 * This loader is not intended to be used in a production system; it is primarily for testing and
 * debugging. Many tags do not work when using this loader, such as "extends", "imports",
 * "include".
 */
public class StringLoader implements Loader<String> {

  @Override
  public Reader getReader(String templateName) {
    return new StringReader(templateName);
  }

  @Override
  public void setPrefix(String prefix) {

  }

  @Override
  public void setSuffix(String suffix) {

  }

  @Override
  public void setCharset(String charset) {

  }

  @Override
  public String resolveRelativePath(String relativePath, String anchorPath) {
    return null;
  }

  @Override
  public String createCacheKey(String templateName) {
    return templateName;
  }

}
