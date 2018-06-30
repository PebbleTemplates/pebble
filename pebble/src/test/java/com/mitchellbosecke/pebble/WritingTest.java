/*
 * This file is part of Pebble.
 * <p>
 * Copyright (c) 2014 by Mitchell Bösecke
 * <p>
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.mitchellbosecke.pebble;

import static org.junit.Assert.assertEquals;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.loader.StringLoader;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import org.junit.Test;

public class WritingTest {

  /**
   * There was an issue where the pebble engine was closing the provided writer. This is wrong.
   */
  @Test
  public void testMultipleEvaluationsWithOneWriter() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader()).build();

    PebbleTemplate template1 = pebble.getTemplate("first");
    PebbleTemplate template2 = pebble.getTemplate("second");

    Writer writer = new UncloseableWriter();
    template1.evaluate(writer);
    template2.evaluate(writer);

    assertEquals("firstsecond", writer.toString());
  }

  public class UncloseableWriter extends StringWriter {

    @Override
    public void close() {
      throw new RuntimeException("Can not close this writer.");
    }
  }

  /**
   * The following test used to fail because one parallel thread would rewrite the contents of
   * another parallel thread's character buffer.
   */
  @Test
  public void testParallelCharacterBuffersBeingOverriden() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .executorService(Executors.newCachedThreadPool()).build();
    String source = "beginning {% parallel %}{{ slowObject.first }}{% endparallel %} middle {% parallel %}{{ slowObject.second }}{% endparallel %} end {% parallel %}{{ slowObject.third }}{% endparallel %}";
    PebbleTemplate template = pebble.getTemplate(source);
    for (int i = 0; i < 2; i++) {
      Writer writer = new StringWriter();
      Map<String, Object> context = new HashMap<>();
      context.put("slowObject", new SlowObject());
      template.evaluate(writer, context);

      assertEquals("beginning first middle second end third", writer.toString());
    }
  }

  public class SlowObject {

    public String first() {
      try {
        Thread.sleep(200);
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      return "first";
    }

    public String second() {
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      return "second";
    }

    public String third() {
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      return "third";
    }
  }

}
