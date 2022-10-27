package io.pebbletemplates.pebble.loader;

import jakarta.servlet.ServletContext;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Loader that uses a servlet context to find templates. Requires Jakarta Servlet 5.0 or newer.
 *
 * @author mbosecke
 * @author chkal
 */
public class Servlet5Loader extends AbstractServletLoader {

  private final ServletContext context;

  public Servlet5Loader(ServletContext context) {
    this.context = context;
  }

  @Override
  protected InputStream getResourceAsStream(String location) {
    return context.getResourceAsStream(location);
  }

  @Override
  protected URL getResource(String location) throws MalformedURLException {
    return context.getResource(location);
  }
}
