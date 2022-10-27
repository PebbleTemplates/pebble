package io.pebbletemplates.pebble.loader;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import javax.servlet.ServletContext;

/**
 * Loader that uses a servlet context to find templates.
 *
 * @author mbosecke
 * @author chkal
 */
public class ServletLoader extends AbstractServletLoader {

  private final ServletContext context;

  public ServletLoader(ServletContext context) {
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
