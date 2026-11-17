package io.pebbletemplates.pebble.extension.i18n;

import org.junit.jupiter.api.Test;

import java.util.Locale;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class UTF8ControlTest {

  @Test
  void readsUtf8PropertiesWithAndWithoutReload() throws Exception {
    UTF8Control control = new UTF8Control();
    String baseName = "io.pebbletemplates.pebble.extension.i18n.UTF8ControlTest";
    ClassLoader loader = this.getClass().getClassLoader();

    for (boolean reload : new boolean[]{false, true}) {
      ResourceBundle bundle = control.newBundle(baseName, Locale.ROOT, "java.properties", loader,
          reload);
      assertEquals("Crème brûlée", bundle.getString("message"));
    }
  }

  @Test
  void returnsNullWhenPropertiesResourceDoesNotExist() throws Exception {
    UTF8Control control = new UTF8Control();
    String missing = "io.pebbletemplates.pebble.extension.i18n.MissingBundle";
    ClassLoader loader = this.getClass().getClassLoader();

    assertNull(control.newBundle(missing, Locale.ROOT, "java.properties", loader, false));
    assertNull(control.newBundle(missing, Locale.ROOT, "java.properties", loader, true));
  }
}
