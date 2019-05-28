package com.mitchellbosecke.pebble.boot;

import com.mitchellbosecke.pebble.PebbleEngine;
import java.io.StringWriter;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = NonWebApplication.class)
public class CoreTests {

  @Autowired
  private PebbleEngine pebbleEngine;

  @Test
  public void testOk() throws Exception {
    StringWriter sw = new StringWriter();
    pebbleEngine.getTemplate("hello").evaluate(sw);
    Assert.assertTrue(sw.toString() != null && !sw.toString().isEmpty());
  }

}
