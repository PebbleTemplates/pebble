package com.mitchellbosecke.pebble;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.loader.StringLoader;
import com.mitchellbosecke.pebble.node.ArgumentsNode;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import java.io.StringWriter;
import java.io.Writer;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests {@link ArgumentsNode}.
 */
public class ArgumentsNodeTest {

  /**
   * Tests that the error description is clear when a invalid number of arguments are provided.
   */
  @Test
  public void testInvalidArgument() throws Exception {

    try {
      PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
          .strictVariables(false).build();

      PebbleTemplate template = pebble
          .getTemplate("{{ 'This is a test of the abbreviate filter' | abbreviate(16, 10) }}");
      Writer writer = new StringWriter();
      template.evaluate(writer);
      Assert.fail("Should not be reached, because an exception is expected.");
    } catch (PebbleException e) {
      Assert.assertEquals("{{ 'This is a test of the abbreviate filter' | abbreviate(16, 10) }}",
          e.getFileName());
      Assert.assertEquals((Integer) 1, e.getLineNumber());
    }

  }


}
