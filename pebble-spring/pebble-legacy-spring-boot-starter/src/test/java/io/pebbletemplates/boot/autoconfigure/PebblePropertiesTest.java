package io.pebbletemplates.boot.autoconfigure;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
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
    assertTrue(properties.isCache());
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
    properties.setCache(false);
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
    assertFalse(properties.isCache());
    assertTrue(properties.isStrictVariables());
    assertTrue(properties.isGreedyMatchMethod());
    assertEquals(Locale.CANADA_FRENCH, properties.getDefaultLocale());
    assertArrayEquals(new String[]{"home", "profile"}, properties.getViewNames());
    assertEquals("requestContext", properties.getRequestContextAttribute());
    assertArrayEquals(new String[]{"com.example"}, properties.getAllowPackages());
  }

}
