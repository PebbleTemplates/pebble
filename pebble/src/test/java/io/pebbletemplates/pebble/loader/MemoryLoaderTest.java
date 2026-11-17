package io.pebbletemplates.pebble.loader;

import io.pebbletemplates.pebble.error.LoaderException;
import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MemoryLoaderTest {

  @Test
  void readsKnownTemplatesAndReportsMissingOrEmptyTemplates() throws Exception {
    MemoryLoader loader = new MemoryLoader();
    loader.addTemplate("known", "contents");
    loader.addTemplate("empty", "");

    try (Reader reader = loader.getReader("known")) {
      StringWriter writer = new StringWriter();
      char[] buffer = new char[16];
      int length;
      while ((length = reader.read(buffer)) != -1) {
        writer.write(buffer, 0, length);
      }
      assertEquals("contents", writer.toString());
    }

    assertThrows(LoaderException.class, () -> loader.getReader("missing"));
    assertThrows(LoaderException.class, () -> loader.getReader("empty"));
  }

  @Test
  void exposesTemplateMetadataAndFlatLoaderBehavior() throws Exception {
    MemoryLoader loader = new MemoryLoader();
    loader.addTemplate("page", "first");
    loader.addTemplate("page", "second");
    loader.setPrefix("ignored");
    loader.setSuffix("ignored");
    loader.setCharset("ignored");

    assertEquals("page", loader.createCacheKey("page"));
    assertEquals("relative", loader.resolveRelativePath("relative", "anchor"));
    assertTrue(loader.resourceExists("page"));
    assertFalse(loader.resourceExists("missing"));
    assertEquals(2, loader.getTemplates().size());
    assertEquals("first", loader.getTemplates().get(0).content);
    try (Reader reader = loader.getReader("page")) {
      StringWriter writer = new StringWriter();
      char[] buffer = new char[16];
      int length;
      while ((length = reader.read(buffer)) != -1) {
        writer.write(buffer, 0, length);
      }
      assertEquals("first", writer.toString());
    }
  }

  @Test
  void rejectsNullTemplateNamesAndContents() {
    MemoryLoader loader = new MemoryLoader();

    assertThrows(IllegalArgumentException.class, () -> loader.addTemplate(null, "contents"));
    assertThrows(IllegalArgumentException.class, () -> loader.addTemplate("name", null));
  }
}
