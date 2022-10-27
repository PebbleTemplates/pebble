package io.pebbletemplates.pebble.macro;

import io.pebbletemplates.pebble.extension.Filter;
import io.pebbletemplates.pebble.template.EvaluationContext;
import io.pebbletemplates.pebble.template.PebbleTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class TestFilter implements Filter {

  static int counter = 0;

  public static final String FILTER_NAME = "testfilter";

  @Override
  public List<String> getArgumentNames() {
    return Collections.singletonList("content");
  }

  @Override
  public Object apply(Object input, Map<String, Object> args, PebbleTemplate self,
                      EvaluationContext context, int lineNumber) {
    String content = (String) input;
    counter++;
    content = content + "?" + "Hello";
    return content;
  }

  public static int getCounter() {
    return counter;
  }

}
