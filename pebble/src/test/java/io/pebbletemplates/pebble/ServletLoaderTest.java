package io.pebbletemplates.pebble;

import io.pebbletemplates.pebble.error.LoaderException;
import io.pebbletemplates.pebble.loader.Loader;
import io.pebbletemplates.pebble.loader.Servlet5Loader;
import io.pebbletemplates.pebble.loader.ServletLoader;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ServletLoaderTest {

  private static final String TEMPLATE_LOCATION = "/templates/greeting.peb";
  private static final String TEMPLATE_CONTENT = "Hello from servlet";

  @Test
  void legacyServletLoaderReadsAndChecksResources() throws Exception {
    assertLoaderBehavior(new ServletLoader(
        (javax.servlet.ServletContext) servletContext(javax.servlet.ServletContext.class)));
  }

  @Test
  void jakartaServletLoaderReadsAndChecksResources() throws Exception {
    assertLoaderBehavior(new Servlet5Loader(
        (jakarta.servlet.ServletContext) servletContext(jakarta.servlet.ServletContext.class)));
  }

  private void assertLoaderBehavior(Loader<String> loader) throws Exception {
    loader.setPrefix("/templates");
    loader.setSuffix(".peb");

    Reader reader = loader.getReader("greeting");
    assertEquals(TEMPLATE_CONTENT, new BufferedReader(reader).readLine());
    assertTrue(loader.resourceExists("greeting"));

    assertThrows(LoaderException.class, () -> loader.getReader("missing"));
    assertFalse(loader.resourceExists("missing"));
  }

  private static Object servletContext(Class<?> servletContextType) {
    return Proxy.newProxyInstance(servletContextType.getClassLoader(),
        new Class<?>[]{servletContextType}, (proxy, method, args) -> {
          String location = args == null || args.length == 0 ? null : (String) args[0];
          if ("getResourceAsStream".equals(method.getName())) {
            return getResourceAsStream(location);
          }
          if ("getResource".equals(method.getName())) {
            return getResource(location);
          }
          if ("toString".equals(method.getName())) {
            return "TestServletContext";
          }
          if ("hashCode".equals(method.getName())) {
            return System.identityHashCode(proxy);
          }
          if ("equals".equals(method.getName())) {
            return proxy == args[0];
          }
          return null;
        });
  }

  private static InputStream getResourceAsStream(String location) {
    if (TEMPLATE_LOCATION.equals(location)) {
      return new ByteArrayInputStream(TEMPLATE_CONTENT.getBytes(StandardCharsets.UTF_8));
    }
    return null;
  }

  private static URL getResource(String location) throws IOException {
    if (TEMPLATE_LOCATION.equals(location)) {
      return new URL("file:" + TEMPLATE_LOCATION);
    }
    return null;
  }
}
