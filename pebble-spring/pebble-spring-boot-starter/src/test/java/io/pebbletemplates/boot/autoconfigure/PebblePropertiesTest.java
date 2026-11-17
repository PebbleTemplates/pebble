package io.pebbletemplates.boot.autoconfigure;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.util.MimeType;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PebblePropertiesTest {

  @Test
  void exposesDefaultsAndConfiguredTemplateProperties() {
    PebbleProperties properties = new PebbleProperties();

    assertEquals(PebbleProperties.DEFAULT_PREFIX, properties.getPrefix());
    assertEquals(PebbleProperties.DEFAULT_SUFFIX, properties.getSuffix());
    assertEquals(StandardCharsets.UTF_8, properties.getCharset());
    assertEquals("UTF-8", properties.getCharsetName());
    assertTrue(properties.isCheckTemplateLocation());
    assertFalse(properties.isStrictVariables());
    assertFalse(properties.isGreedyMatchMethod());
    assertNull(properties.getDefaultLocale());
    assertNull(properties.getViewNames());
    assertNull(properties.getRequestContextAttribute());
    assertNull(properties.getAllowPackages());

    properties.setPrefix("views/");
    properties.setSuffix(".html");
    properties.setCharset(StandardCharsets.UTF_16);
    properties.setCheckTemplateLocation(false);
    properties.setStrictVariables(true);
    properties.setGreedyMatchMethod(true);
    properties.setDefaultLocale(Locale.CANADA_FRENCH);
    properties.setViewNames(new String[]{"home", "profile"});
    properties.setRequestContextAttribute("requestContext");
    properties.setAllowPackages(new String[]{"com.example"});

    assertEquals("views/", properties.getPrefix());
    assertEquals(".html", properties.getSuffix());
    assertEquals(StandardCharsets.UTF_16, properties.getCharset());
    assertEquals("UTF-16", properties.getCharsetName());
    assertFalse(properties.isCheckTemplateLocation());
    assertTrue(properties.isStrictVariables());
    assertTrue(properties.isGreedyMatchMethod());
    assertEquals(Locale.CANADA_FRENCH, properties.getDefaultLocale());
    assertArrayEquals(new String[]{"home", "profile"}, properties.getViewNames());
    assertEquals("requestContext", properties.getRequestContextAttribute());
    assertArrayEquals(new String[]{"com.example"}, properties.getAllowPackages());
  }

  @Test
  void servletContentTypeInheritsCharsetUnlessExplicitlySet() {
    PebbleProperties properties = new PebbleProperties();
    PebbleProperties.Servlet servlet = properties.getServlet();

    assertEquals("text/html;charset=UTF-8", servlet.getContentType().toString());
    assertEquals(StandardCharsets.UTF_8, servlet.getContentType().getCharset());
    properties.setCharset(StandardCharsets.UTF_16LE);
    assertEquals(StandardCharsets.UTF_16LE, servlet.getContentType().getCharset());

    servlet.setContentType(MimeType.valueOf("application/json;charset=US-ASCII"));
    assertEquals(StandardCharsets.US_ASCII, servlet.getContentType().getCharset());

    servlet.setContentType(MimeType.valueOf("application/json;profile=v1"));
    MimeType contentType = servlet.getContentType();
    assertEquals(StandardCharsets.UTF_16LE, contentType.getCharset());
    assertEquals("v1", contentType.getParameter("profile"));

    servlet.setContentType(null);
    assertNull(servlet.getContentType());
    assertNull(new PebbleProperties.Servlet().getContentType().getCharset());
  }

  @Test
  void exposesServletAndReactiveSettings() {
    PebbleProperties.Servlet servlet = new PebbleProperties().getServlet();
    assertFalse(servlet.isAllowRequestOverride());
    assertFalse(servlet.isAllowSessionOverride());
    assertTrue(servlet.isCache());
    assertFalse(servlet.isExposeRequestAttributes());
    assertFalse(servlet.isExposeSessionAttributes());
    assertTrue(servlet.isExposeSpringMacroHelpers());

    servlet.setAllowRequestOverride(true);
    servlet.setAllowSessionOverride(true);
    servlet.setCache(false);
    servlet.setExposeRequestAttributes(true);
    servlet.setExposeSessionAttributes(true);
    servlet.setExposeSpringMacroHelpers(false);
    assertTrue(servlet.isAllowRequestOverride());
    assertTrue(servlet.isAllowSessionOverride());
    assertFalse(servlet.isCache());
    assertTrue(servlet.isExposeRequestAttributes());
    assertTrue(servlet.isExposeSessionAttributes());
    assertFalse(servlet.isExposeSpringMacroHelpers());

    PebbleProperties.Reactive reactive = new PebbleProperties().getReactive();
    assertNull(reactive.getMediaTypes());
    List<MediaType> mediaTypes = Arrays.asList(MediaType.TEXT_HTML, MediaType.APPLICATION_JSON);
    reactive.setMediaTypes(mediaTypes);
    assertEquals(mediaTypes, reactive.getMediaTypes());
    reactive.setMediaTypes(null);
    assertNull(reactive.getMediaTypes());
  }
}
